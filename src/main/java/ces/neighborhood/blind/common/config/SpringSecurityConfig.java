package ces.neighborhood.blind.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

import ces.neighborhood.blind.common.provider.AuthenticationProviderImpl;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * Spring Security Configuration
 * </pre>
 *
 * @version 1.0
 * @author the-mascot
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final AuthenticationProviderImpl authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((auth) -> auth
                    .requestMatchers("/", "/login", "/join", "/auth/join", "/static/**").permitAll()
                    .anyRequest().authenticated()
                )
                .formLogin((login) -> login
                        .loginPage("/login")    // 로그인 페이지
                        .loginProcessingUrl("/auth/login")  // 로그인 처리 url
                        .defaultSuccessUrl("/") // 로그인 성공 후 처리 url
                        .failureForwardUrl("/") // 로그인 실패 후 처리 url
                )
                // custom authenticationProvider bean 등록
                .authenticationProvider(authenticationProvider)
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                )
                .build();
    }
}
