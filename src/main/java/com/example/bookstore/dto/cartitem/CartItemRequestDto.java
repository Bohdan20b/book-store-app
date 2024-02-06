package com.example.bookstore.dto.cartitem;

import lombok.Data;

@Data
public class CartItemRequestDto {
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
}
