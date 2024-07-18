package de.htw.paymentservice.core.domain.service.interfaces;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.port.dto.ItemDTO;
import de.htw.paymentservice.port.user.exception.OrderIdNotFoundException;
import de.htw.paymentservice.port.user.exception.OrderSessionIdNotFoundException;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    Order createOrder(Session session, List<ItemDTO> items, String username);
    Order findOrderBySessionId(String sessionId) throws OrderSessionIdNotFoundException;
    void notifyCheckoutStatus(String sessionId) throws StripeException, OrderSessionIdNotFoundException;
    void deleteOrder(UUID orderId) throws OrderIdNotFoundException;
    Order getOrderById(UUID orderId) throws OrderIdNotFoundException;
    List<Order> getAllOrdersForUser(String username);
    void deleteAllOrders();
    Order updateOrderStatus(Order order) throws StripeException;
}
