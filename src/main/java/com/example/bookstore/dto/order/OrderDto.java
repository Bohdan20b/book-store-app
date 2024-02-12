package com.example.bookstore.dto.order;

import com.example.bookstore.dto.orderitem.OrderItemDto;
import com.example.bookstore.model.Order.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    @EqualsAndHashCode.Exclude
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Status status;
}
