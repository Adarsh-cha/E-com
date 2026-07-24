package com.adarsh.ECom.controller;

import com.adarsh.ECom.model.dto.OrderRequest;
import com.adarsh.ECom.model.dto.OrderResponse;
import com.adarsh.ECom.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrder() {
        List<OrderResponse> response = orderService.getAllOrderResponse();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
