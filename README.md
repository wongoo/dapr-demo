# Dapr Demo

## 1. prepare

The version of protocol buffer and grpc should match the dapr version, 
which can be found at https://github.com/dapr/java-sdk/blob/master/pom.xml.

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
# CLI version: 1.5.0
# Runtime version: 1.4.3


docker ps
# CONTAINER ID   IMAGE                    COMMAND                  CREATED         STATUS         PORTS                              NAMES
# 0dda6684dc2e   openzipkin/zipkin        "/busybox/sh run.sh"     2 minutes ago   Up 2 minutes   9410/tcp, 0.0.0.0:9411->9411/tcp   dapr_zipkin
# 9bf6ef339f50   redis                    "docker-entrypoint.s…"   2 minutes ago   Up 2 minutes   0.0.0.0:6379->6379/tcp             dapr_redis
# 8d993e514150   daprio/dapr              "./placement"            2 minutes ago   Up 2 minutes   0.0.0.0:6050->50005/tcp            dapr_placement

sh run-self-host.sh
```

### 3.2. run dapr demo in kubernetes

install tools:
```bash
# install kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
curl -LO "https://dl.k8s.io/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl.sha256"
echo "$(<kubectl.sha256) kubectl" | sha256sum --check
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
kubectl version --client

# install helm
# find the latest version from https://mirrors.huaweicloud.com/helm
wget https://mirrors.huaweicloud.com/helm/v3.7.1/helm-v3.7.1-linux-amd64.tar.gz
tar -xvf helm-v3.7.1-linux-amd64.tar.gz
mv linux-amd64/helm /usr/local/bin/

# https://docs.dapr.io/operations/hosting/kubernetes/cluster/setup-minikube/
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube
minikube config set vm-driver docker
minikube start --cpus=4 --memory=4096
```

install dapr in kubernetes:
```bash
# ----------------------------------
# install dapr cli using command (NOT required if already installed using helm)
wget -q https://raw.githubusercontent.com/dapr/cli/master/install/install.sh -O - | /bin/bash
# The -k flag initializes Dapr on the Kubernetes cluster in your current context.
# Setup & configure mutual TLS: https://docs.dapr.io/operations/security/mtls/
# If custom certificates have not been provided, Dapr will automatically create and persist self signed certs valid for one year. In Kubernetes, 
# the certs are persisted to a secret that resides in the namespace of the Dapr system pods, accessible only to them.
dapr init -k  --enable-mtls=false
# Uninstall Dapr on Kubernetes with CLI  
# dapr uninstall -k
kubectl get pods --namespace dapr-system
dapr status -k
# ----------------------------------
```

[install dapr in kubernetes using helm](doc/install-dapr-in-kubernetes-using-helm.md) 

install redis:
```bash
# Install Redis into your cluster
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
helm install redis bitnami/redis
#    redis-master.default.svc.cluster.local for read/write operations (port 6379)
#    redis-replicas.default.svc.cluster.local for read-only operations (port 6379)
# export REDIS_PASSWORD=$(kubectl get secret --namespace default redis -o jsonpath="{.data.redis-password}" | base64 --decode)
# see the Redis containers now running in your cluster
kubectl get pods

cat <<EOF > redis-state.yaml 
apiVersion: dapr.io/v1alpha1
kind: Component
metadata:
  name: statestore
  namespace: default
spec:
  type: state.redis
  version: v1
  metadata:
  - name: redisHost
    value: redis-master.default.svc.cluster.local:6379
  - name: redisPassword
    secretKeyRef:
      name: redis
      key: redis-password
EOF


cat <<EOF > redis-pubsub.yaml 
apiVersion: dapr.io/v1alpha1
kind: Component
metadata:
  name: pubsub
  namespace: default
spec:
  type: pubsub.redis
  version: v1
  metadata:
  - name: redisHost
    value: redis-master.default.svc.cluster.local:6379
  - name: redisPassword
    secretKeyRef:
      name: redis
      key: redis-password
EOF

kubectl apply -f redis-state.yaml
kubectl apply -f redis-pubsub.yaml

```

build demo docker images:
```bash

# use minikube docker registry
eval $(minikube -p minikube docker-env)
# build project images
make build-docker
```

run demo in kubernetes:
```bash
sh run-in-kubernetes.sh
```

