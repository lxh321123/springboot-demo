package com.example.demo3.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class Order {
    private Integer id;
    private BigInteger productId;
    private Integer count;
    private BigDecimal amount;
    private Integer status;
    private LocalDateTime createTime;
}
