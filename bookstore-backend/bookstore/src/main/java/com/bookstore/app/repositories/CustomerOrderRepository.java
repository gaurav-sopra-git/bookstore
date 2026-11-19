package com.bookstore.app.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.bookstore.app.entities.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
	
	@Query("select distinct o from CustomerOrder o left join fetch o.items where o.orderNumber=:number and o.user.id=:userId")
	Optional<CustomerOrder> findDetailedByNumberAndUser(@Param("number") String number, @Param("userId") Long userId);

	List<CustomerOrder> findAllByUserIdOrderByCreatedAtDesc(Long userId);
	
	
	Optional<CustomerOrder> findByOrderNumberAndUser_Id(String orderNumber, Long userId);
}
