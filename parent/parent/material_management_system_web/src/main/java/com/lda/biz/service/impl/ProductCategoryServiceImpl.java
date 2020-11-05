package com.lda.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.converter.ProductCategoryConverter;
import com.lda.biz.entity.ProductCategory;
import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.ProductCategoryTreeNodeVO;
import com.lda.biz.entity.vo.ProductCategoryVO;
import com.lda.biz.mapper.ProductCategoryMapper;
import com.lda.biz.service.ProductCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lda.biz.utils.CategoryTreeBuilder;
import com.lda.biz.utils.ListPageUtils;
import com.lda.handler.BusinessException;
import com.lda.response.ResultCode;
import com.lda.system.entity.Menu;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-10-11
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Autowired
    private ProductCategoryMapper productCategoryMapper;
    /**
     * 所有商品类别
     * @return
     */
    @Override
    public List<ProductCategoryVO> findAll() {
        List<ProductCategory> productCategories = this.baseMapper.selectList(null);
        return ProductCategoryConverter.converterToVOList(productCategories);
    }
    @Override
    public PageVO<ProductCategoryTreeNodeVO> productCategoryTree(Integer pageNum, Integer pageSize) {
        List<ProductCategoryVO> productCategoryVOS = this.findAll();
        List<ProductCategoryTreeNodeVO> treeNodeVO=ProductCategoryConverter.converterToTreeNodeVO(productCategoryVOS);
        List<ProductCategoryTreeNodeVO> tree = CategoryTreeBuilder.builder(treeNodeVO);
        List<ProductCategoryTreeNodeVO> page;
        if(pageSize!=null&&pageNum!=null){
            page= ListPageUtils.page(tree, pageSize, pageNum);
            return new PageVO<ProductCategoryTreeNodeVO>(tree.size(),page);
        }else {
            return new PageVO<ProductCategoryTreeNodeVO>(tree.size(), tree);
        }

    }

    @Override
    public List<ProductCategoryTreeNodeVO> getTwoCategoryTree() {
        List<ProductCategoryVO> productCategoryVOS = this.findAll();
        List<ProductCategoryTreeNodeVO> treeNodeVO=ProductCategoryConverter.converterToTreeNodeVO(productCategoryVOS);
        List<ProductCategoryTreeNodeVO> tree = CategoryTreeBuilder.buildTwoTree(treeNodeVO);
        return tree;
    }
    @Transactional
    @Override
    public void add(ProductCategoryVO productCategoryVO) {
        String productCategoryVOName = productCategoryVO.getName();
        Long productCategoryVOId = productCategoryVO.getId();

        QueryWrapper<ProductCategory> qw = new QueryWrapper<>();
        qw.eq("name",productCategoryVOName);
        Integer count = this.baseMapper.selectCount(qw);
        if (count>0) {
            throw  new BusinessException(ResultCode.CATEGORIES_ALREADY_EXIST.getCode(),ResultCode.CATEGORIES_ALREADY_EXIST.getMessage());
        }
        ProductCategory productCategory = new ProductCategory();
        BeanUtils.copyProperties(productCategoryVO,productCategory);
        productCategory.setCreateTime(new Date());
        productCategory.setModifiedTime(new Date());
        this.baseMapper.insert(productCategory);
    }

    @Override
    public ProductCategoryVO edit(Long id) {
        ProductCategory productCategory = this.baseMapper.selectById(id);
        if (productCategory == null) {
            throw  new BusinessException(ResultCode.CATEGORIES_NOT_EXIST.getCode(),ResultCode.CATEGORIES_NOT_EXIST.getMessage());
        }
        ProductCategoryVO productCategoryVO = new ProductCategoryVO();
        BeanUtils.copyProperties(productCategory,productCategoryVO);
        return productCategoryVO;
    }
    @Transactional
    @Override
    public void updateProductCategory(ProductCategoryVO productCategoryVO) {
        String productCategoryVOName = productCategoryVO.getName();
        Long productCategoryVOId = productCategoryVO.getId();

        QueryWrapper<ProductCategory> qw = new QueryWrapper<>();
        //查原来的对象数据
        qw.eq("id",productCategoryVOId);
        ProductCategory dbProductCategory = this.baseMapper.selectById(productCategoryVOId);
        //查分类名重复没
        QueryWrapper<ProductCategory> qw1 = new QueryWrapper<>();
        qw1.eq("name",productCategoryVOName);
        List<ProductCategory> productCategoryList = this.baseMapper.selectList(qw1);
        if( dbProductCategory!=null && productCategoryVO.getName().equals(dbProductCategory.getName())|| CollectionUtils.isEmpty(productCategoryList)){
            qw.eq("id",productCategoryVOId);
            ProductCategory productCategory = new ProductCategory();
            BeanUtils.copyProperties(productCategoryVO,productCategory);
            productCategory.setModifiedTime(new Date());
            this.baseMapper.update(productCategory,qw);
        }
        else if (!CollectionUtils.isEmpty(productCategoryList)) {
            throw  new BusinessException(ResultCode.CATEGORIES_ALREADY_EXIST.getCode(),ResultCode.CATEGORIES_ALREADY_EXIST.getMessage());
        }
    }


}
