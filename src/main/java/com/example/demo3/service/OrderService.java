package com.example.demo3.service;

import com.example.demo3.dto.OrderDTO;
import com.example.demo3.vo.OrderVo;

public interface OrderService {
    void insertOrder(OrderDTO orderDTO);

    OrderVo getOrderById(Integer productId);
}
