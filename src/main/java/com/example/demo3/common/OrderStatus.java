package com.example.demo3.common;

public enum OrderStatus {
    PAID(1, "已支付"),
    UNPAID(2, "未支付");
    private Integer code;
    private String message;
    OrderStatus(Integer code, String message){
        this.code = code;
        this.message = message;
    }
    public Integer getCode(){
        return code;
    }
    public String getMessage(){
    return message;
}
public static OrderStatus of(Integer code){
    for (OrderStatus s: values()){
        if(s.code == code){
            return s;
        }
    }
    throw new IllegalArgumentException("未知订单状态: " + code);
}
}