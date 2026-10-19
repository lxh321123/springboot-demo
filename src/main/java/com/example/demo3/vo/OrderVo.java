package com.example.demo3.vo;

import com.example.demo3.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class OrderVo {
    private BigInteger productId;
    private Integer count;
    private BigDecimal amount;
    private Integer status;
    private LocalDateTime createTime;
}
