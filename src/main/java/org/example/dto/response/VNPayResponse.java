package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayResponse {
    private String status;
    private String message;
    private String url;
    private String orderInfo;
    private Long amount;
    private String transactionId;
    private String bankCode;
    private String paymentTime;
    private String transactionStatus;
} 