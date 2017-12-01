package com.forgerock.example.mtls.hello.x509;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class ForceAuthCheckinator implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.contains("X509AuthenticationFilter")) {
            ((X509AuthenticationFilter) bean).setCheckForPrincipalChanges(true);
        }
        return bean;
    }
}
