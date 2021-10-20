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

import com.github.wongoo.dapr.pay.proto.PayProto;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.HttpExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wongoo
 */
@SpringBootApplication
@RestController
public class OrderApp {

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }

    /**
     * create order
     */
    @PostMapping(path = "/create")
    public String handleMethod(@RequestBody Map<String, Object> request) {
        String productId = (String)request.get("productId");
        int count = (int)request.get("count");

        System.out.printf("create order, productId: %s, count: %d\n", productId, count);

        double productPrice = getProductPrice(productId);

        pay(productId, productPrice, count);

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

        System.out.printf("product %s, price: %f\n", productId, price);

        return price;
    }

    public void pay(String productId, double price, int count) {
        double amount = price * count;

        PayProto.PayRequest request =
            PayProto.PayRequest.newBuilder().setProductId(productId).setCount(count).setPrice(price).setDiscount(0)
                .setAmount(amount).build();

        System.out.printf("pay request: productId: %s, price: %f, count: %d, amount: %f\n", request.getProductId(),
            request.getPrice(), request.getCount(), request.getAmount());

        PayProto.PayResponse response =
            daprClient.invokeMethod(SERVICE_APP_ID_PAY, "pay", request, HttpExtension.NONE, null,
                PayProto.PayResponse.class).block();

        assert response != null;

        System.out.printf("pay response, code: %s, message: %s\n", response.getCode(), response.getMessage());
    }
}
