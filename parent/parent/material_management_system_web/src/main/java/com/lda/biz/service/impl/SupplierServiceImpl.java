package com.lda.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.converter.SupplierConverter;
import com.lda.biz.entity.Supplier;
import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.SupplierVO;
import com.lda.biz.mapper.SupplierMapper;
import com.lda.biz.service.SupplierService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.lda.handler.BusinessException;
import com.lda.response.ResultCode;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-10-13
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Override
    public PageVO<SupplierVO> findSupplierList(Integer pageNum, Integer pageSize, SupplierVO supplierVO) {
        Page<Supplier> page=new Page<>(pageNum,pageSize);
        QueryWrapper<Supplier> wrapper=getWrapper(supplierVO);
        IPage<Supplier> page1 = this.baseMapper.selectPage(page, wrapper);
        List<Supplier> supplierList = page1.getRecords();
        List<SupplierVO> supplierVOS= SupplierConverter.converterToVOList(supplierList);
        return new PageVO<SupplierVO>(page1.getTotal(),supplierVOS);
    }
    @Transactional
    @Override
    public Supplier addSupplier(SupplierVO supplierVO) {

        String supplierVOName = supplierVO.getName();
        Long supplierVOId = supplierVO.getId();

        QueryWrapper<Supplier> qw = new QueryWrapper<Supplier>();
        qw.eq("name", supplierVO.getName()).eq("address", supplierVO.getAddress())
                .eq("email", supplierVO.getEmail()).eq("phone", supplierVO.getPhone())
                .eq("contact", supplierVO.getContact());
        Integer count = this.baseMapper.selectCount(qw);
        if (count>0) {
            throw  new BusinessException(ResultCode.MATERIAL_SOURCE_ALREADY_EXIST.getCode(),ResultCode.MATERIAL_SOURCE_ALREADY_EXIST.getMessage());
        }
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierVO,supplier);
        supplier.setModifiedTime(new Date());
        supplier.setCreateTime(new Date());
        this.baseMapper.insert(supplier);
        return supplier;
    }

    @Override
    public SupplierVO edit(Long id) {
        Supplier supplier = this.baseMapper.selectById(id);
        if (supplier==null) {
            throw  new BusinessException(ResultCode.MATERIAL_SOURCE_NOT_EXIST.getCode(),ResultCode.MATERIAL_SOURCE_NOT_EXIST.getMessage());
        }
        SupplierVO supplierVO = SupplierConverter.converterToSupplierVO(supplier);
        return supplierVO;
    }
    @Transactional
    @Override
    public void updateSupplier(SupplierVO supplierVO) {
        String supplierVOName = supplierVO.getName();
        Long supplierVOId = supplierVO.getId();

        QueryWrapper<Supplier> qw = new QueryWrapper<>();
        //查原来的对象数据
        qw.eq("id",supplierVOId);
        Supplier dbSupplier = this.baseMapper.selectById(supplierVOId);
        //查角色名重复没
        QueryWrapper<Supplier> qw1 = new QueryWrapper<>();
        qw1.eq("name",supplierVOName);
        List<Supplier> supplierList = this.baseMapper.selectList(qw1);
        if( dbSupplier!=null && supplierVO.getName().equals(dbSupplier.getName())|| CollectionUtils.isEmpty(supplierList)){
            qw.eq("id",supplierVOId);
            Supplier supplier = new Supplier();
            BeanUtils.copyProperties(supplierVO,supplier);
            supplier.setModifiedTime(new Date());
            this.baseMapper.update(supplier,qw);
        }
        else if (!CollectionUtils.isEmpty(supplierList)) {
            throw  new BusinessException(ResultCode.MATERIAL_SOURCE_ALREADY_EXIST.getCode(),ResultCode.MATERIAL_SOURCE_ALREADY_EXIST.getMessage());
        }
    }
    @Transactional
    @Override
    public void delete(Long id) {
        Supplier supplier = this.baseMapper.selectById(id);
        if (supplier==null) {
            throw  new BusinessException(ResultCode.MATERIAL_SOURCE_NOT_EXIST.getCode(),ResultCode.MATERIAL_SOURCE_NOT_EXIST.getMessage());
        }
        this.baseMapper.deleteById(id);
    }

    @Override
    public List<SupplierVO> findAll() {
        List<Supplier> supplierList = this.baseMapper.selectList(null);
        return SupplierConverter.converterToVOList(supplierList);
    }

    private QueryWrapper<Supplier> getWrapper(SupplierVO supplierVO){
        System.out.println(supplierVO);
        QueryWrapper<Supplier> qw = new QueryWrapper<>();
        qw.orderByAsc("sort");
        if (supplierVO != null) {
            if (!StringUtils.isEmpty(supplierVO.getName())) {
                qw.eq("name",supplierVO.getName());
            }
            if (!StringUtils.isEmpty(supplierVO.getContact())) {
                qw.like("contact",supplierVO.getContact());
            }
            if (!StringUtils.isEmpty(supplierVO.getAddress())) {
                qw.like("address",supplierVO.getAddress());
            }

        }
        return qw;
    }
}
