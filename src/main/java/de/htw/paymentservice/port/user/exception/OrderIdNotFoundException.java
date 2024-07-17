package de.htw.paymentservice.port.user.exception;

import java.util.UUID;

public class OrderIdNotFoundException extends RuntimeException{
    public OrderIdNotFoundException(UUID orderId){
        super("Order with id: " + orderId + " could not be found.");
    }
}
