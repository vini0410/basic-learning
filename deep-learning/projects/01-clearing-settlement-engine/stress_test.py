import http.client
import json
import threading
import time
import sys

# Configurações do Teste
TOTAL_REQUESTS = 100
CONCURRENCY = 20  # Menos threads simultâneas para não saturar o socket
TARGET_URL = "localhost"
TARGET_PORT = 8080
TARGET_PATH = "/accounts/transfer"

# Dados da Transferência
PAYLOAD = {
    "sourceAccount": "1111-1",
    "destinationAccount": "1234-5",
    "amount": 1.00
}

success_count = 0
error_count = 0
lock = threading.Lock()

def make_request():
    global success_count, error_count
    try:
        conn = http.client.HTTPConnection(TARGET_URL, TARGET_PORT, timeout=10)
        headers = {'Content-type': 'application/json'}
        body = json.dumps(PAYLOAD)
        
        conn.request("POST", TARGET_PATH, body, headers)
        response = conn.getresponse()
        resp_data = response.read().decode()
        
        if response.status == 200:
            with lock:
                success_count += 1
        else:
            with lock:
                error_count += 1
                print(f"Erro {response.status}: {resp_data}")
        
        conn.close()
    except Exception as e:
        with lock:
            error_count += 1
            print(f"Exceção: {e}")

def run_stress_test():
    print(f"--- Iniciando Stress Test: {TOTAL_REQUESTS} requisições ---")
    start_time = time.time()
    
    threads = []
    # Disparando em lotes para manter a concorrência controlada
    for i in range(TOTAL_REQUESTS):
        t = threading.Thread(target=make_request)
        threads.append(t)
        t.start()
        
        # Limita a concorrência para não travar o SO cliente
        if len(threads) >= CONCURRENCY:
            for t in threads:
                t.join()
            threads = []
            
    # Join nas threads restantes
    for t in threads:
        t.join()
        
    end_time = time.time()
    duration = end_time - start_time
    
    print("\n--- Resultados do Teste ---")
    print(f"Tempo Total: {duration:.2f} segundos")
    print(f"Requisições por segundo: {TOTAL_REQUESTS / duration:.2f}")
    print(f"Sucessos: {success_count}")
    print(f"Falhas: {error_count}")
    print("---------------------------\n")

if __name__ == "__main__":
    run_stress_test()
