package com.lda.biz.service;

import com.lda.biz.entity.Consumer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.biz.entity.vo.ConsumerVO;
import com.lda.biz.entity.vo.PageVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lda
 * @since 2020-10-17
 */
public interface ConsumerService extends IService<Consumer> {


    void updateConsumer(ConsumerVO consumerVO);

    void delete(Long id);

    List<ConsumerVO> findAll();

    Consumer addConsumer(ConsumerVO consumerVO);

    PageVO<ConsumerVO> findConsumerList(Integer currentPage, Integer pageSize, ConsumerVO consumerVO);

    ConsumerVO edit(Long id);
}
