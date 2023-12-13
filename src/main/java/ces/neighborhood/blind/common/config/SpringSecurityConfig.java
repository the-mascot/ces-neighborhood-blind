package ces.neighborhood.blind.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import ces.neighborhood.blind.app.provider.AuthenticationProviderImpl;
import ces.neighborhood.blind.app.service.Oauth2UserServiceImpl;
import ces.neighborhood.blind.common.filter.LoginFailureHandler;
import ces.neighborhood.blind.common.filter.LoginSuccessHandler;
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

    private final Oauth2UserServiceImpl oauth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login", "/auth/login", "/logout", "/join", "/auth/join", "/static/**").permitAll()
                    .anyRequest().authenticated()   // permitAll url을 제외하고 모든 요청 인증필요
                )
                .formLogin(login -> login
                        .loginPage("/login")    // 로그인 페이지 url
                        .loginProcessingUrl("/auth/login")  // 로그인 처리 url
                        .successHandler(loginSuccessHandler)    // 인증성공 처리 handler
                        .failureHandler(loginFailureHandler)    // 인증실패 처리 handler
                )
                .oauth2Login(login -> login
                        .userInfoEndpoint(point -> point.userService(oauth2UserService))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")   // 로그아웃 url
                        .logoutSuccessUrl("/")  // 로그아웃 성공시 redirect url
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                )
                .build();
    }

    @Bean
    // custom authenticationProvider bean 등록
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        return authenticationManagerBuilder.build();
    }
}
