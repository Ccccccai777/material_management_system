package com.lda.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.entity.InStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lda.biz.entity.InStockInfo;
import com.lda.biz.entity.vo.StatisticsVO;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lda
 * @since 2020-10-13
 */
public interface InStockMapper extends BaseMapper<InStock> {

    IPage<LinkedHashMap<String, Object>> findStatisticsOfWarehousingInformation(Page<InStock> page, @Param(Constants.WRAPPER) QueryWrapper<InStockInfo> wrapper);

    IPage<LinkedHashMap<String, Object>> findAllStoredMaterials(Page<InStock> page, @Param(Constants.WRAPPER) QueryWrapper<InStockInfo> wrapper);

    IPage<LinkedHashMap<String, Object>> findRegionalContributionMaterials(Page<InStock> page,@Param(Constants.WRAPPER) QueryWrapper<InStockInfo> wrapper1);
}
