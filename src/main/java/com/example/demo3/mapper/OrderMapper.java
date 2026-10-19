package com.example.demo3.mapper;

import com.example.demo3.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface OrderMapper {
    @Insert("insert into t_order" +
            "(product_id, count, amount, status, create_time) " +
            "values " +
            "(#{productId}, #{count}, #{amount}, #{status}, #{createTime})" )
    @Options(useGeneratedKeys = true)
    void insertOrder(Order order);
}
