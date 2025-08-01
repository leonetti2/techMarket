package com.example.techmarket.services;

import com.example.techmarket.entities.Order;
import com.example.techmarket.entities.OrderStatus;
import com.example.techmarket.entities.User;
import com.example.techmarket.repositories.OrderRepository;
import com.example.techmarket.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lucio
 */

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(int userId){
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(int id){
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return orderRepository.findAll(paging);
    }

    @Transactional(readOnly = true)
    public Page<Order> getAllOrdersFiltered(
            int pageNumber, int pageSize, String sortBy,
            List<OrderStatus> status, String email, LocalDate from, LocalDate to,
            BigDecimal minTotal, BigDecimal maxTotal
    ) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        String statusListParam = (status == null || status.isEmpty())
                ? ""
                : status.stream().map(Enum::name).collect(Collectors.joining(","));
        String emailParam = (email == null) ? "" : email;
        Timestamp fromDateParam = (from == null) ? Timestamp.valueOf("1970-01-01 00:00:00") : Timestamp.valueOf(from.atStartOfDay());
        Timestamp toDateParam = (to == null) ? Timestamp.valueOf("2100-01-01 00:00:00") : Timestamp.valueOf(to.atTime(23, 59, 59));
        BigDecimal minTotalParam = (minTotal == null) ? BigDecimal.ZERO : minTotal;
        BigDecimal maxTotalParam = (maxTotal == null) ? new BigDecimal("999999999") : maxTotal;

        return orderRepository.searchFiltered(
                statusListParam,
                emailParam,
                fromDateParam,
                toDateParam,
                minTotalParam,
                maxTotalParam,
                paging
        );
    }

    @Transactional(readOnly = true)
    public long countOrders() {
        return orderRepository.count();
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalSales() {
        BigDecimal total = orderRepository.sumTotalSales();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = false)
    public void requestReturn(int orderId, String userEmail){
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(!order.getUser().getEmail().equalsIgnoreCase(userEmail))
            throw new RuntimeException("Unauthorized user");

        if(!(order.getStatus() == OrderStatus.COMPLETED))
            throw new RuntimeException("Return not allowed");

        order.setStatus(OrderStatus.RETURN_REQUESTED);
        orderRepository.save(order);
    }

}
