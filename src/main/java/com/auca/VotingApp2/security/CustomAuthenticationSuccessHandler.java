//package com.auca.VotingApp2.security;
//
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import java.io.IOException;
//import java.util.Set;
//
//
//public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
//
//        // Redirect to different pages based on the role
//        if (roles.contains("ROLE_ADMIN")) {
//            response.sendRedirect("/admin/manage-users");
//        } else if (roles.contains("ROLE_USER")) {
//            response.sendRedirect("/user/home");
//        } else {
//            response.sendRedirect("/login");  // Default redirection
//        }
//
//    }
//}
