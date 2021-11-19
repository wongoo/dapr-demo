license-check:
	# go install github.com/lsm-dev/license-header-checker/cmd/license-header-checker@latest
	license-header-checker -v -a -r apache-license.txt . java

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

run-self-host:
	sh ./run-self-host.sh

build-golang-docker:
	cd dapr-demo-discount && GOOS=linux go build -o ../main discount.go
	docker build -f docker/Dockerfile-golang . -t dapr-demo-discount:v1.0.0
	rm -f main

define buildDockerImage
	cp $(1)/target/$(1)-1.0.0-SNAPSHOT.jar app.jar
	docker build -f docker/Dockerfile . -t $(1):v1.0.0
	rm -f app.jar
endef

build-docker: build-golang-docker
	mvn clean package
	$(call buildDockerImage,dapr-demo-product)
	$(call buildDockerImage,dapr-demo-order)
	$(call buildDockerImage,dapr-demo-pay)
	$(call buildDockerImage,dapr-demo-bank)

	# delete override images
	docker images |grep "<none>" |awk '{print $3}'  |xargs --no-run-if-empty docker image rm --force
