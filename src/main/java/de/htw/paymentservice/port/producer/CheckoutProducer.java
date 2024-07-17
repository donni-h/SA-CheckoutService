package de.htw.paymentservice.port.producer;


import de.htw.paymentservice.port.dto.ResultDTO;
import de.htw.paymentservice.port.mappers.CheckoutToResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CheckoutProducer {
    private final RabbitTemplate rabbitTemplate;
    private final CheckoutToResultMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutProducer.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routingKey.name}")
    private String routingKey;

    @Autowired
    public CheckoutProducer(RabbitTemplate rabbitTemplate, CheckoutToResultMapper mapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.mapper = mapper;
    }

    public void notifyOrderResult(String username, String status) {
        ResultDTO resultDTO = mapper.getResultDTO(username, status);
        rabbitTemplate.convertAndSend(exchange, routingKey, resultDTO);
    }
}
