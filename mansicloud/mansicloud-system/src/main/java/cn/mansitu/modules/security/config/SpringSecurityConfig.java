/*
 *  Copyright 2019-2025 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.mansitu.modules.security.config;

import cn.mansitu.modules.security.security.JwtAccessDeniedHandler;
import cn.mansitu.modules.security.security.JwtAuthenticationEntryPoint;
import cn.mansitu.modules.security.security.TokenConfigurer;
import cn.mansitu.modules.security.security.TokenProvider;
import cn.mansitu.modules.security.service.OnlineUserService;
import cn.mansitu.utils.AnonTagUtils;
import cn.mansitu.utils.enums.RequestMethodEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;
import java.util.Map;
import java.util.Set;

/**
 * @author Zheng Jie
 */
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SpringSecurityConfig {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final ApplicationContext applicationContext;
    private final SecurityProperties properties;
    private final OnlineUserService onlineUserService;

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // 去除 ROLE_ 前缀
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 密码加密方式
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // 获取匿名标记
        Map<String, Set<String>> anonymousUrls = AnonTagUtils.getAnonymousUrl(applicationContext);
        return httpSecurity
                // 禁用 CSRF
                .csrf(csrf -> csrf.disable())
                .addFilter(corsFilter)
                // 授权异常
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authenticationErrorHandler)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .headers(headers -> headers
                        .frameOptions()
                        .disable())
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(requests -> requests
                        // 静态资源等等
                        .antMatchers(
                                HttpMethod.GET,
                                "/*.html",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js",
                                "/webSocket/**"
                        ).permitAll()
                        // swagger 文档
                        .antMatchers("/swagger-ui.html").permitAll()
                        .antMatchers("/swagger-resources/**").permitAll()
                        .antMatchers("/webjars/**").permitAll()
                        .antMatchers("/*/api-docs").permitAll()
                        // 文件
                        .antMatchers("/avatar/**").permitAll()
                        .antMatchers("/file/**").permitAll()
                        // 阿里巴巴 druid
                        .antMatchers("/druid/**").permitAll()
                        // 放行OPTIONS请求
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 自定义匿名访问所有url放行：允许匿名和带Token访问，细腻化到每个 Request 类型
                        // GET
                        .antMatchers(HttpMethod.GET, anonymousUrls.get(RequestMethodEnum.GET.getType()).toArray(new String[0])).permitAll()
                        // POST
                        .antMatchers(HttpMethod.POST, anonymousUrls.get(RequestMethodEnum.POST.getType()).toArray(new String[0])).permitAll()
                        // PUT
                        .antMatchers(HttpMethod.PUT, anonymousUrls.get(RequestMethodEnum.PUT.getType()).toArray(new String[0])).permitAll()
                        // PATCH
                        .antMatchers(HttpMethod.PATCH, anonymousUrls.get(RequestMethodEnum.PATCH.getType()).toArray(new String[0])).permitAll()
                        // DELETE
                        .antMatchers(HttpMethod.DELETE, anonymousUrls.get(RequestMethodEnum.DELETE.getType()).toArray(new String[0])).permitAll()
                        // 所有类型的接口都放行
                        .antMatchers(anonymousUrls.get(RequestMethodEnum.ALL.getType()).toArray(new String[0])).permitAll()
                        // 所有请求都需要认证
                        .anyRequest().authenticated()).apply(securityConfigurerAdapter())
                .and().build();
    }

    private TokenConfigurer securityConfigurerAdapter() {
        return new TokenConfigurer(tokenProvider, properties, onlineUserService);
    }
}
