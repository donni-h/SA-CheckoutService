package de.htw.paymentservice.unitTests;

import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.core.domain.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderItemTest {

    @Mock
    private Order mockOrder;

    private OrderItem orderItemUnderTest;

    @BeforeEach
    void setUp() {
        orderItemUnderTest = new OrderItem(UUID.fromString("dae22a33-b280-4eec-909c-8bfb41111fed"), "productName",
                UUID.fromString("f36df43f-cd73-4ff5-aa05-c3eaa87bb30a"), new BigDecimal("0.00"), mockOrder);
    }


    @Test
    void testIdGetterAndSetter() {
        final UUID id = UUID.fromString("dae22a33-b280-4eec-909c-8bfb41111fed");
        orderItemUnderTest.setId(id);
        assertThat(orderItemUnderTest.getId()).isEqualTo(id);
    }

    @Test
    void testProductNameGetterAndSetter() {
        final String productName = "productName";
        orderItemUnderTest.setProductName(productName);
        assertThat(orderItemUnderTest.getProductName()).isEqualTo(productName);
    }

    @Test
    void testItemIdGetterAndSetter() {
        final UUID itemId = UUID.fromString("f36df43f-cd73-4ff5-aa05-c3eaa87bb30a");
        orderItemUnderTest.setItemId(itemId);
        assertThat(orderItemUnderTest.getItemId()).isEqualTo(itemId);
    }

    @Test
    void testPriceGetterAndSetter() {
        final BigDecimal price = new BigDecimal("0.00");
        orderItemUnderTest.setPrice(price);
        assertThat(orderItemUnderTest.getPrice()).isEqualTo(price);
    }

    @Test
    void testOrderGetterAndSetter() {
        final Order order = new Order();
        orderItemUnderTest.setOrder(order);
        assertThat(orderItemUnderTest.getOrder()).isEqualTo(order);
    }

    @Test
    void testEquals() {
        // Run the test
        final boolean result = orderItemUnderTest.equals("o");

        // Verify the results
        assertThat(result).isFalse();
    }
}
