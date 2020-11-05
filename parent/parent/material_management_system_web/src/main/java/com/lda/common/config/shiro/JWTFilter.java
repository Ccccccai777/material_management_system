package com.lda.common.config.shiro;

import com.google.gson.Gson;
import com.lda.handler.BusinessException;
import com.lda.jwt.JWTToken;

import com.lda.response.ResultCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JWTFilter extends BasicHttpAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        return subject!=null&&subject.isAuthenticated();
    }


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        String token = httpServletRequest.getHeader("Authorization");
            //2. 如果客户端没有携带token，拦下请求
        if (StringUtils.isEmpty(token)) {
            responseTokenError(response,"Token无效，您无权访问该接口");
            return false;
        }
        //3. 如果有，对进行进行token验证
        JWTToken jwtToken = new JWTToken(token);
        try {
            SecurityUtils.getSubject().login(jwtToken);
            return true;
        } catch (AuthenticationException e) {

            responseTokenError(response,e.getMessage());
            return  false;
        }

    }
    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
    private void responseTokenError(ServletResponse response, String s) {

        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        try (PrintWriter out = httpServletResponse.getWriter()) {
            String data = new Gson().toJson(new BusinessException(ResultCode.USER_TOKEN_ERROR.getCode(),s));
            out.append(data);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
