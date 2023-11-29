package ces.neighborhood.blind.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * <pre>
 * BCryptPasswordEncoder Bean 등록 config
 * </pre>
 * SpringSecurityConfig에 두면 순환참조 error 발생으로 따로 config 파일에 빼둠.
 *
 * @version 1.0
 * @author the-mascot
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
