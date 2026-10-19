package com.example.demo3.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class Product {
    private BigInteger id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Integer version;
}
