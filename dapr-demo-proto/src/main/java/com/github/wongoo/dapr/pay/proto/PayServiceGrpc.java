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

package com.github.wongoo.dapr.pay.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * User Code definitions
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.39.0)",
    comments = "Source: pay.proto")
public final class PayServiceGrpc {

  private PayServiceGrpc() {}

  public static final String SERVICE_NAME = "pay.PayService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.github.wongoo.dapr.pay.proto.PayProto.PayRequest,
      com.github.wongoo.dapr.pay.proto.PayProto.PayResponse> getPayMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Pay",
      requestType = com.github.wongoo.dapr.pay.proto.PayProto.PayRequest.class,
      responseType = com.github.wongoo.dapr.pay.proto.PayProto.PayResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.wongoo.dapr.pay.proto.PayProto.PayRequest,
      com.github.wongoo.dapr.pay.proto.PayProto.PayResponse> getPayMethod() {
    io.grpc.MethodDescriptor<com.github.wongoo.dapr.pay.proto.PayProto.PayRequest, com.github.wongoo.dapr.pay.proto.PayProto.PayResponse> getPayMethod;
    if ((getPayMethod = PayServiceGrpc.getPayMethod) == null) {
      synchronized (PayServiceGrpc.class) {
        if ((getPayMethod = PayServiceGrpc.getPayMethod) == null) {
          PayServiceGrpc.getPayMethod = getPayMethod =
              io.grpc.MethodDescriptor.<com.github.wongoo.dapr.pay.proto.PayProto.PayRequest, com.github.wongoo.dapr.pay.proto.PayProto.PayResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Pay"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.wongoo.dapr.pay.proto.PayProto.PayRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.wongoo.dapr.pay.proto.PayProto.PayResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PayServiceMethodDescriptorSupplier("Pay"))
              .build();
        }
      }
    }
    return getPayMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PayServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PayServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PayServiceStub>() {
        @java.lang.Override
        public PayServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PayServiceStub(channel, callOptions);
        }
      };
    return PayServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PayServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PayServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PayServiceBlockingStub>() {
        @java.lang.Override
        public PayServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PayServiceBlockingStub(channel, callOptions);
        }
      };
    return PayServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PayServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PayServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PayServiceFutureStub>() {
        @java.lang.Override
        public PayServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PayServiceFutureStub(channel, callOptions);
        }
      };
    return PayServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * User Code definitions
   * </pre>
   */
  public static abstract class PayServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void pay(com.github.wongoo.dapr.pay.proto.PayProto.PayRequest request,
        io.grpc.stub.StreamObserver<com.github.wongoo.dapr.pay.proto.PayProto.PayResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPayMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPayMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.github.wongoo.dapr.pay.proto.PayProto.PayRequest,
                com.github.wongoo.dapr.pay.proto.PayProto.PayResponse>(
                  this, METHODID_PAY)))
          .build();
    }
  }

  /**
   * <pre>
   * User Code definitions
   * </pre>
   */
  public static final class PayServiceStub extends io.grpc.stub.AbstractAsyncStub<PayServiceStub> {
    private PayServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PayServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PayServiceStub(channel, callOptions);
    }

    /**
     */
    public void pay(com.github.wongoo.dapr.pay.proto.PayProto.PayRequest request,
        io.grpc.stub.StreamObserver<com.github.wongoo.dapr.pay.proto.PayProto.PayResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPayMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * User Code definitions
   * </pre>
   */
  public static final class PayServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<PayServiceBlockingStub> {
    private PayServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PayServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PayServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.github.wongoo.dapr.pay.proto.PayProto.PayResponse pay(com.github.wongoo.dapr.pay.proto.PayProto.PayRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPayMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * User Code definitions
   * </pre>
   */
  public static final class PayServiceFutureStub extends io.grpc.stub.AbstractFutureStub<PayServiceFutureStub> {
    private PayServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PayServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PayServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.wongoo.dapr.pay.proto.PayProto.PayResponse> pay(
        com.github.wongoo.dapr.pay.proto.PayProto.PayRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPayMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PAY = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PayServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PayServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PAY:
          serviceImpl.pay((com.github.wongoo.dapr.pay.proto.PayProto.PayRequest) request,
              (io.grpc.stub.StreamObserver<com.github.wongoo.dapr.pay.proto.PayProto.PayResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class PayServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PayServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.github.wongoo.dapr.pay.proto.PayProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PayService");
    }
  }

  private static final class PayServiceFileDescriptorSupplier
      extends PayServiceBaseDescriptorSupplier {
    PayServiceFileDescriptorSupplier() {}
  }

  private static final class PayServiceMethodDescriptorSupplier
      extends PayServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PayServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PayServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PayServiceFileDescriptorSupplier())
              .addMethod(getPayMethod())
              .build();
        }
      }
    }
    return result;
  }
}
