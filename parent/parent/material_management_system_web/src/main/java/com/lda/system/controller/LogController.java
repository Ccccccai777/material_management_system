package com.lda.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.common.annotation.ControllerEndpoint;
import com.lda.response.Result;
import com.lda.system.entity.Log;
import com.lda.system.entity.Role;
import com.lda.system.entity.User;
import com.lda.system.entity.vo.LogVO;
import com.lda.system.entity.vo.RoleVO;
import com.lda.system.entity.vo.UserVO;
import com.lda.system.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 操作日志表 前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-10-08
 */
@Api("操作日志接口")
@RestController
@RequestMapping("/log")
public class LogController {
    @Autowired
    private LogService logService;
    @ApiOperation(value = "查询所有操作日志包括模糊查询", notes = "查询所有操作日志")
    @PostMapping("/findLogByPage")
    public Result findLogByPage(@RequestParam(defaultValue = "1") Integer current,
                                 @RequestParam(defaultValue = "10") Integer size,
                                 @RequestBody LogVO logVO){
        Page<Log> page=new Page<>(current,size);
        QueryWrapper<Log> wrapper = getWrapper(logVO);
        IPage<LogVO> logVOIPage = logService.findLogByPage(page,wrapper);
        long total = logVOIPage.getTotal();
        List<LogVO> logVOList = logVOIPage.getRecords();
        return Result.ok().data("total", total).data("logList", logVOList);

    }

    /**
     * 删除日志信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除日志", notes = "删除日志信息根据Id")
    @ControllerEndpoint(exceptionMessage = "删除日志信息失败", operation = "删除日志信息")
    @RequiresPermissions({"log:delete"})
    @DeleteMapping("/delete/{id}")
    public Result deleteLog(@PathVariable  Long id){
        try {
            logService.removeById(id);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }

    /**
     * 批量删除日志信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "批量删除日志信息", notes = "批量删除日志信息")
    @ControllerEndpoint(exceptionMessage = "批量删除日志信息失败", operation = "批量删除日志信息")
    @RequiresPermissions({"log:batchDelete"})
    @DeleteMapping("/batchDelete/{ids}")
    public Result batchDelete(@PathVariable  String[] ids){
        try {
            logService.batchDelete(ids);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }
    private QueryWrapper<Log> getWrapper(LogVO logVO){
        QueryWrapper<Log> qw = new QueryWrapper<>();
        if (logVO != null) {

            if (!StringUtils.isEmpty(logVO.getUsername())) {
                qw.like("username",logVO.getUsername());
            }
            if (!StringUtils.isEmpty(logVO.getIp())) {
                qw.like("ip",logVO.getIp());
            }
            if (!StringUtils.isEmpty(logVO.getLocation())) {
                qw.like("location",logVO.getLocation());
            }

        }
        return qw;
    }
}

