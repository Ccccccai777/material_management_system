package com.lda.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.converter.ConsumerConverter;
import com.lda.biz.converter.SupplierConverter;
import com.lda.biz.entity.Consumer;
import com.lda.biz.entity.Consumer;
import com.lda.biz.entity.Supplier;
import com.lda.biz.entity.vo.ConsumerVO;
import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.ConsumerVO;
import com.lda.biz.entity.vo.SupplierVO;
import com.lda.biz.mapper.ConsumerMapper;
import com.lda.biz.service.ConsumerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lda.handler.BusinessException;
import com.lda.response.ResultCode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-10-17
 */
@Service
public class ConsumerServiceImpl extends ServiceImpl<ConsumerMapper, Consumer> implements ConsumerService {
    @Transactional
    @Override
    public void updateConsumer(ConsumerVO consumerVO) {
        String consumerVOName = consumerVO.getName();
        Long consumerVOId = consumerVO.getId();

        QueryWrapper<Consumer> qw = new QueryWrapper<>();
        //查原来的对象数据
        qw.eq("id",consumerVOId);
        Consumer dbConsumer = this.baseMapper.selectById(consumerVOId);
        //查角色名重复没
        QueryWrapper<Consumer> qw1 = new QueryWrapper<>();
        qw1.eq("name",consumerVOName);
        List<Consumer> consumerList = this.baseMapper.selectList(qw1);
        if( dbConsumer!=null && consumerVO.getName().equals(dbConsumer.getName())|| CollectionUtils.isEmpty(consumerList)){
            qw.eq("id",consumerVOId);
            Consumer consumer = new Consumer();
            BeanUtils.copyProperties(consumerVO,consumer);
            consumer.setModifiedTime(new Date());
            this.baseMapper.update(consumer,qw);
        }
        else if (!CollectionUtils.isEmpty(consumerList)) {
            throw  new BusinessException(ResultCode.WHERE_TO_GO_ALREADY_EXIST.getCode(),ResultCode.WHERE_TO_GO_ALREADY_EXIST.getMessage());
        }
    }
    @Transactional
    @Override
    public void delete(Long id) {
        Consumer consumer = this.baseMapper.selectById(id);
        if (consumer==null) {
            throw  new BusinessException(ResultCode.WHERE_TO_GO_NOT_EXIST.getCode(),ResultCode.WHERE_TO_GO_NOT_EXIST.getMessage());
        }
        this.baseMapper.deleteById(id);
    }

    @Override
    public List<ConsumerVO> findAll() {
        List<Consumer> ConsumerList = this.baseMapper.selectList(null);
        return ConsumerConverter.converterToVOList(ConsumerList);
    }
    @Transactional
    @Override
    public Consumer addConsumer(ConsumerVO consumerVO) {
        String ConsumerVOName = consumerVO.getName();
        Long ConsumerVOId = consumerVO.getId();

        QueryWrapper<Consumer> qw = new QueryWrapper<Consumer>();
        qw.eq("name", consumerVO.getName()).eq("address", consumerVO.getAddress())
                  .eq("phone", consumerVO.getPhone())
                .eq("contact", consumerVO.getContact());
        Integer count = this.baseMapper.selectCount(qw);
        if (count>0) {
            throw  new BusinessException(ResultCode.WHERE_TO_GO_ALREADY_EXIST.getCode(),ResultCode.WHERE_TO_GO_ALREADY_EXIST.getMessage());
        }
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(consumerVO,consumer);
        consumer.setCreateTime(new Date());
        consumer.setModifiedTime(new Date());
        this.baseMapper.insert(consumer);
        return consumer;
    }

    @Override
    public PageVO<ConsumerVO> findConsumerList(Integer currentPage, Integer pageSize, ConsumerVO consumerVO) {
        Page<Consumer> page=new Page<>(currentPage,pageSize);
        QueryWrapper<Consumer> wrapper=getWrapper(consumerVO);
        IPage<Consumer> page1 = this.baseMapper.selectPage(page, wrapper);
        List<Consumer> consumerList = page1.getRecords();
        List<ConsumerVO> consumerVOS= ConsumerConverter.converterToVOList(consumerList);
        return new PageVO<ConsumerVO>(page1.getTotal(),consumerVOS);
    }

    @Override
    public ConsumerVO edit(Long id) {
        Consumer consumer = this.baseMapper.selectById(id);
        if (consumer==null) {
            throw  new BusinessException(ResultCode.WHERE_TO_GO_NOT_EXIST.getCode(),ResultCode.WHERE_TO_GO_NOT_EXIST.getMessage());
        }
        ConsumerVO consumerVO = ConsumerConverter.converterToConsumerVO(consumer);
        return consumerVO;
    }

    private QueryWrapper<Consumer> getWrapper(ConsumerVO consumerVO){
        System.out.println(consumerVO);
        QueryWrapper<Consumer> qw = new QueryWrapper<>();
        qw.orderByAsc("sort");
        if (consumerVO != null) {
            if (!StringUtils.isEmpty(consumerVO.getName())) {
                qw.eq("name",consumerVO.getName());
            }
            if (!StringUtils.isEmpty(consumerVO.getContact())) {
                qw.like("contact",consumerVO.getContact());
            }
            if (!StringUtils.isEmpty(consumerVO.getAddress())) {
                qw.like("address",consumerVO.getAddress());
            }

        }
        return qw;
    }
}
