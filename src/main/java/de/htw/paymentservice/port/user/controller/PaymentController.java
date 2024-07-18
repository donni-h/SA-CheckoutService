package de.htw.paymentservice.port.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.core.domain.service.impl.StripeService;
import de.htw.paymentservice.core.domain.service.interfaces.IOrderService;
import de.htw.paymentservice.core.domain.service.interfaces.IStripeService;
import de.htw.paymentservice.port.dto.BasketDTO;
import de.htw.paymentservice.port.user.exception.OrderIdNotFoundException;
import de.htw.paymentservice.port.user.exception.OrderSessionIdNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

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
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody String createCheckoutSession(@RequestBody @Valid BasketDTO basketDTO, Authentication authentication) throws StripeException {
        String username = authentication.getName();
        Session session = stripeService.createCheckoutSession(basketDTO);
        orderService.createOrder(session, basketDTO.getItems(), username);
        return session.getUrl();
    }

    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Order success(@RequestParam(name = "session_id", required = true) String sessionId) throws StripeException, OrderSessionIdNotFoundException{
        Order order = orderService.findOrderBySessionId(sessionId);
        order = orderService.updateOrderStatus(order);
        orderService.notifyCheckoutStatus(sessionId);
        return order;
    }

    @GetMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody void cancel(@RequestParam(name = "session_id", required = true) String sessionId) throws OrderSessionIdNotFoundException, OrderIdNotFoundException, StripeException {
        Order order = orderService.findOrderBySessionId(sessionId);
        stripeService.expireSession(sessionId);
        orderService.deleteOrder(order.getId());
    }

    //getorderbyid
    @GetMapping("/orderbyid")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Order getOrderById(@RequestParam(name = "order_id", required = true) UUID orderId) throws OrderIdNotFoundException {
        return orderService.getOrderById(orderId);
    }

    //getallordersforuser
    @GetMapping("/allordersforuser")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Order> getAllOrdersForUser(Authentication connectedUser){
        return orderService.getAllOrdersForUser(connectedUser.getName());
    }

    //deleteorder
    @DeleteMapping("/order")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrderById(@RequestParam(name = "order_id", required = true) UUID orderId) throws OrderIdNotFoundException{
        orderService.deleteOrder(orderId);
    }

    @DeleteMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllOrders(){
        orderService.deleteAllOrders();
    }
}