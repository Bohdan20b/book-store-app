package com.example.bookstore;

import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setAuthor("William Shakespeare");
            book.setTitle("Macbeth");
            book.setDescription("description example");
            book.setPrice(BigDecimal.valueOf(4.69));
            book.setIsbn("ibsn string example");
            book.setCoverImage("cover image");
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }

}
