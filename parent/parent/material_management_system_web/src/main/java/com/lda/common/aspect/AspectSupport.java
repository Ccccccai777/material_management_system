package com.lda.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public abstract class AspectSupport {

    Method resolveMethod(ProceedingJoinPoint joinPoint){
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Class<?> clazz = joinPoint.getTarget().getClass();
      Method method= getDeclaredMethod(clazz,signature.getName(),signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new IllegalStateException("无法解析目标方法: " + signature.getMethod().getName());
        }
        return method;
    }

    private   Method getDeclaredMethod(Class<?> clazz, String name, Class<?>[] parameterTypes){

        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
               return   getDeclaredMethod(superclass,name,parameterTypes);
            }
        }
        return null;
    }
}
