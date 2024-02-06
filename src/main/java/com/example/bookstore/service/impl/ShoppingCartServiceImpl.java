package com.example.bookstore.service.impl;

import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.ShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getShoppingCartDto(String email) {
        ShoppingCart shoppingCart = getShoppingCart(email);
        shoppingCart.setCartItems(cartItemRepository.getAllByShoppingCart(shoppingCart));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addItemsToCart(String email, CartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = getShoppingCart(email);
        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(bookRepository.getReferenceById(requestDto.getBookId()));
        cartItemRepository.save(cartItem);
        shoppingCart.setCartItems(cartItemRepository.getAllByShoppingCart(shoppingCart));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateQuantity(Long id, CartItemRequestDto requestDto) {
        CartItem cartItem = cartItemRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find cart item with id: " + id));
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        ShoppingCart shoppingCart = cartItem.getShoppingCart();
        shoppingCart.setCartItems(cartItemRepository.getAllByShoppingCart(shoppingCart));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteCartById(Long id) {
        cartItemRepository.deleteById(id);
    }

    private ShoppingCart getShoppingCart(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException("User not found."));
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository
                .findByUser(user);
        return shoppingCart.orElseGet(() -> createNewShoppingCart(user));
    }

    private ShoppingCart createNewShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return shoppingCartRepository.save(shoppingCart);
    }
}
