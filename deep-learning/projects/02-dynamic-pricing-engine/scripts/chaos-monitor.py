import time
import urllib.request
import json
import sys

URL = "http://localhost:8080/price"
payload = {"serviceId": "uber-x", "lat": -23.55, "lon": -46.63, "userId": "chaos-user"}
data = json.dumps(payload).encode('utf-8')

print("--- INICIANDO MONITORAMENTO DE CAOS ---")
print("A aplicação deve degradar graciosamente se os serviços caírem.")
print("-" * 40)
print("DICA: Use 'docker stop redis' ou 'docker stop scylla' em outro terminal.")
print("-" * 40)

try:
    for i in range(100):
        start = time.time()
        try:
            req = urllib.request.Request(URL, data=data, headers={'Content-Type': 'application/json'})
            with urllib.request.urlopen(req, timeout=2.0) as response:
                latency = (time.time() - start) * 1000
                print(f"Iteração {i:02d}: Status {response.getcode()} | Latência: {latency:.2f}ms")
        except urllib.error.HTTPError as e:
            print(f"Iteração {i:02d}: Erro HTTP {e.code}")
        except Exception as e:
            print(f"Iteração {i:02d}: Falha de Conexão/Timeout ({type(e).__name__})")
        time.sleep(1)
except KeyboardInterrupt:
    print("\nTeste interrompido pelo usuário.")
