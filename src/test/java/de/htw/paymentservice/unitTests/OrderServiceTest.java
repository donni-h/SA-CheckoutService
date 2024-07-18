package de.htw.paymentservice.unitTests;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.core.domain.model.Metadata;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.core.domain.model.OrderItem;
import de.htw.paymentservice.core.domain.service.impl.OrderService;
import de.htw.paymentservice.core.domain.service.impl.StripeService;
import de.htw.paymentservice.core.domain.service.interfaces.IOrderRepository;
import de.htw.paymentservice.port.dto.ItemDTO;
import de.htw.paymentservice.port.mappers.LineItemMapper;
import de.htw.paymentservice.port.producer.CheckoutProducer;
import de.htw.paymentservice.port.user.exception.OrderIdNotFoundException;
import de.htw.paymentservice.port.user.exception.OrderSessionIdNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private IOrderRepository mockRepository;
    @Mock
    private CheckoutProducer mockCheckoutProducer;
    @Mock
    private StripeService mockStripeService;
    @Mock
    private LineItemMapper mockLineItemToOrderItemMapper;
    private OrderService orderServiceUnderTest;

    @BeforeEach
    void setUp() {
        orderServiceUnderTest = new OrderService(mockRepository, mockCheckoutProducer);
        ReflectionTestUtils.setField(orderServiceUnderTest, "stripeService", mockStripeService);
        ReflectionTestUtils.setField(orderServiceUnderTest, "lineItemToOrderItemMapper", mockLineItemToOrderItemMapper);
    }

    @Test
    void testCreateOrder() {
        final Session session = new Session();
        session.setCustomer("customer");
        session.setInvoice("invoice");
        session.setPaymentIntent("paymentIntent");
        session.setId("sessionId");
        session.setStatus("status");
        final List<OrderItem> orderItems = new ArrayList<OrderItem>();
        final List<ItemDTO> itemDTOs = new ArrayList<ItemDTO>();
        final Date date = new Date();
        final Metadata metadata = new Metadata("Username", "status", "sessionId", date);
        Order order = new Order(orderItems, metadata);


        when(mockRepository.save(any(Order.class))).thenReturn(order);

        final Order result = orderServiceUnderTest.createOrder(session, itemDTOs, "username");

        // Verify the results
        assertThat(result).isEqualTo(order);
    }

    @Test
    void testFindOrderBySessionId_IOrderRepositoryReturnsAbsent() {
        // Setup
        when(mockRepository.findOrderBySessionId("sessionId")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> orderServiceUnderTest.findOrderBySessionId("sessionId"))
                .isInstanceOf(OrderSessionIdNotFoundException.class);
    }

    @Test
    void testNotifyCheckoutStatus() throws Exception {
        final Order order1 = new Order();
        final OrderItem orderItem = new OrderItem();
        orderItem.setProductName("productName");
        orderItem.setItemId(UUID.fromString("80897d60-75ca-43f0-852f-ceb49cd48e03"));
        orderItem.setPrice(new BigDecimal("0.00"));
        order1.setItems(List.of(orderItem));
        final Metadata metadata = new Metadata();
        metadata.setUsername("username");
        metadata.setStatus("status");
        metadata.setSessionId("sessionId");
        order1.setMetadata(metadata);
        final Optional<Order> order = Optional.of(order1);
        when(mockRepository.findOrderBySessionId("sessionId")).thenReturn(order);

        when(mockStripeService.retrieveCheckoutStatus("sessionId")).thenReturn("status");

        // Run the test
        orderServiceUnderTest.notifyCheckoutStatus("sessionId");

        // Verify the results
        verify(mockCheckoutProducer).notifyOrderResult("username", "status");
    }

    @Test
    void testNotifyCheckoutStatus_IOrderRepositoryReturnsAbsent() {
        // Setup
        when(mockRepository.findOrderBySessionId("sessionId")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> orderServiceUnderTest.notifyCheckoutStatus("sessionId"))
                .isInstanceOf(OrderSessionIdNotFoundException.class);
    }

    @Test
    void testDeleteOrder() {
        when(mockRepository.existsById(UUID.fromString("106b9312-48a4-4837-b35f-5d903c3c13c3"))).thenReturn(true);
        orderServiceUnderTest.deleteOrder(UUID.fromString("106b9312-48a4-4837-b35f-5d903c3c13c3"));

        verify(mockRepository).deleteById(UUID.fromString("106b9312-48a4-4837-b35f-5d903c3c13c3"));
    }

    @Test
    void testDeleteOrder_IOrderRepositoryExistsByIdReturnsFalse() {
        // Setup
        when(mockRepository.existsById(UUID.fromString("106b9312-48a4-4837-b35f-5d903c3c13c3"))).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> orderServiceUnderTest.deleteOrder(
                UUID.fromString("106b9312-48a4-4837-b35f-5d903c3c13c3"))).isInstanceOf(OrderIdNotFoundException.class);
    }

    @Test
    void testGetOrderById_IOrderRepositoryReturnsAbsent() {
        when(mockRepository.findById(UUID.fromString("20bee993-1be4-4358-928a-f2c3ae8d2657")))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderServiceUnderTest.getOrderById(
                UUID.fromString("20bee993-1be4-4358-928a-f2c3ae8d2657"))).isInstanceOf(OrderIdNotFoundException.class);
    }

    @Test
    void testGetAllOrdersForUser_IOrderRepositoryReturnsNoItems() {

        when(mockRepository.findOrdersByUsername("username")).thenReturn(Collections.emptyList());

        // Run the test
        final List<Order> result = orderServiceUnderTest.getAllOrdersForUser("username");

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testDeleteAllOrders() {
        // Setup
        // Run the test
        orderServiceUnderTest.deleteAllOrders();

        // Verify the results
        verify(mockRepository).deleteAll();
    }

}
