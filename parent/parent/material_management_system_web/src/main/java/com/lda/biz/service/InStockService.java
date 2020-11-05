package com.lda.biz.service;

import com.lda.biz.entity.InStock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.biz.entity.vo.InStockDetailVO;
import com.lda.biz.entity.vo.InStockVO;
import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.StatisticsVO;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lda
 * @since 2020-10-13
 */
public interface InStockService extends IService<InStock> {

    void addIntoStock(InStockVO inStockVO);

    PageVO<InStockVO> findInStockList(Integer pageNum, Integer pageSize, InStockVO inStockVO);

    void delete(Long id);

    void publish(Long id);

    void moveToTrash(Long id);

    void restore(Long id);

    InStockDetailVO detail(Long id, Integer pageNum, Integer pageSize);

    StatisticsVO findInboundStatistics(Date startTime, Date endTime);
}
