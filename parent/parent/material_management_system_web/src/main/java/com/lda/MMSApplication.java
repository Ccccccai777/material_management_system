package com.lda;

import com.alibaba.druid.support.http.StatViewServlet;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.lda.*.mapper")
@EnableSwagger2
public class MMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(MMSApplication.class,args);
    }


    @Bean
    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet() {
        ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>(new StatViewServlet(),  "/druid/*");
        registrationBean.addInitParameter("allow", "127.0.0.1");// IP白名单 (没有配置或者为空，则允许所有访问)
        registrationBean.addInitParameter("deny", "");// IP黑名单 (存在共同时，deny优先于allow)
        registrationBean.addInitParameter("loginUsername", "root");
        registrationBean.addInitParameter("loginPassword", "123456");
        registrationBean.addInitParameter("resetEnable", "false");
        return registrationBean;
    }
}
