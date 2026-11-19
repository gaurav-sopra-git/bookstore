package com.bookstore.app.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.bookstore.app.entities.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

	@Query("select b from Book b where lower(b.title) like lower(concat('%',:term,'%')) or lower(b.author) like lower(concat('%',:term,'%'))")
	List<Book> search(@Param("term") String term);
}
