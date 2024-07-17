package de.htw.paymentservice.port.user.exception;

public class OrderSessionIdNotFoundException extends RuntimeException{
    public OrderSessionIdNotFoundException(String sessionId){
        super("Order with sessionId: " + sessionId + " could not be found.");
    }
}
