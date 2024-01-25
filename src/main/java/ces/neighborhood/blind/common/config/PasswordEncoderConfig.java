package ces.neighborhood.blind.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * <pre>
 * BCryptPasswordEncoder Bean 등록 config
 * SpringSecurityConfig 에 두면 순환참조 error 발생으로 따로 config 파일에 빼둠.
 * </pre>
 *
 * @version 1.0
 * @author the-mascot
 * @since 2023.11.28
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
