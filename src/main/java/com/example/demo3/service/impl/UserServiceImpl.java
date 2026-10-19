package com.example.demo3.service.impl;

import com.example.demo3.dto.UserDTO;
import com.example.demo3.entity.User;
import com.example.demo3.mapper.UserMapper;
import com.example.demo3.service.UserService;
import com.example.demo3.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Override
    public void addUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userMapper.insert(user);
    }

    @Override
    public void deleteUser(Integer id) {
        userMapper.delete(id);
    }

    @Override
    public UserVO findUser(Integer id) {
        User user = userMapper.select(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public UserVO findUserByName(String username) {
        User user = userMapper.selectByName(username);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> findByEntity(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        List<User> userList = userMapper.selectByEntity(user);
        List<UserVO> userVOList = new ArrayList<>();
        if (userList != null && !userList.isEmpty()) {
            userList.forEach(u -> {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(u, userVO);
                userVOList.add(userVO);
            });
        }
        return userVOList;
    }

    @Override
    public void editUser(User user) {
        userMapper.update(user);
    }
}
