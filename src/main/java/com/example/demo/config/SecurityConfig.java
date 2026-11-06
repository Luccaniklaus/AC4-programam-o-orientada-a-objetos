package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security: login por formulário (interface Web) + HTTP Basic (Postman).
 * Perfis: USER (somente consulta) e ADMIN (consulta, cadastro, edição e exclusão).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // público
                .requestMatchers("/", "/login", "/error", "/css/**").permitAll()
                // API REST: leitura para USER e ADMIN; escrita somente ADMIN
                .requestMatchers(HttpMethod.GET, "/brinquedos/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/brinquedos/**").hasRole("ADMIN")
                // Painel Web: ações de escrita somente ADMIN; listagem para USER e ADMIN
                .requestMatchers("/painel/salvar", "/painel/editar/**", "/painel/excluir/**").hasRole("ADMIN")
                .requestMatchers("/painel/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/painel", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            // A API REST é chamada pelo Postman (sem formulário), então o CSRF fica desligado só nela
            .csrf(csrf -> csrf.ignoringRequestMatchers("/brinquedos/**"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
