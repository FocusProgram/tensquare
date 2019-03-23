package com.tensquare.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 安全配置类
 * 添加了spring security依赖后，所有的地址都被spring security所控制了，我们目
 * 前只是需要用到BCrypt密码加密的部分，所以我们要添加一个配置类，配置为所有地址
 * 都可以匿名访问
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{


    //authorizeRequests()所有的security全注解实现的开端，表示开始说明需要的权限
    //需要权限两部分，第一部分是拦截的路径，第二部分是访问该路径所需的权限
    //antMatchers表示拦截的路径，permitAll表示任何权限都可以访问，直接放行所有
    //anyRequest()任何的请求，authenticated认证后才能访问
    //.and().csrf().disable()固定写法，表示csrf(跨站伪造请求)拦截失效
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();
    }
}
