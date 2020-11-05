package com.lda.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.system.entity.Log;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lda.system.entity.vo.LogVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 操作日志表 Mapper 接口
 * </p>
 *
 * @author lda
 * @since 2020-10-08
 */
public interface LogMapper extends BaseMapper<Log> {

    IPage<LogVO> findLogByPage(Page<Log> page,@Param(Constants.WRAPPER) QueryWrapper<Log> wrapper);
}
