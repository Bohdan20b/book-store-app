package com.example.bookstore.service.impl;

import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.Book;
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
        ShoppingCart shoppingCart = getShoppingCartWithItems(email);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addItemToCart(String email, CartItemRequestDto requestDto) {
        Optional<Book> optionalBook = bookRepository.findById(requestDto.getBookId());
        Book book = optionalBook.orElseThrow(() ->
                new EntityNotFoundException("Can't find book with id: " + requestDto.getBookId()));
        ShoppingCart shoppingCart = getShoppingCartWithItems(email);
        boolean cartItemExists = shoppingCart.getCartItems().stream()
                .anyMatch(cartItem -> cartItem.getBook().getId().equals(book.getId()));
        if (cartItemExists) {
            throw new IllegalArgumentException("The book is already in the shopping cart.");
        }
        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItemRepository.save(cartItem);
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

    private ShoppingCart getShoppingCartWithItems(String email) {
        return shoppingCartRepository.findByUserWithItems(email)
                .orElseGet(() -> createNewShoppingCart(email));
    }

    private ShoppingCart createNewShoppingCart(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException("User not found."));
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return shoppingCartRepository.save(shoppingCart);
    }
}
