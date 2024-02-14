package com.example.bookstore.service;

import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartDto(String email);

    ShoppingCartDto addItemToCart(String email, CartItemRequestDto requestDto);

    ShoppingCartDto updateQuantity(Long id, CartItemRequestDto requestDto);

    void deleteCartById(Long id);
}
