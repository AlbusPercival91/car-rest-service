package ua.foxminded.car.microservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(HttpMethod.GET, "/api/v1/cars/**", "/api/v1/categories/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/cars/**", "/api/v1/categories/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/cars/**", "/api/v1/categories/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/cars/**", "/api/v1/categories/**").authenticated()
                    .anyRequest().authenticated();
        }).oauth2Login(withDefaults()).formLogin(Customizer.withDefaults()).build();
    }
}
