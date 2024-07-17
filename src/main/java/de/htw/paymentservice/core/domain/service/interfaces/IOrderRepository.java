package de.htw.paymentservice.core.domain.service.interfaces;

import de.htw.paymentservice.core.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o JOIN o.metadata m WHERE m.sessionId = :sessionId")
    Optional<Order> findOrderBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT o FROM Order o JOIN o.metadata m WHERE m.username = :username")
    List<Order> findOrdersByUsername(@Param("username") String username);
}
