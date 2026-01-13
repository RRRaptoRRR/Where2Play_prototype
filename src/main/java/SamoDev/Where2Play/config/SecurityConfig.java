package SamoDev.Where2Play.config;

import SamoDev.Where2Play.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ОТКЛЮЧАЕМ CSRF для работы AJAX запросов
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests((requests) -> requests
                        // Разрешаем статику и страницы входа/регистрации
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/auth/**", "/register").permitAll()
                        // Разрешаем API и создание событий для авторизованных
                        .requestMatchers("/create", "/api/search/**").authenticated()
                        // Все остальное требует входа
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .permitAll()
                )
                // Фильтр авто-логина (уберите перед продакшеном)
                .addFilterBefore(new AutoLoginFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
