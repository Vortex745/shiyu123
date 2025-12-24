package com.li.lostbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 👇👇👇 这就是你缺失的那行代码，我帮你加好了 👇👇👇
import com.li.lostbackend.component.JwtAuthenticationTokenFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF 保护
                .csrf().disable()
                // 不使用 Session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 放行基础接口
                .antMatchers("/login", "/register", "/user/login").permitAll()
                .antMatchers("/post/list", "/post/detail/**").permitAll()
                .antMatchers("/img/**", "/upload/**", "/profile/**").permitAll()

                // 👇 Erupt 后台放行配置
                .antMatchers("/erupt-web/**").permitAll()
                .antMatchers("/erupt-api/**").permitAll()
                .antMatchers("/erupt-attachment/**").permitAll()
                // 👆 Erupt 配置结束

                // 其他接口需要认证
                .anyRequest().authenticated();

        // 添加 JWT 过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}