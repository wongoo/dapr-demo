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

package com.github.wongoo.dapr.bank;

import com.github.wongoo.dapr.bank.proto.BankProto;
import com.github.wongoo.dapr.util.JsonSerializer;
import com.google.protobuf.ByteString;
import org.junit.Assert;
import org.junit.Test;

public class TestEventEncode {

    @Test
    public void testEventEncode() throws Exception {
        BankProto.TransEvent event =
            BankProto.TransEvent.newBuilder().setOrderId(11).setStatus(1).setMessage("ok").build();

        ByteString byteString = event.toByteString();
        byte[] bytes = event.toByteArray();

        BankProto.TransEvent parse1 = BankProto.TransEvent.parseFrom(byteString);
        BankProto.TransEvent parse2 = BankProto.TransEvent.parseFrom(bytes);
        BankProto.TransEvent parse3 = BankProto.TransEvent.parseFrom(byteString.toByteArray());

        Assert.assertEquals(event, parse1);
        Assert.assertEquals(event, parse2);
        Assert.assertEquals(event, parse3);

        JsonSerializer jsonSerializer = new JsonSerializer();
        byte[] encode = jsonSerializer.serialize(event);

        BankProto.TransEvent decode = jsonSerializer.deserialize(encode, BankProto.TransEvent.class);
        Assert.assertEquals(event, decode);
    }

}
