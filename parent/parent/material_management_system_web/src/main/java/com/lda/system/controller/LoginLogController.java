package com.lda.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.common.annotation.ControllerEndpoint;
import com.lda.response.Result;
import com.lda.system.entity.Log;
import com.lda.system.entity.LoginLog;
import com.lda.system.entity.vo.LogVO;
import com.lda.system.entity.vo.LoginLogVO;
import com.lda.system.entity.vo.UserVO;
import com.lda.system.service.LogService;
import com.lda.system.service.LoginLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 登录日志表 前端控制器
 * </p>
 *
 * @author lda
 * @since 2020-10-08
 */
@Api("登入日志接口")
@RestController
@RequestMapping("/loginLog")
public class LoginLogController {
    @Autowired
    private LoginLogService loginLogService;
    @ApiOperation(value = "查询所有登入日志包括模糊查询", notes = "查询所有登入日志")
    @PostMapping("/findLoginLogByPage")
    public Result findLoginLogByPage(@RequestParam(defaultValue = "1") Integer current,
                                @RequestParam(defaultValue = "10") Integer size,
                                @RequestBody LoginLogVO loginLogVO){
        Page<LoginLog> page=new Page<>(current,size);
        QueryWrapper<LoginLog> wrapper = getWrapper(loginLogVO);
        IPage<LoginLogVO> loginLogIPage = loginLogService.findLoginLogByPage(page,wrapper);
        long total = loginLogIPage.getTotal();
        List<LoginLogVO> loginLogVOList = loginLogIPage.getRecords();
        return Result.ok().data("total", total).data("loginLogList", loginLogVOList);

    }
    /**
     * 登入报表
     * @return
     */
    @GetMapping("/loginReport")
    @ApiOperation(value = "登入报表",notes = "用户登入报表")
    public Result loginReport( String username){
        List<Map<String,Object>> mapList= loginLogService.loginReport(username);
        Map<String,Object> map=new HashMap<>();
        username="";
        List<Map<String,Object>> meList= loginLogService.loginReport(username);
        map.put("me",mapList);
        map.put("all",meList);
        return Result.ok().data("data",map);
    }

    /**
     * 删除登入日志信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除登入日志", notes = "删除登入日志信息根据Id")
    @ControllerEndpoint(exceptionMessage = "删除登入日志信息失败", operation = "删除登入日志信息")
    @RequiresPermissions({"loginLog:delete"})
    @DeleteMapping("/delete/{id}")
    public Result deleteLoginLog(@PathVariable  Long id){
        try {
            loginLogService.removeById(id);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }

    /**
     * 批量删除登入日志信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "批量删除登入日志信息", notes = "批量删除登入日志信息")
    @ControllerEndpoint(exceptionMessage = "批量删除登入日志信息失败", operation = "批量删除登入日志信息")
    @RequiresPermissions({"loginLog:batchDelete"})
    @DeleteMapping("/batchDelete/{ids}")
    public Result batchDelete(@PathVariable String[] ids){
        try {
            loginLogService.batchDelete(ids);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }

    }
    private QueryWrapper<LoginLog> getWrapper(LoginLogVO loginLogVO){
        QueryWrapper<LoginLog> qw = new QueryWrapper<>();
          if (loginLogVO != null) {
              System.out.println(loginLogVO);

            if (!StringUtils.isEmpty(loginLogVO.getUsername())) {
                qw.like("username",loginLogVO.getUsername());
            }
            if (!StringUtils.isEmpty(loginLogVO.getIp())) {
                qw.like("ip",loginLogVO.getIp());
            }
            if (!StringUtils.isEmpty(loginLogVO.getLocation())) {
                qw.like("location",loginLogVO.getLocation());
            }

        }
        return qw;
    }
}

