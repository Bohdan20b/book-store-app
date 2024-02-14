package com.example.bookstore.controller;

import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing cart and items")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Add new book",
            description = "Adding new book to shopping cart")
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public ShoppingCartDto addBookToShoppingCart(Authentication authentication,
            @RequestBody CartItemRequestDto dto) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return shoppingCartService.addItemToCart(userDetails.getUsername(), dto);
    }

    @Operation(summary = "Get all my items",
            description = "Getting all items in shopping cart by user")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return shoppingCartService.getShoppingCartDto(userDetails.getUsername());
    }

    @Operation(summary = "Update books quantity",
            description = "Updating the number of books in cart")
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/cart-items/{cartItemId}")
    public ShoppingCartDto updateBooksQuantity(
            @PathVariable Long cartItemId,
            @RequestBody CartItemRequestDto dto) {
        return shoppingCartService.updateQuantity(cartItemId, dto);
    }

    @Operation(summary = "Remove the book from cart",
            description = "Removing specific book from user's cart by id")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/cart-items/{cartItemId}")
    public void deleteBookFromShoppingCart(@PathVariable Long cartItemId) {
        shoppingCartService.deleteCartById(cartItemId);
    }
}
