package com.example.bookstore.service;

import com.example.bookstore.dto.order.CreateOrderRequestDto;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.StatusDto;
import com.example.bookstore.dto.orderitem.OrderItemDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    List<OrderDto> findAllOrdersByUser(String email, Pageable pageable);

    OrderItemDto getOrderItemById(String email, Long orderId, Long itemId);

    OrderDto createNewOrder(String email, CreateOrderRequestDto dto);

    List<OrderItemDto> findAllOrderItemsByOrderId(String email, Long orderId);

    OrderDto updateOrderStatus(Long orderId, StatusDto dto);

    void deleteById(Long id);
}
