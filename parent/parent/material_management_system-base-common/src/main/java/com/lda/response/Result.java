package com.lda.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共返回结果
 * @author lda
*
* */
@Data
public class Result {

    @ApiModelProperty("是否成功")
    private Boolean success;
    @ApiModelProperty("返回码")
    private Integer code;
    @ApiModelProperty("返回消息")
    private String message;
    @ApiModelProperty("返回数据")
    private Map<String,Object> data=new HashMap();
    /**
     * 构造方法私有化 里面的方法都是静态方法 达到保护属性的作用*/
    private Result(){

    }
    public static Result ok(){
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
    }
    public static Result error(){
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.COMMON_FAIL.getCode());
        result.setMessage(ResultCode.COMMON_FAIL.getMessage());
        return result;
    }
    public static Result error(Integer code,String message){
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    /**自定义返回成功结果
     * @param  success
     * @return
     * */
    public  Result success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public  Result message(String message){
        this.setMessage(message);
        return this;
    }
    public Result code(Integer code){
        this.setCode(code);
        return this;
    }
    public Result data(String key,Object values){
        this.data.put(key,values);
        return this;
    }
    public Result data(Map<String,Object> map){
        this.data(map);
        return this;
    }
}
