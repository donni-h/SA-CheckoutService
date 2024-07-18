package de.htw.paymentservice.unitTests;

import de.htw.paymentservice.core.domain.model.Metadata;
import de.htw.paymentservice.core.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class MetadataTest {

    @Mock
    private Order mockOrder;

    private Metadata metadataUnderTest;

    @BeforeEach
    void setUp() {
        metadataUnderTest = new Metadata(mockOrder, "status", "sessionId", "username");
    }

    @Test
    void testEquals() {
        // Run the test
        final boolean result = metadataUnderTest.equals("o");

        // Verify the results
        assertThat(result).isFalse();
    }

    @Test
    void testIdGetterAndSetter() {
        final UUID id = UUID.fromString("c2a6dbfa-7be5-4226-bcaf-c0c90e4aaf66");
        metadataUnderTest.setId(id);
        assertThat(metadataUnderTest.getId()).isEqualTo(id);
    }

    @Test
    void testOrderGetterAndSetter() {
        final Order order = new Order();
        metadataUnderTest.setOrder(order);
        assertThat(metadataUnderTest.getOrder()).isEqualTo(order);
    }

    @Test
    void testUsernameGetterAndSetter() {
        final String username = "username";
        metadataUnderTest.setUsername(username);
        assertThat(metadataUnderTest.getUsername()).isEqualTo(username);
    }

    @Test
    void testStatusGetterAndSetter() {
        final String status = "status";
        metadataUnderTest.setStatus(status);
        assertThat(metadataUnderTest.getStatus()).isEqualTo(status);
    }

    @Test
    void testSessionIdGetterAndSetter() {
        final String sessionId = "sessionId";
        metadataUnderTest.setSessionId(sessionId);
        assertThat(metadataUnderTest.getSessionId()).isEqualTo(sessionId);
    }

    @Test
    void testCreatedAtGetterAndSetter() {
        final Date createdAt = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        metadataUnderTest.setCreatedAt(createdAt);
        assertThat(metadataUnderTest.getCreatedAt()).isEqualTo(createdAt);
    }
}
