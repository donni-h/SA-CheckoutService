package de.htw.paymentservice.core.domain.service.interfaces;

import de.htw.paymentservice.core.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT o FROM Order o JOIN o.metadata m WHERE m.sessionId = :sessionId")
    Optional<Order> findOrderBySessionId(@Param("sessionId") String sessionId);
}
