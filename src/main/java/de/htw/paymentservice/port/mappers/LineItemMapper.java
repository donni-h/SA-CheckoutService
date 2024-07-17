package de.htw.paymentservice.port.mappers;

import com.stripe.model.LineItem;
import com.stripe.model.Price;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.core.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LineItemMapper {

    public OrderItem map(LineItem lineItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductName(lineItem.getDescription());
        orderItem.setPrice(lineItem.getPrice().getUnitAmountDecimal());
        return orderItem;
    }
}
