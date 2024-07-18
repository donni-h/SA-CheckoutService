package de.htw.paymentservice.core.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;
import java.util.UUID;

@Entity
@Table(name="orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items;

    @NotNull(message="needs to have metadata")
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Metadata metadata;

    public Order(UUID id, List<OrderItem> items, Metadata metadata) {
        this.id = id;
        this.items = items;
        this.metadata = metadata;
    }

    public Order(List<OrderItem> items, Metadata metadata) {
        this.items = items;
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Order order = (Order) o;
        List<OrderItem> compareOrderItems = order.getItems();
        Boolean itemsEqual = true;
        for (OrderItem item: this.getItems()){
            if (!compareOrderItems.contains(item)){
                itemsEqual = false;
                break;
            }
        }
        return itemsEqual && this.getMetadata().equals(order.getMetadata());
    }
}
