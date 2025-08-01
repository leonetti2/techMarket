package com.example.techmarket.dto;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @author lucio
 */

@Data
public class DashboardStatsDTO {
    private long totalOrders;
    private long totalProducts;
    private long totalUsers;
    private BigDecimal totalSales;
}
