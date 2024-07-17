package de.htw.paymentservice.core.domain.service.interfaces;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.port.dto.ItemDTO;

import java.util.List;

public interface IOrderService {
    Order createOrder(Session session, List<ItemDTO> items, String username) throws StripeException;
    Order findOrderBySessionId(String sessionId);
    void notifyCheckoutStatus(String sessionId) throws StripeException;
    void deleteOrder(Order order);
}
