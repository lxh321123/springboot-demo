package com.example.demo3.service.impl;

import com.example.demo3.common.OrderStatus;
import com.example.demo3.dto.OrderDTO;
import com.example.demo3.entity.Order;
import com.example.demo3.exception.BusinessException;
import com.example.demo3.mapper.OrderMapper;
import com.example.demo3.mapper.ProductMapper;
import com.example.demo3.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrder(OrderDTO orderDTO) {
        int maxRetry = 3;
        Integer rows = 0;
        for (int i = 0; i < maxRetry; i++) {
            Integer version = productMapper.getVersion(orderDTO.getProductId());
            rows = productMapper.deductStock(orderDTO.getProductId(), orderDTO.getCount(), version);
            if(rows > 0) break;
        }
        if(rows == 0) throw new BusinessException("库存不足！");
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        BigDecimal productPrice = productMapper.getProductPrice(order.getProductId());
        order.setAmount(productPrice.multiply(BigDecimal.valueOf(order.getCount())));
        order.setCreateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID.getCode());
        orderMapper.insertOrder(order);
    }
}
