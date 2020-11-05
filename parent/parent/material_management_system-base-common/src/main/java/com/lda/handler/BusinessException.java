package com.lda.handler;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    @ApiModelProperty("状态码")
    private  Integer code;
    @ApiModelProperty("错误信息")
    private  String errMsg;
}
