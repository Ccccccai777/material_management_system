package com.lda.biz.service;

import com.lda.biz.entity.OutStock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.biz.entity.vo.OutStockDetailVO;
import com.lda.biz.entity.vo.OutStockVO;
import com.lda.biz.entity.vo.PageVO;
import com.lda.biz.entity.vo.StatisticsVO;

import java.util.Date;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lda
 * @since 2020-10-17
 */
public interface OutStockService extends IService<OutStock> {

    PageVO<OutStockVO> findOutStockList(Integer currentPage, Integer pageSize, OutStockVO outStockVO);

    void addOutStock(OutStockVO outStockVO);

    void restore(Long id);

    void moveToTrash(Long id);

    void delete(Long id);

    OutStockDetailVO detail(Long id, Integer pageNum, Integer pageSize);

    void publish(Long id);

    void updateCurrentLocation(Long id, String address);

    void confirmArrival(Long id, String address);

    StatisticsVO findInboundStatistics(Date startTime, Date endTime);
}
