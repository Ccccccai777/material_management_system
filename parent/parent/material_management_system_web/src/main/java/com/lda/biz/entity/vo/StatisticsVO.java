package com.lda.biz.entity.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

@Data
public class StatisticsVO {


    private List<LinkedHashMap<String,Object>> inStockInfoList;
    private List<LinkedHashMap<String,Object>> inStockProductInfoList;
    private List<LinkedHashMap<String,Object>> inStockInfoAddressList;

}
