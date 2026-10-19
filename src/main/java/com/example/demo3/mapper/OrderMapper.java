package com.example.demo3.mapper;

import com.example.demo3.entity.Order;
import com.example.demo3.vo.OrderVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
    @Insert("insert into t_order" +
            "(product_id, count, amount, status, create_time) " +
            "values " +
            "(#{productId}, #{count}, #{amount}, #{status}, #{createTime})" )
    @Options(useGeneratedKeys = true)
    void insertOrder(Order order);

    @Select("select product_id, count, amount, status, create_time from t_order where id = #{productId}")
    OrderVo getOrderById(Integer productId);
}
