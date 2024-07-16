package de.htw.paymentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody OrderRequest orderRequest) {
        String sessionId = stripeService.createCheckoutSession(orderRequest);
        Map<String, String> response = new HashMap<>();
        response.put("id", sessionId);
        return ResponseEntity.ok(response);
    }
}