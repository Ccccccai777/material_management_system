package com.lda.biz.entity.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.lda.biz.entity.Product;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class OutStockVO {

    private Long id;

    private String outNum;


    private Integer type;


    private String operator;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedTime;

    private Integer productNumber;


    private Long consumerId;


    private String remark;

    private Integer status;

    private Integer priority;

    private List<Object> products = new ArrayList<>();

    /*物资去向*/
    //去向名
    private String name;

    //地址
    private String address;

    //联系电话
    private String phone;

    //联系人
    private String contact;

    //排序
    private Integer sort;

}
