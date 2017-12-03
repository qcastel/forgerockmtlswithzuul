package com.forgerock.example.mtls.zuul;

import org.apache.http.conn.HttpClientConnectionManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientConnectionManagerFactory;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RestController;

import com.forgerock.example.mtls.zuul.ssl.ZuulApacheHttpClientConnectionManagerFactory;
import com.forgerock.example.mtls.zuul.x509.MTLSDetailsService;

@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@RestController
public class ZuulServerApplication extends WebSecurityConfigurerAdapter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulServerApplication.class).web(true).run(args);
    }

    @Bean
    @ConditionalOnMissingBean(HttpClientConnectionManager.class)
    public HttpClientConnectionManager connectionManager(
            ApacheHttpClientConnectionManagerFactory connectionManagerFactory,
            ZuulProperties zuulProperties) {
        /**
         * Fixing #2503 by not disabling ssl validation when sslHostnameValidationEnabled=false
         */
        ZuulProperties.Host hostProperties = zuulProperties.getHost();
        final HttpClientConnectionManager connectionManager = connectionManagerFactory
                .newConnectionManager(false,
                        hostProperties.getMaxTotalConnections(),
                        hostProperties.getMaxPerRouteConnections(),
                        hostProperties.getTimeToLive(), hostProperties.getTimeUnit(),
                        null);
       return connectionManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public ApacheHttpClientConnectionManagerFactory connManFactory() {
        return new ZuulApacheHttpClientConnectionManagerFactory();
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
