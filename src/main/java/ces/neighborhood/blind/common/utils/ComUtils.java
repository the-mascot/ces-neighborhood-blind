package ces.neighborhood.blind.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import ces.neighborhood.blind.common.code.Constant;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 공통 유틸
 * </pre>
 *
 * @version 1.0
 * @author mascot
 * @since 2023.12.22
 */
@Slf4j
public class ComUtils {

    /**
     * 랜덤 닉네임생성
     * @param
     * @return 10글자 이하 랜덤 nickname
     * @throws
     */
    public static String generateRandomNickname() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "").substring(0, 10);
    }

    /**
     * Object to Json String Converter
     * @param o
     * @return JSON String
     */
    public static String convertObjectToJson(Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
             json = objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            log.error("[objectToJson] Json String 변환 실패, error: {}", e.getMessage());
        }
        return json;
    }

    /**
     * Map to Object converter
     * @param map, clazz
     * @return Object
     */
    public static <T> T convertMapToObject(Map<String, Object> map, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T instance = clazz.newInstance();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            Field field = getField(clazz, fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(instance, value);
            }
        }

        return instance;
    }

    private static <T> Field getField(Class<T> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * 현재시간과 timestamp의 시간 차이 계산
     * @param timestamp
     * @return 시간차이 ex) 1분, 1시간, 1일
     */
    public static String calculateTimeDifference(Timestamp timestamp) {
        if (timestamp == null) return null;
        Instant instant = timestamp.toInstant();
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of(TimeZone.getDefault().getID()));

        LocalDateTime now = LocalDateTime.now();
        // between(start, end) end 가 start 보다 늦으면 양수, 빠르면 음수
        long minutes = ChronoUnit.MINUTES.between(now, dateTime);
        long hours = ChronoUnit.HOURS.between(now, dateTime);
        long days = ChronoUnit.DAYS.between(now, dateTime);
        long year = ChronoUnit.YEARS.between(now, dateTime);

        if (minutes == 0) {
            return Constant.JUST_BEFORE_KO;
        } else if (minutes < 60) {
            return getElapsedTimeString(minutes, Constant.MINUTES_KO);
        } else if (hours < 24) {
            return getElapsedTimeString(hours, Constant.HOURS_KO);
        } else if (days < 365) {
            return getElapsedTimeString(days, Constant.HOURS_KO);
        } else {
            return getElapsedTimeString(year, Constant.YEARS_KO);
        }
    }

    public static String getElapsedTimeString(long time, String unit) {
        StringBuilder builder = new StringBuilder();
        return builder
                .append(Math.abs(time))
                .append(unit)
                .append(time < 0 ? Constant.BEFORE_KO : Constant.AFTER_KO)
                .toString();
    }
}
