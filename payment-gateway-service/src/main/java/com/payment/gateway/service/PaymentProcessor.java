package com.payment.gateway.service;

import com.payment.gateway.enums.Currency;
import com.payment.gateway.enums.TransactionStatus;
import com.payment.gateway.event.EventPublisher;
import com.payment.gateway.model.PaymentRequest;
import com.payment.gateway.model.PaymentResponse;
import com.payment.gateway.model.Transaction;
import com.payment.gateway.model.User;
import com.payment.gateway.repository.TransactionRepository;
import com.payment.gateway.utils.TransactionLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Service
public class PaymentProcessor {

    @Autowired
    private CurrencyConverter currencyConverter;

    @Autowired
    private BankingSystemIntegration bankingSystemIntegration;

    @Autowired
    private ThirdPartyProviderIntegration thirdPartyProviderIntegration;

    @Autowired
    private TransactionLogger transactionLogger;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EventPublisher eventPublisher;

    /**
     * Processes a payment request by validating, converting currency, and communicating with external systems.
     *
     * @param request The payment request containing all necessary transaction details.
     * @return The response indicating the transaction status and confirmation details.
     */
    public PaymentResponse processPayment(PaymentRequest request) {

        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        if (!request.getCurrency().equals(Currency.INR)) {
            try {
                amount = currencyConverter.convert(amount, request.getCurrency(), Currency.INR);
                request.setCurrency(Currency.INR);
                request.setAmount(amount.longValue());
            } catch (Exception e) {
                return new PaymentResponse(TransactionStatus.FAILURE, "Currency conversion failed: " + e.getMessage());
            }
        }
        TransactionStatus status;
        try {
            switch (request.getPaymentMethod()) {
                case CREDIT_CARD:
                    status = bankingSystemIntegration.processCreditCardPayment(amount, request);
                    break;
                case PAYPAL:
                    status = thirdPartyProviderIntegration.processPayPalPayment(amount, request);
                    break;
                case BANK_TRANSFER:
                    status = bankingSystemIntegration.processBankTransfer(amount, request);
                    break;
                default:
                    return new PaymentResponse(TransactionStatus.FAILURE, "Unsupported payment method.");
            }
            User user = userService.getUserByAccountId(request.getAccountId());

            PaymentResponse response = paymentService.processPayment(user, request);

            Transaction transaction = new Transaction();
            transaction.setTransactionId(request.getTransactionId());
            transaction.setAmount(request.getAmount());
            transaction.setCurrency(request.getCurrency().name());
            transaction.setTransactionType(request.getPaymentMethod().name());
            transaction.setAccountId(request.getAccountId());
            transaction.setStatus(response.getStatus().name());
            transaction.setConfirmationDetails(response.getConfirmationDetails());
            transaction.setMerchant(request.getMerchantId());
            transaction.setTimestamp(Timestamp.from(Instant.now()).getTime());

            if (response.getStatus().equals(TransactionStatus.SUCCESS)) {
                user.setBalance(user.getBalance() - request.getAmount());
                userService.updateUser(user);
                eventPublisher.publishTransaction(transaction);
            }


            transactionRepository.saveTransaction(transaction);

            // Step 4: Log the transaction
            transactionLogger.logTransaction(request, response);

            // Step 5: Return response
            if (status == TransactionStatus.SUCCESS) {
                return new PaymentResponse(TransactionStatus.SUCCESS, response.getConfirmationDetails());
            } else {
                return new PaymentResponse(TransactionStatus.FAILURE, response.getConfirmationDetails());
            }

        } catch (Exception e) {
            return new PaymentResponse(TransactionStatus.FAILURE, "Error processing payment: " + e.getMessage());
        }
    }
}
