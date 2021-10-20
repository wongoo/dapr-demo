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

import com.github.wongoo.dapr.pay.proto.PayProto;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.CommonProtos;
import io.grpc.stub.StreamObserver;

/**
 * @author wongoo
 */
public class DaprPayService extends AppCallbackGrpc.AppCallbackImplBase {

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
        System.out.printf("pay request: productId: %s, price: %f, count: %d, amount: %f\n", request.getProductId(),
            request.getPrice(), request.getCount(), request.getAmount());

        return PayProto.PayResponse.newBuilder().setCode("0").setMessage("submit ok").build();
    }
}
