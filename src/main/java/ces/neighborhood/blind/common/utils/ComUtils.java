package ces.neighborhood.blind.common.utils;

import ces.neighborhood.blind.app.entity.Board;
import ces.neighborhood.blind.common.code.Constant;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class ComUtils {

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
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (minutes < 60) {
            return minutes + Constant.MINUTES_KO;
        } else if (hours < 24) {
            return hours + Constant.HOURS_KO;
        } else {
            return days + Constant.DAYS_KO;
        }
    }
}
