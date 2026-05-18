package com.cantina.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        var funcionario = User.withUsername("funcionario")
                .password("{noop}1234")
                .roles("FUNCIONARIO")
                .build();

        var gerente = User.withUsername("gerente")
                .password("{noop}1234")
                .roles("GERENTE")
                .build();

        System.out.println("SECURITY CONFIG CARREGADO - Utilizadores criados!");
        return new InMemoryUserDetailsManager(funcionario, gerente);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout.logoutSuccessUrl("/login"));

        return http.build();
    }
}
