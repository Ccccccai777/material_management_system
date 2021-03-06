package com.lda.system.enums;

/**
 * 用户状态
 * @Author zhangyukang
 * @Date 2020/5/29 12:29
 * @Version 1.0
 **/
public enum  UserStatusEnum {

    DISABLE(0),//禁用
    AVAILABLE(1);//启用

    private int statusCode;

    UserStatusEnum(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
