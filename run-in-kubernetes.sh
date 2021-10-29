
kubectl apply -f kubernetes/dapr-demo-product.yaml
kubectl apply -f kubernetes/dapr-demo-order.yaml
kubectl apply -f kubernetes/dapr-demo-pay.yaml
kubectl apply -f kubernetes/dapr-demo-bank.yaml

sleep 5

kubectl port-forward service/dapr-demo-order 5050:80 &
kubectl port-forward service/dapr-demo-product 5051:80 &

# check product service
# productServiceIp=$(kubectl get svc dapr-demo-product -ojsonpath='{.spec.clusterIP}')
curl --location --request POST "http://localhost:5051/get" \
--header 'Content-Type: application/json' \
--data-raw '{
    "productId": "123"
}'

# create order
# orderServiceIp=$(kubectl get svc dapr-demo-order -ojsonpath='{.spec.clusterIP}')
curl --location --request POST "http://localhost:5050/create" \
--header 'Content-Type: application/json' \
--data-raw '{
    "productId": "123",
    "count": 2
}'

sleep 5

echo "---------------------- product log ----------------------"
kubectl logs --tail 100 -f $(kubectl get pods |grep dapr-demo-product | awk '{print $1}') dapr-demo-product
echo "---------------------- pay log ----------------------"
kubectl logs --tail 100 -f $(kubectl get pods |grep dapr-demo-pay | awk '{print $1}') dapr-demo-pay
echo "---------------------- bank log ----------------------"
kubectl logs --tail 100 -f $(kubectl get pods |grep dapr-demo-bank | awk '{print $1}') dapr-demo-bank
echo "---------------------- order log ----------------------"
kubectl logs --tail 100 -f $(kubectl get pods |grep dapr-demo-order | awk '{print $1}') dapr-demo-order

kubectl delete -f kubernetes/dapr-demo-product.yaml
kubectl delete -f kubernetes/dapr-demo-order.yaml
kubectl delete -f kubernetes/dapr-demo-pay.yaml
kubectl delete -f kubernetes/dapr-demo-bank.yaml
