package SamoDev.Where2Play.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AutoLoginFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    public AutoLoginFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Проверяем, если пользователь еще не залогинен
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            // Пытаемся загрузить нашего тестового пользователя
            // Убедитесь, что 'test_user' реально существует в БД!
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername("test_user");

                // Создаем токен аутентификации
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Устанавливаем его в контекст безопасности
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Если пользователя нет - просто игнорируем, придется логиниться руками
                System.out.println("Auto-login failed: User 'test_user' not found");
            }
        }

        filterChain.doFilter(request, response);
    }
}
