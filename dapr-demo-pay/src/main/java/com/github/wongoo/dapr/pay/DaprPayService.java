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

package com.github.wongoo.dapr.pay;

import com.github.wongoo.dapr.bank.proto.BankProto;
import com.github.wongoo.dapr.pay.model.PayResult;
import com.github.wongoo.dapr.pay.proto.PayProto;
import com.github.wongoo.dapr.util.JsonSerializer;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.CloudEvent;
import io.dapr.client.domain.HttpExtension;
import io.dapr.client.domain.Metadata;
import io.dapr.client.domain.PublishEventRequest;
import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.CommonProtos;
import io.dapr.v1.DaprAppCallbackProtos;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonMap;

/**
 * @author wongoo
 */
@Slf4j
public class DaprPayService extends AppCallbackGrpc.AppCallbackImplBase {

    private static final String MESSAGE_TTL_IN_SECONDS = "1000";
    private static final Map<String, String> PUBLISH_METADATA =
        singletonMap(Metadata.TTL_IN_SECONDS, MESSAGE_TTL_IN_SECONDS);

    private static final String SERVICE_APP_ID_BANK = "dapr-demo-bank";

    private static final String TOPIC_PAY_EVENT = "pay_event";
    private static final String TOPIC_TRANS_EVENT = "trans_event";

    private static final String PUBSUB_NAME = "pubsub";

    JsonSerializer jsonSerializer = new JsonSerializer();
    private final DaprClient daprClient;

    public DaprPayService() {
        this.daprClient = (new DaprClientBuilder()).withObjectSerializer(jsonSerializer).build();
    }

    /**
     * tell dapr topics to subscribe
     */
    @Override
    public void listTopicSubscriptions(Empty request,
        StreamObserver<DaprAppCallbackProtos.ListTopicSubscriptionsResponse> responseObserver) {
        DaprAppCallbackProtos.TopicSubscription topicSubscription =
            DaprAppCallbackProtos.TopicSubscription.newBuilder().setTopic(TOPIC_TRANS_EVENT).setPubsubName(PUBSUB_NAME)
                .build();

        DaprAppCallbackProtos.ListTopicSubscriptionsResponse subscriptionsResponse =
            DaprAppCallbackProtos.ListTopicSubscriptionsResponse.newBuilder().addSubscriptions(topicSubscription)
                .build();

        responseObserver.onNext(subscriptionsResponse);
        responseObserver.onCompleted();
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
            if ("pay".equals(request.getMethod())) {
                PayProto.PayRequest payRequest = PayProto.PayRequest.parseFrom(request.getData().getValue());
                PayProto.PayResponse response = pay(payRequest);

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

    public PayProto.PayResponse pay(PayProto.PayRequest request) {
        log.info("pay request: orderId: {}, productId: {}, price: {}, count: {}, discount: {}, amount: {}",
            request.getOrderId(), request.getProductId(), request.getPrice(), request.getCount(), request.getDiscount(),
            request.getAmount());

        PayProto.PayEvent payingEvent =
            PayProto.PayEvent.newBuilder().setOrderId(request.getOrderId()).setStatus(1).setMessage("paying 支付中")
                .build();

        publishRawEvent(payingEvent);

        requestTrans(request);

        return PayProto.PayResponse.newBuilder().setCode("0").setMessage("submit ok 已提交支付").build();
    }

    private void requestTrans(PayProto.PayRequest request) {
        BankProto.TransRequest transRequest =
            BankProto.TransRequest.newBuilder().setOrderId(request.getOrderId()).setAmount(request.getAmount()).build();

        log.info("trans request: orderId: {}, amount: {}", transRequest.getOrderId(), transRequest.getAmount());

        BankProto.TransResponse response =
            daprClient.invokeMethod(SERVICE_APP_ID_BANK, "trans", transRequest, HttpExtension.NONE, null,
                BankProto.TransResponse.class).block();

        assert response != null;

        log.info("trans response, code: {}, message: {}", response.getCode(), response.getMessage());
    }

    private void publishRawEvent(PayProto.PayEvent event) {
        log.info("publish pay event, orderId: {}, status: {}, message: {}", event.getOrderId(), event.getStatus(),
            event.getMessage());

        /*
         * event data will auto package with CloudEvent<?>
         */
        daprClient.publishEvent(PUBSUB_NAME, TOPIC_PAY_EVENT, event.toByteArray(), PUBLISH_METADATA).block();
    }

    private void publishPayResult(PayResult result) {
        log.info("publish cloud event pay result, orderId: {}, code: {}, message: {}", result.getOrderId(),
            result.getCode(), result.getMessage());

        CloudEvent<PayResult> cloudEvent = new CloudEvent<>();
        cloudEvent.setId(UUID.randomUUID().toString());
        cloudEvent.setType("pay_result");
        cloudEvent.setSpecversion("1");
        cloudEvent.setDatacontenttype(CloudEvent.CONTENT_TYPE);
        cloudEvent.setData(result);

        //Publishing messages
        daprClient.publishEvent(
            new PublishEventRequest(PUBSUB_NAME, "pay_result", cloudEvent).setContentType(CloudEvent.CONTENT_TYPE)
                .setMetadata(PUBLISH_METADATA)).block();
    }

    @Override
    public void onTopicEvent(DaprAppCallbackProtos.TopicEventRequest request,
        StreamObserver<DaprAppCallbackProtos.TopicEventResponse> responseObserver) {
        try {
            String topic = request.getTopic();
            ByteString data = request.getData();
            log.info("topic event, topic: {}, data: {}", topic, data);
            switch (topic) {
                case TOPIC_TRANS_EVENT:
                    /*
                     * TODO: public proto Object will get error, so convert base64 string. SHOULD have better way.
                     */
                    String base64 = jsonSerializer.deserialize(data.toByteArray(), String.class);
                    byte[] decode = Base64.getDecoder().decode(base64.getBytes());
                    BankProto.TransEvent transEvent = BankProto.TransEvent.parseFrom(decode);
                    processTransEvent(transEvent);
                    break;
                default:
                    log.info("unknown message");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    private void processTransEvent(BankProto.TransEvent event) {
        log.info("receive trans event, transId: {}, status: {}, message: {}", event.getTransId(), event.getStatus(),
            event.getMessage());

        PayResult payResult =
            PayResult.builder().orderId(event.getOrderId()).code(0).message("pay success 支付成功").build();
        publishPayResult(payResult);
    }
}
