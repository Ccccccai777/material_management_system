package com.lda.biz.converter;


import com.lda.biz.entity.InStock;
import com.lda.biz.entity.Supplier;
import com.lda.biz.entity.vo.InStockVO;
import com.lda.biz.entity.vo.StatisticsVO;
import com.lda.biz.mapper.SupplierMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author lda
 * @Date 2020/10/14 09:58
 * @Version 1.0
 **/
@Component
public class InStockConverter {

    @Autowired
    private SupplierMapper supplierMapper;



    /**
     * 转voList
     * @param inStocks
     * @return
     */
    public   List<InStockVO> converterToVOList(List<InStock> inStocks) {
        List<InStockVO> inStockVOS=new ArrayList<>();
        if(!CollectionUtils.isEmpty(inStocks)){
            for (InStock inStock : inStocks) {
                InStockVO inStockVO = new InStockVO();
                BeanUtils.copyProperties(inStock,inStockVO);
                Supplier supplier = supplierMapper.selectById(inStock.getSupplierId());
                if(supplier!=null){
                    inStockVO.setSupplierName(supplier.getName());
                    inStockVO.setPhone(supplier.getPhone());
                }
                inStockVOS.add(inStockVO);
            }
        }
        return inStockVOS;
    }
}
