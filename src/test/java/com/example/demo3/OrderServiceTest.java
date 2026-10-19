package com.example.demo3;

import com.example.demo3.dto.OrderDTO;
import com.example.demo3.entity.Order;
import com.example.demo3.entity.Product;
import com.example.demo3.exception.BusinessException;
import com.example.demo3.mapper.OrderMapper;
import com.example.demo3.mapper.ProductMapper;
import com.example.demo3.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class orderServiceTest{
    @Mock
    ProductMapper productMapper;
    @Mock
    OrderMapper orderMapper;
    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void 库存充足时下单(){
        when(productMapper.getVersion(BigInteger.ONE)).thenReturn(0);
        when(productMapper.deductStock(BigInteger.ONE, 1, 0)).thenReturn(1);
        when(productMapper.getProductPrice(BigInteger.ONE)).thenReturn(new BigDecimal(99.00));

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setProductId(BigInteger.ONE);
        orderDTO.setCount(1);

        orderService.insertOrder(orderDTO);
        verify(orderMapper).insertOrder(any(Order.class));
    }

    @Test
    void 库存不足时下单(){
        when(productMapper.getVersion(BigInteger.ONE)).thenReturn(0);
        when(productMapper.deductStock(BigInteger.ONE, 1, 0)).thenReturn(0);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setProductId(BigInteger.ONE);
        orderDTO.setCount(1);

        assertThrows(BusinessException.class, () -> orderService.insertOrder(orderDTO));
    }
}