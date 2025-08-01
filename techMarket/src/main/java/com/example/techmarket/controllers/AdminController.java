package com.example.techmarket.controllers;

import com.example.techmarket.dto.*;
import com.example.techmarket.entities.Order;
import com.example.techmarket.entities.OrderStatus;
import com.example.techmarket.entities.Product;
import com.example.techmarket.services.OrderService;
import com.example.techmarket.services.ProductService;
import com.example.techmarket.services.UserService;
import com.example.techmarket.support.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lucio
 */

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final OrderService orderService;
    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/dashboard-stats")
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalOrders(orderService.countOrders());
        stats.setTotalProducts(productService.countProducts());
        stats.setTotalUsers(userService.countUsers());
        stats.setTotalSales(orderService.getTotalSales());
        return stats;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> dtos = productService.getAllProduct().stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/products/paged")
    public ResponseEntity<Page<ProductDTO>> getAllProductsPaged(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "9") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        Page<Product> result = productService.getAllProduct(pageNumber, pageSize, sortBy);
        Page<ProductDTO> dtoPage = result.map(this::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProduct(@PathVariable int id) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseMessage("Product not found"));
            }
            return ResponseEntity.ok(toDTO(product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Error retrieving product: " + e.getMessage()));
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            ProductDTO dto = toDTO(productService.createProduct(product));
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("Error creating product: " + e.getMessage()));
        }
    }

    @PostMapping("/products/{id}/image")
    public ResponseEntity<?> uploadProductImage(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        var product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Product not found"));
        }

        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Invalid file or not an image"));
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Image too large (max 2MB)"));
        }

        try {
            String uploadsDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            File dir = new File(uploadsDir);
            if (!dir.exists() && !dir.mkdirs()) {
                return ResponseEntity.status(500).body("Unable to create uploads folder");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                return ResponseEntity.badRequest().body(new ResponseMessage("File without extension"));
            }
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String filename = "product_" + id + "_" + System.currentTimeMillis() + ext;
            String filePath = uploadsDir + filename;

            file.transferTo(new File(filePath));

            product.setImageUrl("/images/" + filename);
            productService.updateProduct(id, product);

            return ResponseEntity.ok(product.getImageUrl());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ResponseMessage("Error saving image: " + e.getMessage()));
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody Product product) {
        try {
            ProductDTO dto = toDTO(productService.updateProduct(id, product));
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("Error while updating product: " + e.getMessage()));
        }
    }

    @PutMapping("/products/{id}/updateStock")
    public ResponseEntity<?> updateStock(@PathVariable int id, @RequestParam int quantity) {
        try {
            ProductDTO dto = toDTO(productService.updateStock(id, quantity));
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("Error while updating stock: " + e.getMessage()));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage("Error deleting product: " + e.getMessage()));
        }
    }

    @GetMapping("/products/count")
    public long countProductsAdmin() {
        return productService.countProducts();
    }

    private ProductDTO toDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setBrand(p.getBrand());
        dto.setCateogory(p.getCategory());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setQuantity(p.getQuantity());
        dto.setImageUrl(p.getImageUrl());
        return dto;
    }

    @GetMapping("/orders/paged")
    public ResponseEntity<Page<OrderDTO>> getAllOrdersPaged(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        Page<Order> orders = orderService.getAllOrders(pageNumber, pageSize, sortBy);
        Page<OrderDTO> dtos = orders.map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getId());
            dto.setUserId(order.getUser().getId());

            var user = order.getUser();
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setSecondNamen(user.getLastName());
            userDTO.setEmail(user.getEmail());
            userDTO.setTelephoneNumber(user.getTelephoneNumber());
            userDTO.setAddress(user.getAddress());
            userDTO.setRole(user.getRole());
            dto.setUser(userDTO);

            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemDTO itemDto = new OrderItemDTO();
                itemDto.setOrderId(order.getId());
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                return itemDto;
            }).toList());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setStatus(order.getStatus());
            dto.setCreatedAt(order.getCreatedAt());
            return dto;
        });
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/orders/filtered")
    public ResponseEntity<Page<OrderDTO>> getAllOrdersFiltered(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "status", required = false) List<OrderStatus> status,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "minTotal", required = false) BigDecimal minTotal,
            @RequestParam(value = "maxTotal", required = false) BigDecimal maxTotal
    ) {
        Page<Order> orders = orderService.getAllOrdersFiltered(
                pageNumber, pageSize, sortBy, status, email, from, to, minTotal, maxTotal
        );
        Page<OrderDTO> dtos = orders.map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getId());
            dto.setUserId(order.getUser().getId());

            var user = order.getUser();
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setSecondNamen(user.getLastName());
            userDTO.setEmail(user.getEmail());
            userDTO.setTelephoneNumber(user.getTelephoneNumber());
            userDTO.setAddress(user.getAddress());
            userDTO.setRole(user.getRole());
            dto.setUser(userDTO);

            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemDTO itemDto = new OrderItemDTO();
                itemDto.setOrderId(order.getId());
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                return itemDto;
            }).toList());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setStatus(order.getStatus());
            dto.setCreatedAt(order.getCreatedAt());
            return dto;
        });
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable int orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseMessage("Order not found"));
            }
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getId());
            dto.setUserId(order.getUser().getId());
            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemDTO itemDto = new OrderItemDTO();
                itemDto.setOrderId(order.getId());
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                return itemDto;
            }).toList());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setStatus(order.getStatus());
            dto.setCreatedAt(order.getCreatedAt());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Error retrieving order: " + e.getMessage()));
        }
    }

    @GetMapping("/orders/count")
    public long countOrdersAdmin() {
        return orderService.countOrders();
    }

    @GetMapping("/orders/total-sales")
    public BigDecimal getTotalSalesAdmin() {
        return orderService.getTotalSales();
    }

    @GetMapping("/products/search")
    public Map<String, Object> searchProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> page = productService.searchProducts(
                (name == null || name.isBlank()) ? null : name,
                (category == null || category.isBlank()) ? null : category,
                pageable
        );

        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("pageNumber", page.getNumber());
        response.put("pageSize", page.getSize());
        return response;
    }
}