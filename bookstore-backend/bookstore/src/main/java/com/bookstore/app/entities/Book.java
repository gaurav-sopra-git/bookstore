package com.bookstore.app.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "books", indexes = @Index(name = "idx_book_title_author", columnList = "title,author"))
@Getter
public class Book {
	
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, length = 150)
    private String author;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false)
    private int stock;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @Column(length = 1000)
    private String imageUrl;
    
    @Version
    private Long version;


    public static Book create(String title, String author, String description, BigDecimal price, int stock, String imageUrl) {
        if (price == null || price.signum() < 0 || stock < 0) throw new IllegalArgumentException("Price and stock must be non-negative");
        var book = new Book();
        book.title = title;
        book.author = author;
        book.description = description;
        book.price = price;
        book.stock = stock;
        book.imageUrl = imageUrl;
        return book;
    }

    public void decreaseStock(int quantity) {
        if (quantity < 1 || quantity > stock) throw new IllegalArgumentException("Insufficient stock for " + title);
        stock -= quantity;
    }
}
