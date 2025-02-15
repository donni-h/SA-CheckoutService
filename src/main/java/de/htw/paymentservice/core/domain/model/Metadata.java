package de.htw.paymentservice.core.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "metadata")
@Getter
@Setter
@NoArgsConstructor
public class Metadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    private String username;

    private String status;

    private String sessionId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public Metadata(Order order, String status, String sessionId, String username) {
        this.order = order;
        this.status = status;
        this.sessionId = sessionId;
        this.username = username;
    }

    public Metadata(String username, String status, String sessionId, Date createdAt) {
        this.username = username;
        this.status = status;
        this.sessionId = sessionId;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return this.getUsername().equals(metadata.getUsername())
                && this.getStatus().equals(metadata.getStatus())
                && this.getSessionId().equals(metadata.getSessionId())
                && this.getCreatedAt().equals(metadata.getCreatedAt());
    }
}
