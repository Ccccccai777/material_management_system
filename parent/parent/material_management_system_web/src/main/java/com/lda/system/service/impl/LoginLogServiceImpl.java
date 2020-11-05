package com.lda.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lda.handler.BusinessException;
import com.lda.response.ResultCode;
import com.lda.system.entity.Log;
import com.lda.system.entity.LoginLog;
import com.lda.system.entity.bean.ActiveUser;
import com.lda.system.entity.vo.LoginLogVO;
import com.lda.system.mapper.LoginLogMapper;
import com.lda.system.service.LoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lda.utils.AddressUtil;
import com.lda.utils.IPUtil;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.net.util.IPAddressUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 登录日志表 服务实现类
 * </p>
 *
 * @author lda
 * @since 2020-10-08
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;
    @Transactional
    @Override
    public void add(HttpServletRequest request) {
        LoginLog loginLog = new LoginLog();
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        loginLog.setUsername(activeUser.getUser().getUsername());
        loginLog.setIp(IPUtil.getIpAddr(request));
        loginLog.setLocation(AddressUtil.getCityInfo(IPUtil.getIpAddr(request)));
        //获取客户端操作系统
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        Browser browser = userAgent.getBrowser();
        OperatingSystem os = userAgent.getOperatingSystem();
        loginLog.setUserSystem(os.getName());
        loginLog.setUserBrowser(browser.getName());
        loginLog.setLoginTime(new Date());
         this.baseMapper.insert(loginLog);
    }

    @Override
    public IPage<LoginLogVO> findLoginLogByPage(Page<LoginLog> page, QueryWrapper<LoginLog> wrapper) {
        return loginLogMapper.findLoginLogByPage(page,wrapper);
    }
    @Transactional
    @Override
    public void batchDelete(String[] ids) {
        for (String id : ids) {
            long logId = Long.parseLong(id);
            LoginLog loginLog = this.baseMapper.selectById(logId);
            if (loginLog==null) {
                throw new BusinessException(ResultCode.LOGIN_LOG_DOES_NOT_EXIST.getCode(),ResultCode.LOGIN_LOG_DOES_NOT_EXIST.getMessage());
            }
            this.baseMapper.deleteById(logId);
        }
    }

    @Override
    public List<Map<String, Object>> loginReport(String username) {


        return loginLogMapper.loginReport(username);
    }
}
