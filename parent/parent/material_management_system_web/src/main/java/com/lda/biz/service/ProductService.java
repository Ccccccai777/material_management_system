package com.lda.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.biz.entity.vo.ProductStockVO;
import com.lda.biz.entity.vo.ProductVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lda
 * @since 2020-10-11
 */
public interface ProductService extends IService<Product> {


    IPage<ProductVO> findProductByPage(Page<ProductVO> page, String categorys, ProductVO productVO);

    void add(ProductVO productVO);

    ProductVO edit(Long id);

    void updateProduct(ProductVO productVO);

    void moveToTrash(Long id);

    void delete(Long id);

    void restore(Long id);

    void publish(Long id);

    IPage<ProductStockVO> findOutboundMaterials(Page<ProductVO> page, String categorys, ProductVO productVO);

    IPage<ProductStockVO> findAllStocks(Page<ProductVO> page, String categorys, ProductVO productVO);
}
