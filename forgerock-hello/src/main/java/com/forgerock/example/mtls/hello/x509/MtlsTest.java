package com.forgerock.example.mtls.hello.x509;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MtlsTest {

    public static class MtlsTestResponse {
        public String message;
        public Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
    }

    @GetMapping(value = "/mtlsTest")
    public ResponseEntity<MtlsTestResponse> mtlsTest(Principal principal) {

        MtlsTestResponse response = new MtlsTestResponse();
        if (principal == null) {
            response.message = "Hello anonymous! Add your certificate into your web browser or postman to authenticate";
        } else {
            UserDetails currentUser = (UserDetails) ((Authentication) principal).getPrincipal();
            response.message = "Hello " + currentUser.getUsername() + "!";
            response.authorities = currentUser.getAuthorities();
        }
        return ResponseEntity.ok(response);
    }
}
