package com.example.demo3.service;

import com.example.demo3.dto.UserDTO;
import com.example.demo3.entity.User;
import com.example.demo3.vo.UserVO;

import java.util.List;

public interface UserService {
    void addUser(UserDTO userDTO);

    void deleteUser(Integer id);

    UserVO findUser(Integer id);

    UserVO findUserByName(String username);

    List<UserVO> findByEntity(UserDTO userDTO);

    void editUser(User user);
}
