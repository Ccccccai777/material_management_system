package com.lda.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.ProductCategoryTreeNodeVO;
import com.lda.biz.entity.vo.ProductCategoryVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lda
 * @since 2020-10-11
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    List<ProductCategoryVO> findAll();
    PageVO<ProductCategoryTreeNodeVO> productCategoryTree(Integer pageNum, Integer pageSize);


    List<ProductCategoryTreeNodeVO> getTwoCategoryTree();

    void add(ProductCategoryVO productCategoryVO);

    ProductCategoryVO edit(Long id);

    void updateProductCategory(ProductCategoryVO productCategoryVO);


}
