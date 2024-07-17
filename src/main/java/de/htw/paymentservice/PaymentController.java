package de.htw.paymentservice;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.core.domain.service.interfaces.IOrderService;
import de.htw.paymentservice.core.domain.service.interfaces.IStripeService;
import de.htw.paymentservice.port.dto.BasketDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

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
    public @ResponseBody String createCheckoutSession(@RequestBody @Valid BasketDTO basketDTO, Authentication authentication) throws StripeException {
        String username = authentication.getName();
        Session session = stripeService.createCheckoutSession(basketDTO);
        orderService.createOrder(session, basketDTO.getItems(), username);
        return stripeService.createCheckoutSession(basketDTO).getUrl();
    }

    @GetMapping("/success")
    public @ResponseBody Order success(@RequestParam(name = "session_id") String sessionId) throws StripeException, JsonProcessingException {
        Order order = orderService.findOrderBySessionId(sessionId);
        orderService.notifyCheckoutStatus(sessionId);
        return order;
    }

    @GetMapping("/cancel")
    public @ResponseBody void cancel(@RequestParam(name = "session_id") String sessionId) throws StripeException {
        Order order = orderService.findOrderBySessionId(sessionId);
        orderService.deleteOrder(order);
    }
}