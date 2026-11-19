package com.bookstore.app.configs;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bookstore.app.entities.Book;
import com.bookstore.app.repositories.BookRepository;

@Configuration
public class BookDataInitializer {
	
    @Bean
    CommandLineRunner seedBooks(BookRepository repository) {
    	
    	
        return args -> {
            if (repository.count() == 0) {
                repository.save(Book.create("Effective Java", "Joshua Bloch", "A comprehensive guide to Java best practices.", new BigDecimal("45.00"), 10, null));
                repository.save(Book.create("Clean Code", "Robert C. Martin", "A handbook of agile software craftsmanship.", new BigDecimal("50.00"), 15, null));
                repository.save(Book.create("Spring in Action", "Craig Walls", "A practical guide to the Spring Framework.", new BigDecimal("40.00"), 8, null));
            }
        };
    }
    
    
    
    @Bean
    CommandLineRunner seedBooks2(BookRepository repository) {
    	
    return args -> {
		if (repository.count() == 3) {
			repository.saveAll(
					List.of(
							Book.create("The Great Gatsby", "F. Scott Fitzgerald", "A timeless classic", new BigDecimal("10.99"), 6, null),
							Book.create("To Kill a Mockingbird", "Harper Lee", "A timeless classic", new BigDecimal("12.99"), 11, null),
							Book.create("1984", "George Orwell" , "A timeless classic", new BigDecimal("9.99"), 8, null),
							Book.create("Pride and Prejudice" , "A timeless classic", "Jane Austen", new BigDecimal("11.99"), 20, null),
							Book.create("The Catcher in the Rye", "J.D. Salinger" , "A timeless classic", new BigDecimal("8.99"), 5, null)
					)
			);
		}
	};
   }
    
}
