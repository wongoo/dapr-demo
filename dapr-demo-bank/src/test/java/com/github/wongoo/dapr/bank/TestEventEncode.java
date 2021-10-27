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
