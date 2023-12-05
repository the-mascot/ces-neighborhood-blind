package ces.neighborhood.blind.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;

import ces.neighborhood.blind.app.provider.AuthenticationProviderImpl;
import ces.neighborhood.blind.common.filter.LoginFailureHandler;
import ces.neighborhood.blind.common.filter.LoginSuccessHandler;
import java.util.HashSet;
import java.util.Set;
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

    private final LoginSuccessHandler loginSuccessHandler;

    private final LoginFailureHandler loginFailureHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((auth) -> auth
                    .requestMatchers("/", "/login","/auth/login", "/logout", "/join", "/auth/join", "/static/**").permitAll()
                    .anyRequest().authenticated()
                )
                .formLogin((login) -> login
                        .loginPage("/login")    // 로그인 페이지
                        .loginProcessingUrl("/auth/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                .logout((logout) -> logout
                        .permitAll()
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                )
                // custom authenticationProvider bean 등록
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        return authenticationManagerBuilder.build();
    }
}
