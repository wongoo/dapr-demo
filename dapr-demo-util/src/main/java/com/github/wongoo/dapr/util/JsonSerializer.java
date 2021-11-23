/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
