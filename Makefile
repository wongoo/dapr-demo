license-check:
	# go install github.com/lsm-dev/license-header-checker/cmd/license-header-checker@latest
	license-header-checker -v -a -r apache-license.txt . java go

test:
	mvn test

proto-gen:
	protoc --version
	# generate proto java object
	protoc --java_out=dapr-demo-proto/src/main/java dapr-demo-proto/src/main/proto/*.proto
	# generate proto java grpc
	protoc --plugin=protoc-gen-grpc-java=/usr/local/bin/protoc-gen-grpc-java-1.39.0 \
      --grpc-java_out="dapr-demo-proto/src/main/java" --proto_path="dapr-demo-proto/src/main/proto" "pay.proto"
	protoc --plugin=protoc-gen-grpc-java=/usr/local/bin/protoc-gen-grpc-java-1.39.0 \
      --grpc-java_out="dapr-demo-proto/src/main/java" --proto_path="dapr-demo-proto/src/main/proto" "bank.proto"
	# generate proto golang grpc
	protoc --go_out="dapr-demo-proto" "dapr-demo-proto/src/main/proto/discount.proto"
	cd dapr-demo-proto && go mod tidy

run-self-host:
	sh ./run-self-host.sh

build-java:
	mvn clean package

build-golang:
	mkdir -p dapr-demo-discount/target
	cd dapr-demo-discount && go build -o target/discount discount.go

build-golang-docker: build-golang
	cp dapr-demo-discount/target/discount main
	docker build -f docker/Dockerfile-golang . -t dapr-demo-discount:v1.0.0
	rm -f main

define buildJavaDockerImage
	cp $(1)/target/$(1)-1.0.0-SNAPSHOT.jar app.jar
	docker build -f docker/Dockerfile . -t $(1):v1.0.0
	rm -f app.jar
endef

build-java-docker: build-java
	$(call buildJavaDockerImage,dapr-demo-product)
	$(call buildJavaDockerImage,dapr-demo-order)
	$(call buildJavaDockerImage,dapr-demo-pay)
	$(call buildJavaDockerImage,dapr-demo-bank)

build-docker: build-golang-docker build-java-docker
	# delete override images
	docker images |grep "<none>" |awk '{print $$3}'  |xargs --no-run-if-empty docker image rm --force
