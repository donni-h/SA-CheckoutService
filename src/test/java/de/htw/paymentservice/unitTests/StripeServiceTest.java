package de.htw.paymentservice.unitTests;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import de.htw.paymentservice.core.domain.service.impl.StripeService;
import de.htw.paymentservice.core.domain.service.interfaces.IStripeService;
import de.htw.paymentservice.port.dto.BasketDTO;
import de.htw.paymentservice.port.dto.ItemDTO;
import de.htw.paymentservice.port.mappers.ItemDTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    private StripeService stripeServiceUnderTest;

    @BeforeEach
    void setUp() {
        stripeServiceUnderTest = new StripeService();
        ReflectionTestUtils.setField(stripeServiceUnderTest, "stripeSecretKey", "sk_test_4eC39HqLyjWDarjtT1zdp7dc");
        ReflectionTestUtils.setField(stripeServiceUnderTest, "DOMAIN", "http://localhost:3000");
    }

    @Test
    void testInit() {
        // Run the test
        stripeServiceUnderTest.init();

        // Verify the results
        assertThat(Stripe.apiKey).isEqualTo("sk_test_4eC39HqLyjWDarjtT1zdp7dc");
    }

    @Test
    void testRetrieveCheckoutStatus() throws StripeException {
        // Setup
        String sessionId = "sessionId";
        Session mockSession = mock(Session.class);
        when(mockSession.getStatus()).thenReturn("complete");

        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            mockedSession.when(() -> Session.retrieve(sessionId)).thenReturn(mockSession);

            // Run the test
            String result = stripeServiceUnderTest.retrieveCheckoutStatus(sessionId);

            // Verify the results
            assertThat(result).isEqualTo("complete");
        }
    }

    @Test
    void testExpireSession() throws StripeException {
        // Setup
        String sessionId = "sessionId";
        Session mockSession = mock(Session.class);

        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            mockedSession.when(() -> Session.retrieve(sessionId)).thenReturn(mockSession);

            // Run the test
            stripeServiceUnderTest.expireSession(sessionId);

            // Verify the results
            Mockito.verify(mockSession).expire();
        }
    }
}