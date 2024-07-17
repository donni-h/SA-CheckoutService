package de.htw.paymentservice.core.domain.service.interfaces;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.port.dto.ItemDTO;

import java.util.List;

public interface IOrderService {
    Order createOrder(Session session, List<ItemDTO> items) throws StripeException;
    Order findOrderBySessionId(String sessionId);

}
