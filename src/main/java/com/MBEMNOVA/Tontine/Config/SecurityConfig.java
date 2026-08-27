package com.MBEMNOVA.Tontine.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // Page accessible sans connexion
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // Toutes les autres pages nécessitent
                        // une authentification
                        .anyRequest().authenticated()
                )

                // Connexion Google
                .oauth2Login(oauth -> oauth

                        // Notre page de connexion personnalisée
                        .loginPage("/login")

                        // Après connexion réussie
                        .defaultSuccessUrl(
                                "/dashboard",
                                true
                        )
                )

                // Déconnexion
                .logout(logout -> logout

                        .logoutSuccessUrl("/login?logout")

                        .invalidateHttpSession(true)

                        .clearAuthentication(true)

                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}