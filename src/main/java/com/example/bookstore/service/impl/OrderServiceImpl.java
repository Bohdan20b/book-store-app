package com.example.bookstore.service.impl;

import com.example.bookstore.dto.order.CreateOrderRequestDto;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.StatusDto;
import com.example.bookstore.dto.orderitem.OrderItemDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.mapper.OrderMapper;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.Order.Status;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.OrderItemRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.OrderService;
import com.example.bookstore.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserService userService;

    @Override
    public List<OrderDto> findAllOrdersByUser(String email, Pageable pageable) {
        return orderRepository.findAllOrderByUserId(userService.getUserByEmail(email).getId(), pageable)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getOrderItemById(String email, Long orderId, Long itemId) {
        return getOrderById(orderId)
                .getOrderItems()
                .stream()
                .filter(orderItem -> orderItem.getId().equals(itemId))
                .findFirst()
                .map(orderItemMapper::toDto).orElseThrow(
                        () -> new EntityNotFoundException("Can't find item with id: " + itemId)
                );
    }

    @Override
    public OrderDto createNewOrder(String email, CreateOrderRequestDto dto) {
        User user = userService.getUserByEmail(email);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserWithItems(user.getEmail())
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find shopping cart by user id: "
                                + user.getId()));
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.PENDING);
        order.setTotal(countTotal(shoppingCart));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(dto.shippingAddress());
        Set<OrderItem> orderItems = createOrderItems(order,shoppingCart.getCartItems());
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderItemDto> findAllOrderItemsByOrderId(String email, Long orderId) {
        Order order = getOrderById(orderId);
        return orderItemRepository.findAllByOrder(order)
                .stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, StatusDto dto) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id: " + orderId)
        );
        order.setStatus(dto.status());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    private Set<OrderItem> createOrderItems(Order order, Set<CartItem> cartItems) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private BigDecimal countTotal(ShoppingCart shoppingCart) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            BigDecimal quantity = BigDecimal.valueOf(cartItem.getQuantity());
            BigDecimal price = cartItem.getBook().getPrice();
            BigDecimal itemTotal = quantity.multiply(price);
            totalPrice = totalPrice.add(itemTotal);
        }
        return totalPrice;
    }

    private Order getOrderById(Long orderId) {
        Optional<Order> optionalOrder = orderRepository
                .findOrderById(orderId);
        return optionalOrder.orElseThrow(() ->
                new EntityNotFoundException("Can't find order with id:" + orderId));
    }
}
