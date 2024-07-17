package de.htw.paymentservice.port.mappers;


import com.stripe.param.checkout.SessionCreateParams;
import de.htw.paymentservice.core.domain.model.OrderItem;
import de.htw.paymentservice.port.dto.ItemDTO;

public class ItemDTOMapper {

    public static SessionCreateParams.LineItem mapToLineItem(ItemDTO itemDTO) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("eur")
                        .setUnitAmount(itemDTO.getItemPrice().movePointRight(2).longValue())
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(itemDTO.getName())
                                .build())
                        .build())
                .build();
    }

    public static OrderItem mapToOrderItem(ItemDTO itemDTO) {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(itemDTO.getItemPrice());
        orderItem.setProductName(itemDTO.getName());
        orderItem.setItemId(itemDTO.getPlantId());
        return orderItem;
    }
}
