package com.lda.biz.entity.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lda
 * @Date 2020/10/14 16:51
 * @Version 1.0
 **/
@Data
public class InStockDetailVO {

    private String inNum;

    private Integer status;

    private Integer type;

    private String operator;

    private SupplierVO supplierVO;

    private long total;/** 总数**/

    private List<InStockItemVO> itemVOS=new ArrayList<>();

}


