package de.htw.paymentservice.core.domain.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import de.htw.paymentservice.core.domain.service.interfaces.IStripeService;
import de.htw.paymentservice.port.dto.BasketDTO;
import de.htw.paymentservice.port.mappers.ItemDTOMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class StripeService implements IStripeService {

    @Value("${stripe.secretKey}")
    private String stripeSecretKey;

    @Value("${domain}")
    private String DOMAIN;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public Session createCheckoutSession(BasketDTO basket) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setShippingAddressCollection(
                            SessionCreateParams.ShippingAddressCollection.builder()
                                    .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.DE)
                                    .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.US)
                                    .build())
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(String.format("%s/success?session_id=%s", DOMAIN, "{CHECKOUT_SESSION_ID}"))
                    .setCancelUrl(String.format("%s/cancel?session_id=%s", DOMAIN, "{CHECKOUT_SESSION_ID}"))
                    .addAllLineItem(basket.getItems().stream()
                            .map(ItemDTOMapper::mapToLineItem)
                            .collect(Collectors.toList())
                    )
                    .build();

            return Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Stripe API error: " + e.getMessage(), e);
        }

    }

    @Override
    public String retrieveCheckoutStatus(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        return session.getStatus();

    }

    @Override
    public void expireSession(String sessionId) throws StripeException {
       Session session = Session.retrieve(sessionId);
       session.expire();
    }
}
