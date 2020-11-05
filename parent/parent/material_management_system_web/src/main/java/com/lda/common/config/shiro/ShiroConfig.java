package com.lda.common.config.shiro;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {


    @Bean("shiroFiter")
    public ShiroFilterFactoryBean filterFactoryBean(DefaultWebSecurityManager securityManager){
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        Map<String, Filter> filterMap = new HashMap<>();
        // 添加自己的过滤器并且取名为jwt
        filterMap.put("jwt",new JWTFilter());
        factoryBean.setFilters(filterMap);
        /*
         * 自定义url规则
         */
        Map<String, String> filterRuleMap = new LinkedHashMap<>();

        filterRuleMap.put("/user/login","anon");
        //开放API文档接口
        filterRuleMap.put("/swagger-ui.html", "anon");
        filterRuleMap.put("/doc.html/**", "anon");
        filterRuleMap.put("/webjars/**","anon");
        filterRuleMap.put("/swagger-resources/**","anon");
        filterRuleMap.put("/v2/**","anon");
        //sql监控
        filterRuleMap.put("/druid/**","anon");

        filterRuleMap.put("/**","jwt");
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        factoryBean.setSecurityManager(securityManager);
        return factoryBean;


    }


     @Bean("securityManager")
    public  DefaultWebSecurityManager defaultWebSecurityManager(CustomizeRealm realm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
         /*
         * 关闭shiro自带的session，详情见文档
         * http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        return  securityManager;
    }

    @Bean("realm")
    public CustomizeRealm realm(){

        return new CustomizeRealm();
    }

    /**
     * 下面的代码是添加注解支持
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        // https://zhuanlan.zhihu.com/p/29161098
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
