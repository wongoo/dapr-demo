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

package com.github.wongoo.dapr.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wongoo.dapr.pay.model.PayResult;
import com.github.wongoo.dapr.pay.proto.PayProto;
import io.dapr.Topic;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.CloudEvent;
import io.dapr.client.domain.HttpExtension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wongoo
 */
@SpringBootApplication
@RestController
@Slf4j
public class OrderApp {

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }

    AtomicLong orderIdSequence = new AtomicLong(0);

    /**
     * create order
     */
    @PostMapping(path = "/create")
    public String handleMethod(@RequestBody Map<String, Object> request) {
        String productId = (String)request.get("productId");
        int count = (int)request.get("count");

        long orderId = orderIdSequence.incrementAndGet();

        log.info("create order, orderId: {}, productId: {}, count: {}", orderId, productId, count);

        double productPrice = getProductPrice(productId);

        pay(orderId, productId, productPrice, count);

        return "ok";
    }

    private static final String SERVICE_APP_ID_PRODUCT = "dapr-demo-product";
    private static final String SERVICE_APP_ID_PAY = "dapr-demo-pay";

    DaprClient daprClient;

    @PostConstruct
    public void init() {
        daprClient = (new DaprClientBuilder()).build();
    }

    public double getProductPrice(String productId) {
        Map<String, Object> request = new HashMap<>(1);
        request.put("productId", productId);

        Map<?, ?> response =
            daprClient.invokeMethod(SERVICE_APP_ID_PRODUCT, "get", request, HttpExtension.POST, null, Map.class)
                .block();

        assert response != null;
        double price = (double)response.get("price");

        log.info("product {}, price: {}", productId, price);

        return price;
    }

    public void pay(long orderId, String productId, double price, int count) {
        double discount = 0.12;
        double amount = price * count - discount;

        PayProto.PayRequest request =
            PayProto.PayRequest.newBuilder().setOrderId(orderId).setProductId(productId).setCount(count).setPrice(price)
                .setDiscount(discount).setAmount(amount).build();

        log.info("pay request: orderId: {}, productId: {}, price: {}, count: {}, discount: {}, amount: {}",
            request.getOrderId(), request.getProductId(), request.getPrice(), request.getCount(), request.getDiscount(),
            request.getAmount());

        PayProto.PayResponse response =
            daprClient.invokeMethod(SERVICE_APP_ID_PAY, "pay", request, HttpExtension.NONE, null,
                PayProto.PayResponse.class).block();

        assert response != null;

        log.info("pay response, code: {}, message: {}", response.getCode(), response.getMessage());
    }

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Handles a registered publish endpoint on this app.
     */
    @Topic(name = "pay_event", pubsubName = "pubsub")
    @PostMapping(path = "/raw_pay_event")
    public Mono<Void> handlePayEvent(@RequestBody(required = false) byte[] body,
        @RequestHeader Map<String, String> headers) {
        log.info("receive raw pay event");
        return Mono.fromRunnable(() -> {
            try {
                CloudEvent<?> cloudEvent = CloudEvent.deserialize(body);
                Object event = cloudEvent.getData();
                log.info("event class: {}, data: {}", event.getClass().getName(),
                    OBJECT_MAPPER.writeValueAsString(event));

                if (String.class.equals(event.getClass())) {
                    byte[] eventBytes = ((String)event).getBytes(StandardCharsets.UTF_8);
                    PayProto.PayEvent payEvent = PayProto.PayEvent.parseFrom(eventBytes);

                    log.info("subscribe pay event, orderId: {}, status: {}, message: {}", payEvent.getOrderId(),
                        payEvent.getStatus(), payEvent.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handles a registered publish endpoint on this app.
     */
    @Topic(name = "pay_result", pubsubName = "pubsub")
    @PostMapping(path = "/pay_result")
    public Mono<Void> handlePayResult(@RequestBody(required = false) CloudEvent<PayResult> cloudEvent) {
        log.info("receive raw pay result");
        return Mono.fromRunnable(() -> {
            try {
                PayResult result = cloudEvent.getData();
                log.info("pay result, orderId: {}, code: {}, message: {}", result.getOrderId(), result.getCode(),
                    result.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
