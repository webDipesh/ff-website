package ff_website.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import ff_website.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

            // Local testing ko lagi CSRF disable
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Login/Register ra CSS/JS public
                .requestMatchers(
                    "/login",
                    "/register",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()

                // Admin pages - ADMIN role matra
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Dashboard login gareko user lai matra
                .requestMatchers("/dashboard").authenticated()

                // Aru sabai authenticated
                .anyRequest().authenticated()
            )

            // Database bata user load garne
            .userDetailsService(userDetailsService)

            // Login configuration
            .formLogin(form -> form

                .loginPage("/login")

                // Successful login → dashboard
                .defaultSuccessUrl("/dashboard", true)

                // Failed login → login page
                .failureUrl("/login?error")

                .permitAll()
            )

            // Logout
            .logout(logout -> logout

                .logoutSuccessUrl("/login?logout")

                .invalidateHttpSession(true)

                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}