package com.forgerock.example.mtls.zuul.x509;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MTLSDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {

        if (subject == null || "".equals(subject)) {
            return new User("anonymous", "",  AuthorityUtils.createAuthorityList(Role.ANONYMOUS.name()));
        }
        return new User(subject, "",  AuthorityUtils.createAuthorityList(Role.AUTHENTICATED.name()));
    }
}
