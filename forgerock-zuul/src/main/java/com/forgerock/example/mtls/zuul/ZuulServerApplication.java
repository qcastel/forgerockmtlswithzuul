package com.forgerock.example.mtls.zuul;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientConnectionManagerFactory;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.support.FeignHttpClientProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.SimpleHostRoutingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@RestController
public class ZuulServerApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulServerApplication.class).web(true).run(args);
    }

    @Bean
    @ConditionalOnMissingBean(HttpClientConnectionManager.class)
    public HttpClientConnectionManager connectionManager(
            ApacheHttpClientConnectionManagerFactory connectionManagerFactory,
            ZuulProperties zuulProperties) {
        /**
         * If we don't set this HttpClientConnectionManager bean, then Ribbon will instance it later but will enable
         * ssl validation.
         */
        ZuulProperties.Host hostProperties = zuulProperties.getHost();
        final HttpClientConnectionManager connectionManager = connectionManagerFactory
                .newConnectionManager(zuulProperties.isSslHostnameValidationEnabled(),
                        hostProperties.getMaxTotalConnections(),
                        hostProperties.getMaxPerRouteConnections(),
                        hostProperties.getTimeToLive(), hostProperties.getTimeUnit(),
                        null);
       return connectionManager;
    }
}
