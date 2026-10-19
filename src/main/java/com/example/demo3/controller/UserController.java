package com.example.demo3.controller;

import com.example.demo3.common.Result;
import com.example.demo3.dto.UserDTO;
import com.example.demo3.entity.User;
import com.example.demo3.service.UserService;
import com.example.demo3.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/add")
    public Result addUser(@Validated @RequestBody UserDTO userDTO){
        userService.addUser(userDTO);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result deleteUser(@PathVariable Integer id){
        userService.deleteUser(id);
        return Result.success();
    }

    @GetMapping("/find/{id}")
    public Result<UserVO> findUserById(@PathVariable Integer id){
        return Result.success(userService.findUser(id));
    }

    @GetMapping("/find")
    public Result<UserVO> findUserByName(@RequestParam("username") String username){
        return Result.success(userService.findUserByName(username));
    }

    @PostMapping("/search")
    public Result<List<UserVO>> findByEntity(@RequestBody UserDTO userDTO){
        List<UserVO> userVOList = userService.findByEntity(userDTO);
        return Result.success(userVOList);
    }
    @PutMapping("/update")
    public Result editUser(@RequestBody User user){
        userService.editUser(user);
        return Result.success();
    }
}
