package de.htw.paymentservice.unitTests;

import de.htw.paymentservice.core.domain.model.Metadata;
import de.htw.paymentservice.core.domain.model.Order;
import de.htw.paymentservice.core.domain.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    private UUID id;
    private List<OrderItem> items;
    private Metadata metadata;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        items = new ArrayList<>();
        metadata = new Metadata();
    }

    @Test
    void testConstructorWithId() {
        Order order = new Order(id, items, metadata);

        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getItems()).isEqualTo(items);
        assertThat(order.getMetadata()).isEqualTo(metadata);
    }

    @Test
    void testConstructorWithoutId() {
        Order order = new Order(items, metadata);

        assertThat(order.getId()).isNull();
        assertThat(order.getItems()).isEqualTo(items);
        assertThat(order.getMetadata()).isEqualTo(metadata);
    }

    @Test
    void testEquals() {
        Order order1 = new Order(id, items, metadata);
        Order order2 = new Order(id, items, metadata);

        assertThat(order1).isEqualTo(order2);
    }

    @Test
    void testNotEqualsDifferentItems() {
        OrderItem item1 = new OrderItem();
        items.add(item1);
        Order order1 = new Order(id, items, metadata);
        List<OrderItem> differentItems = new ArrayList<>();
        Order order2 = new Order(id, differentItems, metadata);

        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    void testGettersAndSetters() {
        Order order = new Order();
        order.setId(id);
        order.setItems(items);
        order.setMetadata(metadata);

        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getItems()).isEqualTo(items);
        assertThat(order.getMetadata()).isEqualTo(metadata);
    }

    @Test
    void testEqualsSameInstance() {
        Order order = new Order(id, items, metadata);

        assertThat(order).isEqualTo(order);
    }

    @Test
    void testEqualsNullInstance() {
        Order order = new Order(id, items, metadata);

        assertThat(order).isNotEqualTo(null);
    }

    @Test
    void testEqualsDifferentClass() {
        Order order = new Order(id, items, metadata);
        String differentClassObject = "string";

        assertThat(order).isNotEqualTo(differentClassObject);
    }
}