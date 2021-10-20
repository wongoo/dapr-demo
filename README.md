
## prepare

The version of protocol buffer and grpc should match the dapr version:
* protocol buffer version: 3.13.0
* grpc version: 1.39.0

1. install protoc
```bash
# ref: https://google.github.io/proto-lens/installing-protoc.html
# ---- mac
# PROTOC_ZIP=protoc-3.13.0-osx-x86_64.zip
# ---- linux
PROTOC_ZIP=protoc-3.13.0-linux-x86_64.zip
curl -OL https://github.com/protocolbuffers/protobuf/releases/download/v3.13.0/$PROTOC_ZIP
sudo unzip -o $PROTOC_ZIP -d /usr/local bin/protoc
sudo unzip -o $PROTOC_ZIP -d /usr/local 'include/*'
sudo chmod +x /usr/local/bin/protoc
rm -f $PROTOC_ZIP
```

2. install protoc-java-plugin
```bash
# ref: https://github.com/grpc/grpc-java/tree/master/compiler
# 1. Navigate to https://mvnrepository.com/artifact/io.grpc/protoc-gen-grpc-java
# 2. Click into a version
# 3. Click "Files"

# ---- mac
# wget https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.39.0/protoc-gen-grpc-java-1.39.0-osx-x86_64.exe
# sudo mv protoc-gen-grpc-java-1.39.0-osx-x86_64.exe /usr/local/bin/protoc-gen-grpc-java-1.39.0
# ---- linux
wget https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.39.0/protoc-gen-grpc-java-1.39.0-linux-x86_64.exe
sudo mv protoc-gen-grpc-java-1.39.0-linux-x86_64.exe /usr/local/bin/protoc-gen-grpc-java-1.39.0
sudo chmod +x /usr/local/bin/protoc-gen-grpc-java-1.39.0
```

## generate grpc java code

```bash
make proto-gen
```

## How to run demo

```bash
# run dapr-demo-order
dapr run --app-id dapr-demo-order --app-port 5050 -- java -jar dapr-demo-order/target/dapr-demo-order-1.0.0-SNAPSHOT.jar

# run dapr-demo-product
dapr run --app-id dapr-demo-product --app-port 5051 -- java -jar dapr-demo-product/target/dapr-demo-product-1.0.0-SNAPSHOT.jar

# run dapr-demo-pay
dapr run --app-id dapr-demo-pay --app-port 5052 --app-protocol grpc -- java -jar dapr-demo-pay/target/dapr-demo-pay-1.0.0-SNAPSHOT.jar

# check product service
curl --location --request POST 'http://localhost:5051/get' \
--header 'Content-Type: application/json' \
--data-raw '{
    "productId": "123"
}'

# create order
curl --location --request POST 'http://localhost:5050/create' \
--header 'Content-Type: application/json' \
--data-raw '{
    "productId": "123",
    "count": 2
}'



```
