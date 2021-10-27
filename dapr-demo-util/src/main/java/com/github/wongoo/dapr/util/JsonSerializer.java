package com.github.wongoo.dapr.util;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dapr.client.ObjectSerializer;
import io.dapr.serializer.DaprObjectSerializer;
import io.dapr.utils.TypeRef;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.TimeZone;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * @author wongoo
 */
@Slf4j
public class JsonSerializer extends ObjectSerializer implements DaprObjectSerializer {

    public JsonSerializer() {
        OBJECT_MAPPER.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // set timezone to Asia/Shanghai to make date serialize value more user-friendly.
        OBJECT_MAPPER.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    @Override
    public byte[] serialize(Object state) throws IOException {
        log.info("serialize object, class: {}, data: {}", state.getClass(), state);
        return super.serialize(state);
    }

    @Override
    public <T> T deserialize(byte[] content, TypeRef<T> type) throws IOException {
        log.info("deserialize object, type: {}, data: {}", type, new String(content));
        return super.deserialize(content, type);
    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> clazz) throws IOException {
        log.info("deserialize object, class: {}, data: {}", clazz, new String(content));
        return super.deserialize(content, clazz);
    }

    @Override
    public String getContentType() {
        return "application/json";
    }
}
