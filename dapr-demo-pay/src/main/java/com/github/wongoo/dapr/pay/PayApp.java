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

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wongoo
 */
@Slf4j
public class PayApp {

    public static void main(String[] args) throws Exception {
        startGrpc();
    }

    public static void startGrpc() throws Exception {
        int port = 5052;
        DaprPayService service = new DaprPayService();
        Server server = ServerBuilder.forPort(port).addService(service).build().start();
        log.info("Server: started listening on port {}", port);

        // Now we handle ctrl+c (or any other JVM shutdown)
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Server: shutting down gracefully ...");
                server.shutdown();
                log.info("Server: Bye.");
            }
        });

        server.awaitTermination();
    }

}
