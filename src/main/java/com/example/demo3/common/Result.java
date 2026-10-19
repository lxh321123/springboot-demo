package com.example.demo3.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    public static <T>Result success(T data){
        return new Result(HttpStatus.OK.value(), "success", data);
    }
    public static Result success(){
        return new Result(HttpStatus.OK.value(), "success", null);
    }

    public static Result fail(String message){
        return new Result(500, message, null);
    }
}
