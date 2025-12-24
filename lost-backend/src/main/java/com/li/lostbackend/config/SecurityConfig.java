package com.li.lostbackend.config;

import com.li.lostbackend.component.JwtAuthenticationTokenFilter;
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
                .csrf().disable() // 关闭 CSRF
                .cors()           // 开启跨域支持
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()

                // 👇👇👇 核心修复：放行登录注册的所有可能路径 👇👇👇
                .antMatchers("/api/auth/**", "/login", "/register", "/user/login").permitAll()

                // 👇👇👇 放行 Erupt 后台 👇👇👇
                .antMatchers("/erupt-web/**", "/erupt-api/**", "/erupt-attachment/**").permitAll()

                // 👇👇👇 放行静态资源 (避免 404/403) 👇👇👇
                .antMatchers("/", "/*.html", "/**/*.css", "/**/*.js", "/favicon.ico", "/error-bg.svg", "/img/**", "/upload/**").permitAll()

                // 其他接口需登录
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}