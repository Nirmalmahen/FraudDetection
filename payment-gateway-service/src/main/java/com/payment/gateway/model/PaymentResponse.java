package com.payment.gateway.model;

import com.payment.gateway.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private TransactionStatus status;
    private String confirmationDetails;

}
