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

package main

import (
	"context"
	"log"

	"github.com/dapr/go-sdk/service/common"
	daprd "github.com/dapr/go-sdk/service/grpc"
	"github.com/golang/protobuf/proto"
	"github.com/wongoo/dapr-demo/dapr-demo-proto/discount_proto"
)

// grpc example, see https://github.com/dapr/go-sdk/blob/main/examples/service/serving/grpc/main.go
func main() {
	s, err := daprd.NewService(":5054")
	if err != nil {
		log.Fatalf("failed to start the server: %v", err)
	}

	if err = s.AddServiceInvocationHandler("calc", calcHandler); err != nil {
		log.Fatalf("error adding binding handler: %v", err)
	}

	if err = s.Start(); err != nil {
		log.Fatalf("server error: %v", err)
	}

}

func calcHandler(ctx context.Context, in *common.InvocationEvent) (out *common.Content, err error) {
	log.Printf("binding - Data:%s, ContentType:%v", in.Data, in.ContentType)

	req := &discount_proto.DiscountRequest{}
	err = proto.Unmarshal(in.Data, req)
	if err != nil {
		return
	}

	log.Printf("discount request, productId: %s, price: %d, count: %d", req.ProductId, req.Price, req.Count)

	res := &discount_proto.DiscountResponse{
		Discount: 99,
		Code:     "0",
		Message:  "success",
	}

	log.Printf("discount response, discount: %d, code: %s, message: %s", res.Discount, res.Code, res.Message)

	var resData []byte
	resData, err = proto.Marshal(res)
	if err != nil {
		return
	}

	out = &common.Content{
		Data:        resData,
		ContentType: in.ContentType,
		DataTypeURL: in.DataTypeURL,
	}

	return
}
