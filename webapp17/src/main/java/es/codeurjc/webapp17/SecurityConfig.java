package es.codeurjc.webapp17;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
        .authorizeHttpRequests(authRequest -> authRequest
        .anyRequest().permitAll()
        //.requestMatchers("/aloha").authenticated()
            //.requestMatchers("/admin").hasRole("ADMIN")
            
        );
     http.httpBasic(login -> login.disable());
     http.csrf().disable();
    
     return (SecurityFilterChain) http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}