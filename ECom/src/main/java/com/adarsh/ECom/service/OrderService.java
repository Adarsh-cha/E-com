package com.adarsh.ECom.service;

import com.adarsh.ECom.model.Order;
import com.adarsh.ECom.model.OrderItem;
import com.adarsh.ECom.model.Product;
import com.adarsh.ECom.model.dto.OrderItemRequest;
import com.adarsh.ECom.model.dto.OrderItemResponse;
import com.adarsh.ECom.model.dto.OrderRequest;
import com.adarsh.ECom.model.dto.OrderResponse;
import com.adarsh.ECom.repository.OrderRepo;
import com.adarsh.ECom.repository.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private OrderRepo orderRepo;

    public OrderResponse placeOrder(OrderRequest orderRequest) {

        Order order = new Order();
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderId(orderId);
        order.setCustomerName(orderRequest.customerName());
        order.setEmail(orderRequest.email());
        order.setStatus("Placed");
        order.setOrderDate(LocalDate.now());

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest orderItemRequest : orderRequest.items()) {
            Product product = productRepo.findById(orderItemRequest.productId()).
                    orElseThrow(() -> new RuntimeException("Product Not Found"));

            product.setStockQuantity(product.getStockQuantity() - orderItemRequest.quantity());
            productRepo.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(orderItemRequest.quantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.quantity())))
                    .order(order)
                    .build();

            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        Order saveOrder = orderRepo.save(order);

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            OrderItemResponse orderItemResponse = new OrderItemResponse(orderItem.getProduct().getName(),
                    orderItem.getQuantity(),
                    orderItem.getTotalPrice());

            orderItemResponses.add(orderItemResponse);
        }
        OrderResponse orderResponse = new OrderResponse(saveOrder.getOrderId(),
                saveOrder.getCustomerName(),
                saveOrder.getEmail(),
                saveOrder.getStatus(),
                saveOrder.getOrderDate(),
                orderItemResponses
                );
        return orderResponse;
    }

    @Transactional
    public List<OrderResponse> getAllOrderResponse() {
        List<Order> orders = orderRepo.findAll();

        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {

            List<OrderItemResponse> itemResponses = new ArrayList<>();


            for (OrderItem orderItem : order.getOrderItems()) {
                OrderItemResponse orderItemResponse = new OrderItemResponse(orderItem.getProduct().getName(),
                        orderItem.getQuantity(),
                        orderItem.getTotalPrice());

                itemResponses.add(orderItemResponse);
            }

            OrderResponse orderResponse = new OrderResponse(order.getOrderId(),
                    order.getCustomerName(),
                    order.getEmail(),
                    order.getStatus(),
                    order.getOrderDate(),
                    itemResponses);

            orderResponses.add(orderResponse);
        }

        return orderResponses;
    }
}
