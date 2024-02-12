package com.example.bookstore.repository;

import com.example.bookstore.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllOrderByUserId(@Param("userId") Long userId, Pageable pageable);

    Optional<Order> findByUserIdAndId(@Param("userId") Long userId,@Param("orderId") Long orderId);
}
