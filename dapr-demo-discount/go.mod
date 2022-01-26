module github.com/wongoo/dapr-demo/dapr-demo-discount

go 1.18

require github.com/dapr/go-sdk v1.3.1

require github.com/dapr/dapr v1.6.0 // indirect

require (
	github.com/golang/protobuf v1.5.2
	github.com/pkg/errors v0.9.1 // indirect
	github.com/wongoo/dapr-demo/dapr-demo-proto v1.0.0
	golang.org/x/net v0.0.0-20210825183410-e898025ed96a // indirect
	golang.org/x/sys v0.0.0-20211007075335-d3039528d8ac // indirect
	golang.org/x/text v0.3.7 // indirect
	google.golang.org/genproto v0.0.0-20210831024726-fe130286e0e2 // indirect
	google.golang.org/grpc v1.40.0 // indirect
	google.golang.org/protobuf v1.27.1 // indirect
)

replace github.com/wongoo/dapr-demo/dapr-demo-proto => ../dapr-demo-proto
