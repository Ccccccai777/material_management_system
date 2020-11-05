package com.lda.biz.converter;

import com.lda.biz.entity.ProductCategory;
import com.lda.biz.entity.vo.ProductCategoryTreeNodeVO;
import com.lda.biz.entity.vo.ProductCategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryConverter {

    public static List<ProductCategoryVO> converterToVOList(List<ProductCategory> productCategories) {
        List<ProductCategoryVO> productCategoryVOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(productCategories)) {
            for (ProductCategory productCategory : productCategories) {
                ProductCategoryVO productCategoryVO = new ProductCategoryVO();
                BeanUtils.copyProperties(productCategory,productCategoryVO);
                productCategoryVOs.add(productCategoryVO);
            }
        }
        return productCategoryVOs;
    }
    /**
     * 转树节点
     * @param productCategoryVOList
     * @return
     */
    public static List<ProductCategoryTreeNodeVO> converterToTreeNodeVO(List<ProductCategoryVO> productCategoryVOS) {
        List<ProductCategoryTreeNodeVO> nodes=new ArrayList<>();
        if(!CollectionUtils.isEmpty(productCategoryVOS)){
            for (ProductCategoryVO productCategoryVO : productCategoryVOS) {
                ProductCategoryTreeNodeVO productCategoryTreeNodeVO = new ProductCategoryTreeNodeVO();
                BeanUtils.copyProperties(productCategoryVO,productCategoryTreeNodeVO);
                nodes.add(productCategoryTreeNodeVO);
            }
        }
        return nodes;
    }
}
