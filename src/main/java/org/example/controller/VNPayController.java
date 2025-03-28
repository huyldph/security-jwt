package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.VNPayCreatePaymentRequest;
import org.example.dto.response.VNPayResponse;
import org.example.service.VNPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vnpay")
@CrossOrigin(origins = "*")
public class VNPayController {
    private final VNPayService vnPayService;

    @PostMapping("/create-payment")
    public ResponseEntity<VNPayResponse> createPayment(@Valid @RequestBody VNPayCreatePaymentRequest request) throws UnsupportedEncodingException {
        return ResponseEntity.ok(vnPayService.createPayment(request));
    }

    @GetMapping("/payment-callback")
    public ResponseEntity<VNPayResponse> paymentCallback(@RequestParam Map<String, String> queryParams) {
        return ResponseEntity.ok(vnPayService.processPaymentReturn(queryParams));
    }
} 