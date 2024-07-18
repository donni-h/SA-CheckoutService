package de.htw.paymentservice.unitTests;

import de.htw.paymentservice.port.dto.ResultDTO;
import de.htw.paymentservice.port.mappers.CheckoutToResultMapper;
import de.htw.paymentservice.port.producer.CheckoutProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutProducerTest {

    @Mock
    private RabbitTemplate mockRabbitTemplate;
    @Mock
    private CheckoutToResultMapper mockMapper;

    private CheckoutProducer checkoutProducerUnderTest;

    @BeforeEach
    void setUp() {
        checkoutProducerUnderTest = new CheckoutProducer(mockRabbitTemplate, mockMapper);
        ReflectionTestUtils.setField(checkoutProducerUnderTest, "exchange", "CheckoutExchange");
        ReflectionTestUtils.setField(checkoutProducerUnderTest, "routingKey", "CheckoutToBasket");
    }

    @Test
    void testNotifyOrderResult() {
        // Setup
        // Configure CheckoutToResultMapper.getResultDTO(...).
        final ResultDTO resultDTO = new ResultDTO();
        resultDTO.setStatus("status");
        resultDTO.setUsername("username");
        when(mockMapper.getResultDTO("username", "status")).thenReturn(resultDTO);

        // Run the test
        checkoutProducerUnderTest.notifyOrderResult("username", "status");

        // Verify the results
        // Confirm RabbitTemplate.convertAndSend(...).
        final ResultDTO object = new ResultDTO();
        object.setStatus("status");
        object.setUsername("username");
        verify(mockRabbitTemplate).convertAndSend("CheckoutExchange", "CheckoutToBasket", object);
    }

    @Test
    void testNotifyOrderResult_RabbitTemplateThrowsAmqpException() {
        // Setup
        // Configure CheckoutToResultMapper.getResultDTO(...).
        final ResultDTO resultDTO = new ResultDTO();
        resultDTO.setStatus("status");
        resultDTO.setUsername("username");
        when(mockMapper.getResultDTO("username", "status")).thenReturn(resultDTO);

        // Configure RabbitTemplate.convertAndSend(...).
        final ResultDTO object = new ResultDTO();
        object.setStatus("status");
        object.setUsername("username");
        doThrow(AmqpException.class).when(mockRabbitTemplate).convertAndSend("CheckoutExchange", "CheckoutToBasket",
                object);

        // Run the test
        assertThatThrownBy(() -> checkoutProducerUnderTest.notifyOrderResult("username", "status"))
                .isInstanceOf(AmqpException.class);
    }
}
