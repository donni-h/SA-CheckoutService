package de.htw.paymentservice.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.core.domain.service.impl.StripeService;
import de.htw.paymentservice.core.domain.service.interfaces.IOrderService;
import de.htw.paymentservice.core.domain.service.interfaces.IStripeService;
import de.htw.paymentservice.port.dto.BasketDTO;
import de.htw.paymentservice.port.dto.ItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://example.com/oauth2/default/v1/keys",
        "KEYCLOAK_CERTS=your-test-keycloak-certs-value",
        "KEYCLOAK_ISSUER=https://example.com/oauth2/default",
        "DOMAIN=http://localhost:3000",
        "stripe.secretKey=sk_test_examplekey"
})
public class PaymentControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StripeService stripeService;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private IOrderService orderService;

    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() throws StripeException {
        // Mock the Stripe service to return a dummy session
        Session mockSession = new Session();
        mockSession.setId("test_session_id");
        mockSession.setUrl("https://example.com/checkout");
        when(stripeService.createCheckoutSession(any(BasketDTO.class))).thenReturn(mockSession);
        when(stripeService.retrieveCheckoutStatus(anyString())).thenReturn("complete");
        doNothing().when(stripeService).expireSession(anyString());
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

    @Test
    void connectionEstablishedTest() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void createCheckoutSessionTest() throws Exception {
        BasketDTO basketDTO = createTestBasketDTO();

        mockMvc.perform(post("/api/payment/create-checkout-session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basketDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("https://example.com/checkout")));

        verify(stripeService).createCheckoutSession(any(BasketDTO.class));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void cancelTest() throws Exception {
        // First create a checkout session
        BasketDTO basketDTO = createTestBasketDTO();
        mockMvc.perform(post("/api/payment/create-checkout-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketDTO)));

        mockMvc.perform(get("/api/payment/cancel")
                        .param("session_id", "test_session_id"))
                .andExpect(status().isOk());

        verify(stripeService).expireSession("test_session_id");
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void getOrderByIdTest() throws Exception {
        // First create a checkout session and order
        BasketDTO basketDTO = createTestBasketDTO();
        mockMvc.perform(post("/api/payment/create-checkout-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketDTO)));

        // Get the created order
        Order order = orderService.findOrderBySessionId("test_session_id");

        mockMvc.perform(get("/api/payment/orderbyid")
                        .param("order_id", order.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void getAllOrdersForUserTest() throws Exception {
        // Create a few orders for the user
        BasketDTO basketDTO = createTestBasketDTO();
        mockMvc.perform(post("/api/payment/create-checkout-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketDTO)));
        mockMvc.perform(post("/api/payment/create-checkout-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketDTO)));

        mockMvc.perform(get("/api/payment/allordersforuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void deleteOrderByIdTest() throws Exception {
        // First create a checkout session and order
        BasketDTO basketDTO = createTestBasketDTO();
        mockMvc.perform(post("/api/payment/create-checkout-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketDTO)));

        // Get the created order
        Order order = orderService.findOrderBySessionId("test_session_id");

        mockMvc.perform(delete("/api/payment/order")
                        .param("order_id", order.getId().toString()))
                .andExpect(status().isOk());

        // Verify that the order is deleted
        mockMvc.perform(get("/api/payment/orderbyid")
                        .param("order_id", order.getId().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void deleteAllOrdersTest() throws Exception {
        // Create a few orders
        BasketDTO basketDTO = createTestBasketDTO();
        mockMvc.perform(post("/api/payment/create-checkout-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketDTO)));
        mockMvc.perform(post("/api/payment/create-checkout-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketDTO)));

        mockMvc.perform(delete("/api/payment/orders"))
                .andExpect(status().isOk());

        // Verify that all orders are deleted
        mockMvc.perform(get("/api/payment/allordersforuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private BasketDTO createTestBasketDTO() {
        ItemDTO item = new ItemDTO();
        item.setProductId(UUID.randomUUID());
        item.setName("Test Plant");
        item.setItemPrice(new BigDecimal("19.99"));

        BasketDTO basketDTO = new BasketDTO();
        basketDTO.setItems(Arrays.asList(item));
        basketDTO.setTotalAmount(new BigDecimal("19.99"));

        return basketDTO;
    }
}