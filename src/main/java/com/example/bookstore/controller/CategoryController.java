package com.example.bookstore.controller;

import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoints for managing categories")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;
    private final BookMapper bookMapper;

    @Operation(summary = "Create the category", description = "Creating new category")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public CategoryDto createCategory(@RequestBody @Valid CreateCategoryRequestDto dto) {
        return categoryService.save(dto);
    }

    @Operation(summary = "Get all categories", description = "Get a list of categories")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public List<CategoryDto> getAll() {
        return categoryService.findAll();
    }

    @Operation(summary = "Get a category by id", description = "Get a specific category by id")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @Operation(summary = "Update the category", description = "Update an existing category by id")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public CategoryDto updateCategoryById(@PathVariable Long id,
            @RequestBody CreateCategoryRequestDto dto) {
        return categoryService.update(id, dto);
    }

    @Operation(summary = "Delete the category", description = "Soft delete the category by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @Operation(summary = "Get books by category id", description = "Get books by category id")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}/books")
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(@PathVariable Long id) {
        return bookService.findAllByCategoryId(id)
                .stream()
                .map(bookMapper::toDtoWithoutCategoryIds)
                .toList();
    }
}
