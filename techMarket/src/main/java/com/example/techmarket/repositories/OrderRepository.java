package com.example.techmarket.repositories;

import com.example.techmarket.entities.Order;
import com.example.techmarket.entities.OrderStatus;
import com.example.techmarket.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author lucio
 */

public interface OrderRepository extends JpaRepository<Order, Integer>{
    List<Order> findByUser(User user);

    Optional<Order> findById(int id);

    List<Order> findByUserEmail(String userEmail);

    @Query(value = """
SELECT o.* FROM orders o
JOIN users u ON u.id = o.user_id
WHERE (:statusList = '' OR o.status = ANY(string_to_array(:statusList, ',')))
AND (u.email ILIKE CONCAT('%', :email, '%'))
AND (o.created_at >= :fromDate)
AND (o.created_at <= :toDate)
AND (o.total >= :minTotal)
AND (o.total <= :maxTotal)
""", nativeQuery = true)
    Page<Order> searchFiltered(
            @Param("statusList") String statusList,
            @Param("email") String email,
            @Param("fromDate") java.sql.Timestamp fromDate,
            @Param("toDate") java.sql.Timestamp toDate,
            @Param("minTotal") java.math.BigDecimal minTotal,
            @Param("maxTotal") java.math.BigDecimal maxTotal,
            Pageable pageable
    );

    @Query("select sum(o.totalPrice) from Order o")
    BigDecimal sumTotalSales();
}
