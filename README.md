# Dapr Demo

## 1. prepare

The version of protocol buffer and grpc should match the dapr version:
* protocol buffer version: 3.13.0
* grpc version: 1.39.0

### 1.1. install protoc
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

### 1.2. install protoc-java-plugin
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

## 2. generate grpc java code

```bash
make proto-gen
```

## 3. How To Run

### 3.1. run dapr demo in self host

```bash
# install dapr
wget -q https://raw.githubusercontent.com/dapr/cli/master/install/install.sh -O - | /bin/bash


dapr init
dapr --version
# CLI version: 1.4.0
# Runtime version: 1.4.3


docker ps
# CONTAINER ID   IMAGE                    COMMAND                  CREATED         STATUS         PORTS                              NAMES
# 0dda6684dc2e   openzipkin/zipkin        "/busybox/sh run.sh"     2 minutes ago   Up 2 minutes   9410/tcp, 0.0.0.0:9411->9411/tcp   dapr_zipkin
# 9bf6ef339f50   redis                    "docker-entrypoint.s…"   2 minutes ago   Up 2 minutes   0.0.0.0:6379->6379/tcp             dapr_redis
# 8d993e514150   daprio/dapr              "./placement"            2 minutes ago   Up 2 minutes   0.0.0.0:6050->50005/tcp            dapr_placement

sh run-self-host.sh
```

