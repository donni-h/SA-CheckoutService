package de.htw.paymentservice.core.domain.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.LineItem;
import com.stripe.model.LineItemCollection;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.core.domain.model.Metadata;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.core.domain.model.OrderItem;
import de.htw.paymentservice.core.domain.service.interfaces.IOrderRepository;
import de.htw.paymentservice.core.domain.service.interfaces.IOrderService;
import de.htw.paymentservice.port.dto.BasketDTO;
import de.htw.paymentservice.port.dto.ItemDTO;
import de.htw.paymentservice.port.mappers.ItemDTOMapper;
import de.htw.paymentservice.port.mappers.LineItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;

    @Autowired
    private StripeService stripeService;
    @Autowired
    private LineItemMapper lineItemToOrderItemMapper;


    @Autowired
    public OrderService(IOrderRepository repository) {
        this.orderRepository = repository;
    }

    @Override
    public Order createOrder(Session session, List<ItemDTO> items) throws StripeException {

        Order order = new Order();
        Metadata metadata = new Metadata(order, session.getStatus(), session.getId());
        order.setMetadata(metadata);

        List<OrderItem> orderItems = items.stream()
                        .map(ItemDTOMapper::mapToOrderItem)
                                .collect(Collectors.toList());

        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        order.setItems(orderItems);

        return orderRepository.save(order);
    }

    @Override
    public Order findOrderBySessionId(String sessionId) {
        Order order = orderRepository.findOrderBySessionId(sessionId).orElseThrow(() -> new RuntimeException("Order not found"));
        return order;
    }
}
