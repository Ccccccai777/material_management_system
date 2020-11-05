package com.lda.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.system.entity.Log;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lda.system.entity.vo.LogVO;

/**
 * <p>
 * 操作日志表 服务类
 * </p>
 *
 * @author lda
 * @since 2020-10-08
 */
public interface LogService extends IService<Log> {

    IPage<LogVO> findLogByPage(Page<Log> page, QueryWrapper<Log> wrapper);

    void batchDelete(String[] ids);
}
