package com.example.bookstore.controller;

import com.example.bookstore.dto.order.CreateOrderRequestDto;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.StatusDto;
import com.example.bookstore.dto.orderitem.OrderItemDto;
import com.example.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(summary = "Place an order",
            description = "Place an order for items in your shopping cart")
    public OrderDto placeOrder(Authentication authentication,
            @RequestBody CreateOrderRequestDto dto) {
        return orderService.createNewOrder(getEmail(authentication), dto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(summary = "Get all orders for user",
            description = "Get a list of user's orders")
    public List<OrderDto> getAllOrdersByUser(Authentication authentication, Pageable pageable) {
        return orderService.findAllOrdersByUser(getEmail(authentication), pageable);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get list of items by order",
            description = "Get all items for specific order")
    public List<OrderItemDto> getOrderItemsByOrderId(Authentication authentication,
            @PathVariable Long orderId) {
        return orderService.findAllOrderItemsByOrderId(getEmail(authentication), orderId);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get details about item in order",
            description = "Get details about specific item in specific order")
    public OrderItemDto getSpecificOrderItemById(Authentication authentication,
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        return orderService.getOrderItemById(getEmail(authentication), orderId, itemId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update status",
            description = "Update order status as admin")
    public OrderDto updateOrderStatus(@PathVariable Long id, @RequestBody
            StatusDto dto) {
        return orderService.updateOrderStatus(id, dto);
    }

    private String getEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
