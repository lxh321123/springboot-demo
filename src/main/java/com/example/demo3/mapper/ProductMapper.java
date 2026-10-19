package com.example.demo3.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mapper
public interface ProductMapper {
    @Select("select price from t_product where id = #{id}")
    BigDecimal getProductPrice(BigInteger id);

    @Update("update t_product set stock = stock - #{count}, version = version + 1 where id = #{productId} and stock >= #{count} and version = #{version}")
    Integer deductStock(@Param("productId") BigInteger productId, @Param("count") Integer count, @Param("version") Integer version);

    @Select("select version from t_product where id = #{productId}")
    Integer getVersion(BigInteger productId);
}
