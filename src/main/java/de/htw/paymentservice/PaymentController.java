package de.htw.paymentservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.LineItem;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.core.domain.service.impl.OrderService;
import de.htw.paymentservice.core.domain.service.impl.StripeService;
import de.htw.paymentservice.core.domain.service.interfaces.IOrderService;
import de.htw.paymentservice.core.domain.service.interfaces.IStripeService;
import de.htw.paymentservice.port.dto.BasketDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/payment")
public class PaymentController {


    private IStripeService stripeService;

    private IOrderService orderService;

    @Autowired
    public PaymentController(IStripeService stripeService, IOrderService orderService) {
        this.stripeService = stripeService;
        this.orderService = orderService;
    }

    @PostMapping("/create-checkout-session")
    public @ResponseBody String createCheckoutSession(@RequestBody @Valid BasketDTO basketDTO) throws StripeException {
        Session session = stripeService.createCheckoutSession(basketDTO);
        Order order = orderService.createOrder(session, basketDTO.getItems());
        return stripeService.createCheckoutSession(basketDTO).getUrl();
    }

    @GetMapping("/success")
    public @ResponseBody Order success(@RequestParam(name = "session_id") String sessionId) throws StripeException, JsonProcessingException {
        Order order = orderService.findOrderBySessionId(sessionId);
        return order;
    }
}