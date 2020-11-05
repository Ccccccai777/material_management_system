package com.lda.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.system.entity.LoginLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lda.system.entity.vo.LoginLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 登录日志表 Mapper 接口
 * </p>
 *
 * @author lda
 * @since 2020-10-08
 */
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    IPage<LoginLogVO> findLoginLogByPage(Page<LoginLog> page,@Param(Constants.WRAPPER) QueryWrapper<LoginLog> wrapper);

    List<Map<String, Object>> loginReport(String username);
}
