package com.example.bookstore.repository;

import com.example.bookstore.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("SELECT DISTINCT sc FROM ShoppingCart sc "
            + "LEFT JOIN FETCH sc.cartItems ci WHERE sc.user.email = :email")
    Optional<ShoppingCart> findByUserWithItems(@Param("email") String email);
}
