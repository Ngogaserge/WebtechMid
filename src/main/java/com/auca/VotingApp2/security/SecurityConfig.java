//package com.auca.VotingApp2.security;
//
//import com.auca.VotingApp2.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final UserService userService;
//    private final BCryptPasswordEncoder passwordEncoder;
//
//    @Autowired
//    public SecurityConfig(UserService userService, BCryptPasswordEncoder passwordEncoder) {
//        this.userService = userService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(
//                                "/", // Landing page
//                                "/login", // Login page
//                                "/registration**", // Registration pages
//                                "/forgot-password", "/reset-password", // Password reset endpoints
//                                "/js/**", "/css/**", "/img/**" // Static resources
//                        ).permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN") // Admin-only endpoints
//                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // User and Admin
//                        .requestMatchers("/voter/**").hasRole("USER") // Voter-only endpoints
//                        .anyRequest().authenticated() // All other endpoints require authentication
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .successHandler(customAuthenticationSuccessHandler()) // Custom success handler
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/login?logout")
//                        .permitAll()
//                )
//                .csrf(csrf -> csrf.disable()); // Updated CSRF configuration
//
//        return http.build();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
//        auth.setUserDetailsService(userService); // Ensure UserService implements UserDetailsService
//        auth.setPasswordEncoder(passwordEncoder);
//        return auth;
//    }
//
//    @Bean
//    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
//        return new CustomAuthenticationSuccessHandler();
//    }
//}
