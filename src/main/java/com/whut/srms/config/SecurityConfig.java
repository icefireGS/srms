package com.whut.srms.config;

import com.whut.srms.security.JWTAuthenticationFilter;
import com.whut.srms.security.JWTLoginFilter;
import com.whut.srms.security.MyCustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyCustomUserService myCustomUserService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .cors()  //启用cors跨域处理
                .and()
                //增加登录拦截
                .addFilter(new JWTLoginFilter(authenticationManager()))
                //增加是否登陸过滤
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .csrf().disable()           // 禁用 Spring Security 自带的跨域处理
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //ignore
        web.ignoring().antMatchers("/code", "/register", "/check/**/**", "/search/sharefile");
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        //覆盖UserDetailsService类
        //覆盖默认的密码验证类
        auth.userDetailsService(myCustomUserService).passwordEncoder(bCryptPasswordEncoder());
    }

}
