package com.example.bookstore.service;

import com.example.bookstore.dto.user.UserDto;
import com.example.bookstore.dto.user.UserRegistrationRequestDto;
import com.example.bookstore.exception.RegistrationException;
import com.example.bookstore.model.User;

public interface UserService {
    UserDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    User getUserByEmail(String email);
}
