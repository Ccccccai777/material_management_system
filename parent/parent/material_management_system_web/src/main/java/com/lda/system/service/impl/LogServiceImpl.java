package com.lda.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.handler.BusinessException;
import com.lda.response.ResultCode;
import com.lda.system.entity.Log;
import com.lda.system.entity.vo.LogVO;
import com.lda.system.mapper.LogMapper;
import com.lda.system.service.LogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-10-08
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

    @Override
    public IPage<LogVO> findLogByPage(Page<Log> page, QueryWrapper<Log> wrapper) {
        return  this.baseMapper.findLogByPage(page, wrapper);
    }
    @Transactional
    @Override
    public void batchDelete(String[] ids) {
        for (String id : ids) {
            long logId = Long.parseLong(id);
            Log log = this.baseMapper.selectById(logId);
            if (log==null) {
                throw new BusinessException(ResultCode.OPERATION_LOG_DOES_NOT_EXIST.getCode(),ResultCode.OPERATION_LOG_DOES_NOT_EXIST.getMessage());

            }
            this.baseMapper.deleteById(logId);
        }

    }
}
