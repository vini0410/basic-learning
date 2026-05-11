import http.client
import json
import concurrent.futures
import time

TOTAL_REQUESTS = 5000
CONCURRENCY = 100  # Dobrando a concorrência para testar a fila do HikariPool
TARGET_URL = "localhost"
TARGET_PORT = 8080
TARGET_PATH = "/accounts/transfer"

PAYLOAD = {
    "sourceAccount": "1111-1",
    "destinationAccount": "1234-5",
    "amount": 1.00
}

def send_one_request(_):
    try:
        conn = http.client.HTTPConnection(TARGET_URL, TARGET_PORT, timeout=5)
        headers = {'Content-type': 'application/json'}
        body = json.dumps(PAYLOAD)
        conn.request("POST", TARGET_PATH, body, headers)
        response = conn.getresponse()
        status = response.status
        response.read() # Consumir o corpo para liberar a conexão
        conn.close()
        return status
    except Exception as e:
        return f"Error: {e}"

def run_test():
    print(f"Disparando {TOTAL_REQUESTS} requisições com concorrência de {CONCURRENCY}...")
    start_time = time.time()
    
    with concurrent.futures.ThreadPoolExecutor(max_workers=CONCURRENCY) as executor:
        results = list(executor.map(send_one_request, range(TOTAL_REQUESTS)))
    
    end_time = time.time()
    duration = end_time - start_time
    
    success = results.count(200)
    errors = [r for r in results if r != 200]
    
    print("\n--- Resultados do Stress Test ---")
    print(f"Tempo Total: {duration:.2f}s")
    print(f"Reqs/seg: {TOTAL_REQUESTS/duration:.2f}")
    print(f"Sucesso: {success}")
    print(f"Falhas: {len(errors)}")
    if errors:
        print(f"Amostra de Erro: {errors[0]}")
    print("---------------------------------\n")

if __name__ == "__main__":
    run_test()
