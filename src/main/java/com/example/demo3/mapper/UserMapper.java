package com.example.demo3.mapper;

import com.example.demo3.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("insert into t_user(username, password, email) values" +
            "(#{username}, #{password}, #{email})")
    void insert(User user);

    @Delete("delete from t_user where id = #{id}")
    void delete(Integer id);

    @Select("select * from t_user where id = #{id}")
    User select(Integer id);

    @Select("select * from t_user where username = #{username}")
    User selectByName(String username);

    List<User> selectByEntity(User user);

    void update(User user);
}
