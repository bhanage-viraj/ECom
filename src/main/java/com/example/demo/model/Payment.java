package com.example.ecommerce.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payments")
public class Payment {

    @Id
    private String id;
    private String orderId;
    private double amount;
    private String status;
    private String paymentId;
    private Instant createdAt;

    public Payment() {}

    public Payment(String orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = "PENDING";
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getPaymentId() { return paymentId; }

    public void setStatus(String status) { this.status = status; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}
