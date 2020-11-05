package com.lda.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.system.entity.Log;
import com.lda.system.entity.LoginLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.system.entity.vo.LoginLogVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 登录日志表 服务类
 * </p>
 *
 * @author lda
 * @since 2020-10-08
 */
public interface LoginLogService extends IService<LoginLog> {

    void add(HttpServletRequest request);

    IPage<LoginLogVO> findLoginLogByPage(Page<LoginLog> page, QueryWrapper<LoginLog> wrapper);

    void batchDelete(String[] ids);

    List<Map<String, Object>> loginReport(String username);
}
