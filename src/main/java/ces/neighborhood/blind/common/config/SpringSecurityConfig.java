package ces.neighborhood.blind.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import ces.neighborhood.blind.app.service.UserDetailServiceImpl;
import ces.neighborhood.blind.common.provider.AuthenticationProviderImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final AuthenticationProviderImpl authenticationProvider;

    private final UserDetailServiceImpl userDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((auth) -> auth
                    .requestMatchers("/", "/login", "/join", "/auth/join", "/static/**").permitAll()
                    .anyRequest().authenticated()
                )
                .formLogin((login) -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/")
                        .failureForwardUrl("/")
                )
                .authenticationProvider(authenticationProvider)
                .userDetailsService(userDetailService)
                .csrf((csrf) -> csrf.disable())
                .build();
    }
}
