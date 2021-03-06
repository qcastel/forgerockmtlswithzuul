package com.forgerock.example.mtls.hello.x509;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class MTLSDetailsService implements UserDetailsService {

    @Value("${zuul-cert-sub}")
    private String zuulCertSub;

    @Override
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
        if (zuulCertSub.equals(subject)) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();
            subject = request.getHeader("X-Client-Cert");
        }
        if (subject == null || "".equals(subject)) {
            return new User("anonymous", "",  AuthorityUtils.createAuthorityList(Role.ANONYMOUS.name()));
        }
        return new User("subject: " + subject, "",  AuthorityUtils.createAuthorityList(Role.AUTHENTICATED.name()));

    }
}
