package de.htw.paymentservice.core.domain.service.interfaces;

import com.stripe.model.checkout.Session;
import de.htw.paymentservice.port.dto.BasketDTO;

public interface IStripeService {
    Session createCheckoutSession(BasketDTO basket);
}
