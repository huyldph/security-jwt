package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.configuration.VNPayConfig;
import org.example.dto.request.VNPayCreatePaymentRequest;
import org.example.dto.response.VNPayResponse;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {
    private final VNPayConfig vnPayConfig;

    public VNPayResponse createPayment(VNPayCreatePaymentRequest request) throws UnsupportedEncodingException {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnPayConfig.getVersion());
        vnp_Params.put("vnp_Command", vnPayConfig.getCommand());
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(request.getAmount() * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        
        String vnp_TxnRef = getRandomNumber(8);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        
        vnp_Params.put("vnp_OrderInfo", request.getOrderInfo() != null ? request.getOrderInfo() : "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", request.getOrderType());
        vnp_Params.put("vnp_Locale", request.getLanguage() != null ? request.getLanguage() : "vn");
        vnp_Params.put("vnp_ReturnUrl", request.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
            vnp_Params.put("vnp_BankCode", request.getBankCode());
        }

        // Build data to hash and query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = vnPayConfig.getPayUrl() + "?" + queryUrl;

        return VNPayResponse.builder()
                .status("00")
                .message("success")
                .url(paymentUrl)
                .build();
    }

    public VNPayResponse processPaymentReturn(Map<String, String> queryParams) {
        String vnp_SecureHash = queryParams.get("vnp_SecureHash");
        
        if (vnp_SecureHash != null) {
            // Remove hash from params
            Map<String, String> vnp_Params = new HashMap<>(queryParams);
            vnp_Params.remove("vnp_SecureHash");
            vnp_Params.remove("vnp_SecureHashType");
            
            // Create hash data
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }

            String signValue = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            if (signValue.equals(vnp_SecureHash)) {
                String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
                return VNPayResponse.builder()
                        .status(vnp_ResponseCode)
                        .message(getResponseMessage(vnp_ResponseCode))
                        .orderInfo(queryParams.get("vnp_OrderInfo"))
                        .amount(Long.parseLong(queryParams.get("vnp_Amount")) / 100)
                        .transactionId(queryParams.get("vnp_TransactionNo"))
                        .bankCode(queryParams.get("vnp_BankCode"))
                        .paymentTime(queryParams.get("vnp_PayDate"))
                        .transactionStatus(vnp_ResponseCode)
                        .build();
            }
            return VNPayResponse.builder()
                    .status("97")
                    .message("Invalid signature")
                    .build();
        }
        return VNPayResponse.builder()
                .status("99")
                .message("Invalid request")
                .build();
    }

    private String getResponseMessage(String responseCode) {
        switch (responseCode) {
            case "00":
                return "Giao dịch thành công";
            case "07":
                return "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
            case "09":
                return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.";
            case "10":
                return "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11":
                return "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "12":
                return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.";
            case "13":
                return "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "24":
                return "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "51":
                return "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.";
            case "65":
                return "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.";
            case "75":
                return "Ngân hàng thanh toán đang bảo trì.";
            case "79":
                return "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch";
            case "99":
                return "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)";
            default:
                return "Giao dịch thất bại";
        }
    }

    private String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            return "";
        }
    }

    private String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
} 