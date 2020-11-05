package com.lda.response;

public interface CustomizeResultCode {
    /**
    * 获取错误状态码信息
     * @return 错误状态码
    * */
    Integer getCode();
    /**
     * 获取错误信息
     * @return 错误信息
     * */
    String getMessage();
}
