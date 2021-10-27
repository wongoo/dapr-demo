ps aux | grep -v grep | grep "dapr-demo"  | awk '{print $2}' | xargs --no-run-if-empty kill -9

sleep 2

# run dapr-demo-order
echo "---------> start dapr-demo-order"
dapr run --app-id dapr-demo-order --app-port 5050 \
  --components-path ~/.dapr/components  \
  -- java -jar dapr-demo-order/target/dapr-demo-order-1.0.0-SNAPSHOT.jar > order.log &

# run dapr-demo-product
sleep 1
echo "---------> start dapr-demo-product"
dapr run --app-id dapr-demo-product --app-port 5051 \
  -- java -jar dapr-demo-product/target/dapr-demo-product-1.0.0-SNAPSHOT.jar > product.log &

# run dapr-demo-pay
sleep 1
echo "---------> start dapr-demo-pay"
dapr run --app-id dapr-demo-pay --app-port 5052 \
  --components-path ~/.dapr/components  \
  --app-protocol grpc \
  -- java -jar dapr-demo-pay/target/dapr-demo-pay-1.0.0-SNAPSHOT.jar > pay.log &

# run dapr-demo-bank
sleep 1
echo "---------> start dapr-demo-bank"
dapr run --app-id dapr-demo-bank --app-port 5053 \
  --components-path ~/.dapr/components  \
  --app-protocol grpc \
  -- java -jar dapr-demo-bank/target/dapr-demo-bank-1.0.0-SNAPSHOT.jar > bank.log &

sleep 2

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

sleep 4

echo "---------------------- order log ----------------------"
tail -n 10 order.log
echo "---------------------- product log ----------------------"
tail -n 10 product.log
echo "---------------------- pay log ----------------------"
tail -n 10 pay.log
echo "---------------------- bank log ----------------------"
tail -n 10 bank.log

echo "over"
