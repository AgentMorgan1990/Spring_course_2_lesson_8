package com.geekbrains.spring.web.core.repositories;

import com.geekbrains.spring.web.core.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.username = ?1")
    List<Order> findAllByUsername(String username);

    @Query("select o from Order o where o.createdAt between ?1 and ?2")
    List<Order> findAllOrdersInTimePeriod (LocalDateTime startDate, LocalDateTime finishDate);
}
