module github.com/wongoo/dapr-demo/dapr-demo-discount

go 1.17

require github.com/dapr/go-sdk v1.2.0

require (
	github.com/golang/protobuf v1.5.0 // indirect
	github.com/pkg/errors v0.9.1 // indirect
	github.com/wongoo/dapr-demo/dapr-demo-proto v1.0.0
	golang.org/x/net v0.0.0-20201202161906-c7110b5ffcbb // indirect
	golang.org/x/sys v0.0.0-20201202213521-69691e467435 // indirect
	golang.org/x/text v0.3.4 // indirect
	google.golang.org/genproto v0.0.0-20201204160425-06b3db808446 // indirect
	google.golang.org/grpc v1.34.0 // indirect
	google.golang.org/protobuf v1.27.1 // indirect
)

replace github.com/wongoo/dapr-demo/dapr-demo-proto => ../dapr-demo-proto
