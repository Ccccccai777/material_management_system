package com.lda.common.aspect;

import com.alibaba.fastjson.JSON;
import com.lda.system.entity.Log;
import com.lda.common.annotation.ControllerEndpoint;
import com.lda.system.entity.bean.ActiveUser;
import com.lda.system.service.LogService;
import com.lda.utils.AddressUtil;
import com.lda.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

@Aspect
@Slf4j
@Component
public class ControllerEndpointAspect {
    private Log sysLog=new Log();
    private long startTime;

    @Autowired
    private LogService logService;
    @Pointcut("@annotation(com.lda.common.annotation.ControllerEndpoint)")
    public void pointcut(){

    }
    /**
     * 环绕通知
     * @param joinPoint
     */
    @Around("pointcut()")
   public Object saveSysLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result=null;
        //开始时间
        startTime=System.currentTimeMillis();
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();

        ControllerEndpoint annotation = method.getAnnotation(ControllerEndpoint.class);
        if (annotation != null) {
            String operation = annotation.operation();
            //添加注解上的操作描述
            sysLog.setOperation(operation);
        }
        //获得请求参数
        Object[] args = joinPoint.getArgs();
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = u.getParameterNames(method);
        System.out.println(args);
        System.out.println(parameterNames);
        sysLog.setParams("paramName:"+ Arrays.toString(parameterNames)+"args:"+Arrays.toString(args));

        //请求的IP
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddr = IPUtil.getIpAddr(request);
        sysLog.setIp(ipAddr);
        //地理位置
        sysLog.setLocation(AddressUtil.getCityInfo(ipAddr));

        //操作人
        ActiveUser activeUser= (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        sysLog.setUsername(activeUser.getUser().getUsername());
        //添加时间
        sysLog.setCreateTime(new Date());
        //执行目标方法
         result= joinPoint.proceed();

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getMethod().getName();
        String response="";
        if (result != null) {
            response = JSON.toJSONString(result);
        }
        sysLog.setMethod(className + "." + methodName + "()\n"
                +"\nresponse:"+response);
        //执行耗时
        sysLog.setTime(System.currentTimeMillis()-startTime);
        //保存系统日志
        logService.save(sysLog);
        return  result;

    }
}
