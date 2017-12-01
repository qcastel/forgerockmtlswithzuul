package com.forgerock.example.mtls.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.forgerock.example.mtls.hello.x509.MTLSDetailsService;


@SpringBootApplication
@EnableDiscoveryClient
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan
public class ForgerockHelloWorldApplication extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) throws Exception {
        new SpringApplication(ForgerockHelloWorldApplication.class).run(args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .requestMatchers()
                .anyRequest()
                .and()
                .x509()
                .subjectPrincipalRegex("(.*)")
                .userDetailsService(userDetailsService())
        ;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new MTLSDetailsService();
    }

}
