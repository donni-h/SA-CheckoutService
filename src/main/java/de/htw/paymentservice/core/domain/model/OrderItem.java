package de.htw.paymentservice.core.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="order_item")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "Product name cannot be null")
    private String productName;

    @NotNull(message = "itemID cannot be null")
    private UUID itemId;

    @NotNull(message = "price cannot be null")
    @Positive(message = "Price has to be greater than 0")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    public OrderItem(UUID id, String productName, UUID itemId, BigDecimal price, Order order) {
        this.id = id;
        this.productName = productName;
        this.itemId = itemId;
        this.price = price;
        this.order = order;
    }

    public OrderItem(String productName, UUID itemId, BigDecimal price, Order order) {
        this.productName = productName;
        this.itemId = itemId;
        this.price = price;
        this.order = order;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        OrderItem item = (OrderItem) o;
        return this.getProductName().equals(item.getProductName())
                && this.getItemId().equals(item.getItemId())
                && this.getPrice().equals(item.getPrice());
    }

}
