package ces.neighborhood.blind.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ces.neighborhood.blind.app.provider.JwtTokenProvider;
import ces.neighborhood.blind.app.provider.OAuth2AuthenticationProviderImpl;
import ces.neighborhood.blind.common.filter.JwtTokenFilter;
import ces.neighborhood.blind.common.handler.CustomAccessDeniedHandler;
import ces.neighborhood.blind.common.handler.CustomAuthenticationEntryPoint;
import ces.neighborhood.blind.common.handler.Oauth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * Spring Security Configuration
 * </pre>
 *
 * @version 1.0
 * @author the-mascot
 * @since 2023.11.27
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SpringSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final Oauth2LoginSuccessHandler oauth2LoginSuccessHandler;

    private final OAuth2AuthenticationProviderImpl oAuth2AuthenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/member/check/**",
                                "/api/v1/oauth/**"
                        )
                        .permitAll()
                        .anyRequest().authenticated()   // permitAll url을 제외하고 모든 요청 인증필요
                )
                // API 서버이므로 CSRF disable
                .csrf(csrf -> csrf.disable())
                // JWT 방식은 무상태(Stateless) 임으로 인증 세션 생성 방지 설정
                .sessionManagement((session) -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                // JWT filter 등록 (UsernamePasswordAuthenticationFilter 전에 실행)
                .addFilterBefore(jwtTokenFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler
                        // 인증되지 않은 사용자가 보호된 리소스에 접근할때
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        // 인증된 사용자이지만 권한이 없는 경우 접근처리
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .oauth2Login((oauth) -> oauth.userInfoEndpoint(endpoint -> endpoint.and()
                        .successHandler(oauth2LoginSuccessHandler)))
                .build();
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtTokenProvider);
    }
}
