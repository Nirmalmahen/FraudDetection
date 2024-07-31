package com.fraud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @NotBlank(message = "Transaction ID is mandatory")
    private String transactionId;

    @NotNull(message = "Amount is mandatory")
    @Min(value = 0, message = "Amount must be greater than or equal to 0")
    private Double amount;

    @NotNull(message = "Timestamp is mandatory")
    private Long timestamp;

    @NotBlank(message = "Account ID is mandatory")
    private String accountId;

    @NotBlank(message = "Transaction type is mandatory")
    private String transactionType;

    @NotBlank(message = "Merchant is mandatory")
    private String merchant;

}
