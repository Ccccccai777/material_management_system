package com.lda.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.biz.entity.InStock;
import com.lda.biz.entity.InStockInfo;
import com.lda.biz.entity.OutStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lda
 * @since 2020-10-17
 */
public interface OutStockMapper extends BaseMapper<OutStock> {

    IPage<LinkedHashMap<String, Object>> findAllStoredMaterials(Page<InStock> page,@Param(Constants.WRAPPER) QueryWrapper<InStockInfo> wrapper1);

    IPage<LinkedHashMap<String, Object>> findRegionalContributionMaterials(Page<InStock> page,@Param(Constants.WRAPPER) QueryWrapper<InStockInfo> wrapper1);

    IPage<LinkedHashMap<String, Object>> findStatisticsOfWarehousingInformation(Page<InStock> page,@Param(Constants.WRAPPER) QueryWrapper<InStockInfo> wrapper);
}
