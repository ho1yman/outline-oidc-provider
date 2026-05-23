package com.oidc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .and()
            .authorizeRequests()
                .antMatchers(
                    "/", "/index.html", "/login.html",
                    "/static/**", "/assets/**", "/js/**", "/css/**", "/favicon.ico",
                    "/api/login",
                    "/realms/**/protocol/openid-connect/**",
                    "/realms/**/.well-known/openid-configuration",
                    "/api/theme-config"
                ).permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin().disable()
            .httpBasic().disable();
    }
}
