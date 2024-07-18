package de.htw.paymentservice.core.domain.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.core.domain.model.Metadata;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.core.domain.model.OrderItem;
import de.htw.paymentservice.core.domain.service.interfaces.IOrderRepository;
import de.htw.paymentservice.core.domain.service.interfaces.IOrderService;
import de.htw.paymentservice.port.dto.ItemDTO;
import de.htw.paymentservice.port.mappers.ItemDTOMapper;
import de.htw.paymentservice.port.mappers.LineItemMapper;
import de.htw.paymentservice.port.producer.CheckoutProducer;
import de.htw.paymentservice.port.user.exception.OrderIdNotFoundException;
import de.htw.paymentservice.port.user.exception.OrderSessionIdNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;
    private final CheckoutProducer checkoutProducer;

    @Autowired
    private StripeService stripeService;
    @Autowired
    private LineItemMapper lineItemToOrderItemMapper;


    @Autowired
    public OrderService(IOrderRepository repository, CheckoutProducer checkoutProducer) {
        this.orderRepository = repository;
        this.checkoutProducer = checkoutProducer;
    }

    @Override
    public Order createOrder(Session session, List<ItemDTO> items, String username){

        Order order = new Order();
        Metadata metadata = new Metadata(order, session.getStatus(), session.getId(), username);
        order.setMetadata(metadata);

        List<OrderItem> orderItems = items.stream()
                        .map(ItemDTOMapper::mapToOrderItem)
                                .collect(Collectors.toList());

        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        order.setItems(orderItems);

        return orderRepository.save(order);
    }

    @Override
    public Order findOrderBySessionId(String sessionId) throws OrderSessionIdNotFoundException{
        return orderRepository.findOrderBySessionId(sessionId)
                .orElseThrow(() -> new OrderSessionIdNotFoundException(sessionId));
    }

    @Override
    public void notifyCheckoutStatus(String sessionId) throws StripeException, OrderSessionIdNotFoundException {
        Order order = findOrderBySessionId(sessionId);
        String status = stripeService.retrieveCheckoutStatus(sessionId);
        checkoutProducer.notifyOrderResult(order.getMetadata().getUsername(), status);
    }

    @Override
    public void deleteOrder(UUID orderId) throws OrderIdNotFoundException{
        if(!orderRepository.existsById(orderId)) throw new OrderIdNotFoundException(orderId);
        orderRepository.deleteById(orderId);
    }

    @Override
    public Order getOrderById(UUID orderId) throws OrderIdNotFoundException{
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderIdNotFoundException(orderId));
    }

    @Override
    public List<Order> getAllOrdersForUser(String username){
        return orderRepository.findOrdersByUsername(username);
    }

    @Override
    public void deleteAllOrders(){
        orderRepository.deleteAll();
    }

    public Order updateOrderStatus(Order order) throws StripeException {
        String session = order.getMetadata().getSessionId();
        String status = stripeService.retrieveCheckoutStatus(session);
        order.getMetadata().setStatus(status);
        return orderRepository.save(order);
    }
}
