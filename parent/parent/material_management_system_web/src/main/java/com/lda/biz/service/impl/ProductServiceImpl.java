package com.lda.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.entity.Product;
import com.lda.biz.entity.ProductCategory;
import com.lda.biz.entity.vo.ProductStockVO;
import com.lda.biz.entity.vo.ProductVO;
import com.lda.biz.mapper.ProductMapper;
import com.lda.biz.mapper.ProductStockMapper;
import com.lda.biz.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lda.handler.BusinessException;
import com.lda.response.Result;
import com.lda.response.ResultCode;
import com.lda.system.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-10-11
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductStockMapper productStockMapper;

    @Override
    public IPage<ProductVO> findProductByPage(Page<ProductVO> page, String categorys, ProductVO productVO) {
        //设置分类id
        if(!StringUtils.isEmpty(categorys)){
            setCategoryId(productVO,categorys);
        }

        QueryWrapper<Product> wrapper = getWrapper(productVO,0);
        System.out.println("--------------------"+ productVO);
        return  productMapper.findProductByPage(page,wrapper);
    }

    private  void  setCategoryId(ProductVO productVO,String categorys){
        String[] split = categorys.split(",");
        switch (split.length) {
            case 1:
                productVO.setOneCategoryId(Long.parseLong(split[0]));
                break;
            case 2:
                productVO.setOneCategoryId(Long.parseLong(split[0]));
                productVO.setTwoCategoryId(Long.parseLong(split[1]));
                break;
            case 3:
                productVO.setOneCategoryId(Long.parseLong(split[0]));
                productVO.setTwoCategoryId(Long.parseLong(split[1]));
                productVO.setThreeCategoryId(Long.parseLong(split[2]));
                break;
        }
    }
    @Transactional
    @Override
    public void add(ProductVO productVO) {
        Product product = new Product();
        Integer count = this.baseMapper.selectCount(new QueryWrapper<Product>().eq("name",productVO.getName()));
        if (count >0) {
            throw new BusinessException(ResultCode.PRODUCT_ALREADY_EXIST.getCode(),ResultCode.PRODUCT_ALREADY_EXIST.getMessage());
        }
        BeanUtils.copyProperties(productVO,product);
        product.setCreateTime(new Date());
        product.setModifiedTime(new Date());
        product.setStatus(2);//设置未审核
        product.setPNum(UUID.randomUUID().toString().substring(0,32));
        @NotNull(message = "分类不能为空") Long[] categoryKeys = productVO.getCategoryKeys();
        product.setOneCategoryId(categoryKeys[0]);
        product.setTwoCategoryId(categoryKeys[1]);
        product.setThreeCategoryId(categoryKeys[2]);
        this.baseMapper.insert(product);
    }

    @Override
    public ProductVO edit(Long id) {
        Product product = this.baseMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST.getCode(), ResultCode.PRODUCT_NOT_EXIST.getMessage());
        }
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product,productVO);
        return productVO;
    }
    @Transactional
    @Override
    public void updateProduct(ProductVO productVO) {
        String productVOName = productVO.getName();
        Long productVOId = productVO.getId();

        QueryWrapper<Product> qw = new QueryWrapper<>();
        //查原来的对象数据
        qw.eq("id",productVOId);
        Product dbProduct = this.baseMapper.selectById(productVOId);
        //查分类名重复没
        QueryWrapper<Product> qw1 = new QueryWrapper<>();
        qw1.eq("name",productVOName);
        List<Product> productList = this.baseMapper.selectList(qw1);
        if( dbProduct!=null && productVO.getName().equals(dbProduct.getName())|| CollectionUtils.isEmpty(productList)){
            qw.eq("id",productVOId);
            Product product = new Product();
            @NotNull(message = "分类不能为空") Long[] categoryKeys = productVO.getCategoryKeys();
            productVO.setOneCategoryId(categoryKeys[0]);
            productVO.setTwoCategoryId(categoryKeys[1]);
            productVO.setThreeCategoryId(categoryKeys[2]);
            BeanUtils.copyProperties(productVO,product);
            product.setModifiedTime(new Date());
            this.baseMapper.update(product,qw);
        }
        else if (!CollectionUtils.isEmpty(productList)) {
            throw  new BusinessException(ResultCode.PRODUCT_ALREADY_EXIST.getCode(),ResultCode.PRODUCT_ALREADY_EXIST.getMessage());
        }
    }
    @Transactional
    @Override
    public void moveToTrash(Long id) {
        Product product = this.baseMapper.selectById(id);
        if (product == null ) {
            throw  new BusinessException(ResultCode.PRODUCT_NOT_EXIST.getCode(),ResultCode.PRODUCT_NOT_EXIST.getMessage());
        }
        if(product.getStatus()!=0){
            throw  new BusinessException(ResultCode.PRODUCT_STATUS_ERROR.getCode(),ResultCode.PRODUCT_STATUS_ERROR.getMessage());
        }
        product.setStatus(1);
        this.baseMapper.updateById(product);
    }
    @Transactional
    @Override
    public void delete(Long id) {
        Product product = this.baseMapper.selectById(id);
        if (product == null ) {
            throw  new BusinessException(ResultCode.PRODUCT_NOT_EXIST.getCode(),ResultCode.PRODUCT_NOT_EXIST.getMessage());
        }
        if(product.getStatus()==1 || product.getStatus()==2){
            this.baseMapper.deleteById(id);
        }else {
            throw  new BusinessException(ResultCode.PRODUCT_STATUS_ERROR.getCode(),ResultCode.PRODUCT_STATUS_ERROR.getMessage());

        }

    }
    @Transactional
    @Override
    public void restore(Long id) {
        Product product = this.baseMapper.selectById(id);
        if (product == null ) {
            throw  new BusinessException(ResultCode.PRODUCT_NOT_EXIST.getCode(),ResultCode.PRODUCT_NOT_EXIST.getMessage());
        }
        if(product.getStatus()!=1){
            throw  new BusinessException(ResultCode.PRODUCT_STATUS_ERROR.getCode(),ResultCode.PRODUCT_STATUS_ERROR.getMessage());
        }
        product.setStatus(0);
        this.baseMapper.updateById(product);
    }
    @Transactional
    @Override
    public void publish(Long id) {
        Product product = this.baseMapper.selectById(id);
        if (product == null ) {
            throw  new BusinessException(ResultCode.PRODUCT_NOT_EXIST.getCode(),ResultCode.PRODUCT_NOT_EXIST.getMessage());
        }
        if(product.getStatus()!=2){
            throw  new BusinessException(ResultCode.PRODUCT_STATUS_ERROR.getCode(),ResultCode.PRODUCT_STATUS_ERROR.getMessage());
        }
        product.setStatus(0);
        this.baseMapper.updateById(product);
    }

    @Override
    public IPage<ProductStockVO> findOutboundMaterials(Page<ProductVO> page, String categorys, ProductVO productVO) {
        //设置分类id
        if(!StringUtils.isEmpty(categorys)){
            setCategoryId(productVO,categorys);
        }
        return productStockMapper.findOutboundMaterials(page,getWrapper(productVO,1));
    }

    @Override
    public IPage<ProductStockVO> findAllStocks(Page<ProductVO> page, String categorys, ProductVO productVO) {
        //设置分类id
        if(!StringUtils.isEmpty(categorys)){
            setCategoryId(productVO,categorys);
        }
        return productStockMapper.findAllStocks(page,getWrapper(productVO,0));
    }

    private QueryWrapper<Product> getWrapper(ProductVO productVO,Integer type){
        QueryWrapper<Product> qw = new QueryWrapper<>();
        if(type==1){
            qw.gt("stock",0);
        }
        if (productVO != null) {
            if (!StringUtils.isEmpty(productVO.getStatus())) {
                qw.eq("status",productVO.getStatus());
            }
            if (!StringUtils.isEmpty(productVO.getOneCategoryId())) {
                qw.eq("one_category_id",productVO.getOneCategoryId());
                if (!StringUtils.isEmpty(productVO.getTwoCategoryId())) {
                    qw.eq("two_category_id",productVO.getTwoCategoryId());
                    if (!StringUtils.isEmpty(productVO.getThreeCategoryId())) {
                        qw.eq("three_category_id",productVO.getThreeCategoryId());
                }

                }
                qw.orderByAsc("sort");
                return qw;
            }


            if (!StringUtils.isEmpty(productVO.getName())) {
                qw.like("name",productVO.getName());
                return qw;
            }

        }
        return qw;
    }
}
