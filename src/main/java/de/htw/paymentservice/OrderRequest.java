package de.htw.paymentservice;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderRequest {

    private String orderId;
    private long quantity;
    private long amount;

    public OrderRequest(String orderId, long quantity, long amount) {
        this.orderId = orderId;
        this.quantity = quantity;
        this.amount = amount;
    }

}
