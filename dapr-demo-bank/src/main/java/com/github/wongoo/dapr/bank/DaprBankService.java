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
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.Metadata;
import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.CommonProtos;
import io.dapr.v1.DaprAppCallbackProtos;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Collections.singletonMap;

/**
 * @author wongoo
 */
@Slf4j
public class DaprBankService extends AppCallbackGrpc.AppCallbackImplBase {

    private static final String MESSAGE_TTL_IN_SECONDS = "1000";
    private static final Map<String, String> PUBLISH_METADATA =
        singletonMap(Metadata.TTL_IN_SECONDS, MESSAGE_TTL_IN_SECONDS);

    private static final String TOPIC_TRANS_EVENT = "trans_event";

    private static final String PUBSUB_NAME = "pubsub";

    AtomicLong transIdSequence = new AtomicLong(0);

    /**
     * tell dapr topics to subscribe
     */
    @Override
    public void listTopicSubscriptions(Empty request,
        StreamObserver<DaprAppCallbackProtos.ListTopicSubscriptionsResponse> responseObserver) {
    }

    /**
     * Server mode: this is the Dapr method to receive Invoke operations via Grpc.
     *
     * @param request          Dapr envelope request,
     * @param responseObserver Dapr envelope response.
     */
    @Override
    public void onInvoke(CommonProtos.InvokeRequest request,
        StreamObserver<CommonProtos.InvokeResponse> responseObserver) {
        try {
            if ("trans".equals(request.getMethod())) {
                BankProto.TransRequest transRequest = BankProto.TransRequest.parseFrom(request.getData().getValue());
                BankProto.TransResponse response = trans(transRequest);

                CommonProtos.InvokeResponse.Builder responseBuilder = CommonProtos.InvokeResponse.newBuilder();
                responseBuilder.setData(Any.pack(response));
                responseObserver.onNext(responseBuilder.build());
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    public BankProto.TransResponse trans(BankProto.TransRequest request) {
        log.info("bank trans request: orderId: {}, amount: {}", request.getOrderId(), request.getAmount());

        long transId = transIdSequence.incrementAndGet();
        asyncTransSuccessNotice(transId, request);

        return BankProto.TransResponse.newBuilder().setTransId(transId).setCode("0").setMessage("submit ok").build();
    }

    private void asyncTransSuccessNotice(long transId, BankProto.TransRequest request) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }

            BankProto.TransEvent successEvent =
                BankProto.TransEvent.newBuilder().setTransId(transId).setStatus(200).setMessage("trans success").build();
            publishEvent(successEvent);
        }).start();
    }

    private final DaprClient daprClient;

    public DaprBankService() {
        this.daprClient = (new DaprClientBuilder()).build();
    }

    private void publishEvent(BankProto.TransEvent event) {
        log.info("publish trans event, transId: {}, status: {}, message: {}", event.getTransId(), event.getStatus(),
            event.getMessage());

        ByteString byteString = event.toByteString();
        log.info("publish trans event,data: {}", byteString);

        /*
         * TODO: public proto Object will get error, so convert base64 string. SHOULD have better way.
         */
        String base64 = Base64.getEncoder().encodeToString(byteString.toByteArray());

        /*
         * event data will auto package with CloudEvent<?>
         */
        daprClient.publishEvent(PUBSUB_NAME, TOPIC_TRANS_EVENT, base64, PUBLISH_METADATA).block();
    }

}
