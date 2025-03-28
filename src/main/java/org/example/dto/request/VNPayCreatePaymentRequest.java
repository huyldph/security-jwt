package org.example.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VNPayCreatePaymentRequest {
    @NotNull(message = "Amount is required")
    @Min(value = 1000, message = "Minimum amount is 1000 VND")
    private Long amount;

    @NotBlank(message = "Bank code is required")
    private String bankCode;

    private String orderInfo;
    
    @NotBlank(message = "Order type is required")
    private String orderType;
    
    private String language;
    
    @NotBlank(message = "Return URL is required")
    private String returnUrl;
} 