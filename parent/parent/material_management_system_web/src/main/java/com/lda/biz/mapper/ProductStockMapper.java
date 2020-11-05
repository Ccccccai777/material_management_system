package com.lda.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.entity.Product;
import com.lda.biz.entity.ProductStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lda.biz.entity.vo.ProductStockVO;
import com.lda.biz.entity.vo.ProductVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lda
 * @since 2020-10-11
 */
public interface ProductStockMapper extends BaseMapper<ProductStock> {

    IPage<ProductStockVO> findOutboundMaterials(Page<ProductVO> page, @Param(Constants.WRAPPER) QueryWrapper<Product> wrapper);

    IPage<ProductStockVO> findAllStocks(Page<ProductVO> page, QueryWrapper<Product> wrapper);
}
