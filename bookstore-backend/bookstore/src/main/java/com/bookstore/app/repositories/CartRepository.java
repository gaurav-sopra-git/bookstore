package com.bookstore.app.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.bookstore.app.entities.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	
	@Query("select distinct c from Cart c left join fetch c.items i left join fetch i.book where c.user.id=:userId")
	Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);

	@Query("select distinct c from Cart c left join fetch c.items i left join fetch i.book where c.cartToken=:token")
	Optional<Cart> findByTokenWithItems(@Param("token") String token);

	Optional<Cart> findByUser_Id(Long userId);
}
