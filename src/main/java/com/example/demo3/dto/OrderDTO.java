package com.example.demo3.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    @NotNull(message = "商品id不能为空")
    private BigInteger productId;

    @Min(value = 1)
    @NotNull(message = "商品数量不能为空")
    private Integer count;
}