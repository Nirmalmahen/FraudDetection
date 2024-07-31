package com.payment.gateway.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;
    private String transactionId;
    private Double amount;
    private Long timestamp;
    private String accountId;
    private String currency;
    private String transactionType;
    private String merchant;
    private String status;
    private String confirmationDetails;

}
