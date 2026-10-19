package com.example.demo3.controller;

import com.example.demo3.common.Result;
import com.example.demo3.dto.OrderDTO;
import com.example.demo3.entity.Order;
import com.example.demo3.service.OrderService;
import com.example.demo3.vo.OrderVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/find/{productId}")
    public Result<OrderVo> getOrderById(@PathVariable Integer productId){
        OrderVo orderVo = orderService.getOrderById(productId);
        return Result.success(orderVo);
    }
}
