package ces.neighborhood.blind.common.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class ComUtils {

    /**
     * Object to Json String Converter
     * @param map, clazz
     * @return Object
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
}
