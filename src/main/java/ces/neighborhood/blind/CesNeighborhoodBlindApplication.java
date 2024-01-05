package ces.neighborhood.blind;

import ces.neighborhood.blind.common.code.Constant;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CesNeighborhoodBlindApplication {

	public static void main(String[] args) {
		SpringApplication.run(CesNeighborhoodBlindApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Application 기본 Timezone 설정
		TimeZone.setDefault(TimeZone.getTimeZone(Constant.SEOUL_TIMEZONE));
	}

}
