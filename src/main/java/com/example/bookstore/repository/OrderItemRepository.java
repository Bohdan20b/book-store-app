package com.example.bookstore.repository;

import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrder(Order order);
}
