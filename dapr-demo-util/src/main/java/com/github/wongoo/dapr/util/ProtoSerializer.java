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

import com.google.protobuf.GeneratedMessageV3;
import io.dapr.serializer.DaprObjectSerializer;
import io.dapr.utils.TypeRef;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author wongoo
 */
@SuppressWarnings("unchecked")
@Slf4j
public class ProtoSerializer<F extends GeneratedMessageV3> implements DaprObjectSerializer {

    private final Method protoParseFrom;
    private final TypeRef<F> protoTypeRef;

    public TypeRef<F> getProtoTypeRef() {
        return protoTypeRef;
    }

    public ProtoSerializer(Class<F> protoClazz) {
        try {
            protoTypeRef = TypeRef.get(protoClazz);
            protoParseFrom = protoClazz.getDeclaredMethod("parseFrom", byte[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] serialize(Object state) throws IOException {
        log.info("serialize object, class: {}, data: {}", state.getClass(), state);
        return ((F)state).toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] content, TypeRef<T> type) throws IOException {
        if (!type.equals(protoTypeRef)) {
            throw new IOException("expect type " + protoTypeRef + ", but get " + type);
        }
        return (T)deserialize(content);
    }

    public F deserialize(byte[] content) throws IOException {
        log.info("deserialize object,  data: {}", new String(content));
        try {
            return (F)protoParseFrom.invoke(null, content);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getContentType() {
        return "application/protobuf";
    }

}
