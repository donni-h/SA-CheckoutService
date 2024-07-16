package de.htw.paymentservice;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderRequest {
    // Getters and setters
    private String orderId;
    private long quantity;
    private long amount; // Amount in the smallest currency unit (e.g., cents for USD)

    // Default constructor
    public OrderRequest() {}

    // Parameterized constructor
    public OrderRequest(String orderId, long quantity, long amount) {
        this.orderId = orderId;
        this.quantity = quantity;
        this.amount = amount;
    }

}
