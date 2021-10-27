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
