package es.codeurjc.webapp17;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.mysql.cj.protocol.AuthenticationProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
        .authorizeHttpRequests(authRequest -> authRequest
            .requestMatchers("/aloha").authenticated()
            //.requestMatchers("/admin").hasRole("ADMIN")
            .anyRequest().permitAll()
        );
     http.httpBasic(login -> login.disable());
    
     return (SecurityFilterChain) http.build();
    }
}