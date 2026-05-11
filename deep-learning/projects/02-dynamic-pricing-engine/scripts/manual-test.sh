#!/bin/bash

BASE_URL="http://localhost:8080/price"

echo "--- Teste 1: Sucesso (200 OK) ---"
curl -X POST $BASE_URL \
     -H "Content-Type: application/json" \
     -d '{"serviceId": "uber-x", "lat": -23.55, "lon": -46.63, "userId": "user123"}'
echo -e "\n"

echo "--- Teste 2: Coordenadas Inválidas (400 Bad Request) ---"
curl -i -X POST $BASE_URL \
     -H "Content-Type: application/json" \
     -d '{"serviceId": "uber-x", "lat": 100.0, "lon": -46.63, "userId": "user123"}'
echo -e "\n"

echo "--- Teste 3: Simulação de Alta Carga/Loop ---"
for i in {1..5}
do
   echo "Request $i..."
   curl -s -X POST $BASE_URL \
        -H "Content-Type: application/json" \
        -d '{"serviceId": "uber-x", "lat": -23.55, "lon": -46.63, "userId": "user'$i'"}' \
        | grep -oE "multiplier\":[0-9.]+|warnings\":\[[^\]]*\]"
done
