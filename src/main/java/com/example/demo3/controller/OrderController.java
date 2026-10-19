package com.example.demo3.controller;

import com.example.demo3.common.Result;
import com.example.demo3.dto.OrderDTO;
import com.example.demo3.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/insert")
    public Result insertOrder(@Validated @RequestBody OrderDTO orderDTO){
        orderService.insertOrder(orderDTO);
        return Result.success();
    }
}
