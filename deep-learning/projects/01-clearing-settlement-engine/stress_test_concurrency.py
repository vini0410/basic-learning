import http.client
import json
import threading
import time
import sys

def run_load_test(total_requests, concurrency):
    print(f"\n--- Iniciando Carga: {total_requests} requisições (Concorrência: {concurrency}) ---")
    target_url = "localhost"
    target_port = 8080
    path = "/accounts/transfer"
    
    # Valor baixo para não esgotar o saldo durante o teste massivo
    payload = {"sourceAccount": "1111-1", "destinationAccount": "1234-5", "amount": 0.001}
    success = 0
    errors = 0
    lock = threading.Lock()

    def task():
        nonlocal success, errors
        try:
            conn = http.client.HTTPConnection(target_url, target_port, timeout=30)
            conn.request("POST", path, json.dumps(payload), {'Content-type': 'application/json'})
            response = conn.getresponse()
            if response.status == 200:
                with lock: success += 1
            else:
                # print(f"Erro {response.status}: {response.read().decode()}")
                with lock: errors += 1
            response.read() # garante leitura total
            conn.close()
        except Exception as e:
            with lock: errors += 1
            # print(f"Exceção: {e}")

    start = time.time()
    threads = []
    
    # Processamento em batches para não explodir o limite de descritores de arquivo do SO
    for i in range(0, total_requests, concurrency):
        batch_size = min(concurrency, total_requests - i)
        current_batch = []
        for _ in range(batch_size):
            t = threading.Thread(target=task)
            current_batch.append(t)
            t.start()
        
        for t in current_batch:
            t.join()
            
    duration = time.time() - start
    print(f"\n--- Resultado ({total_requests} reqs) ---")
    print(f"Tempo Total: {duration:.2f}s")
    print(f"Vazão: {total_requests/duration:.2f} req/s")
    print(f"Sucessos: {success}")
    print(f"Falhas: {errors}")
    print("--------------------------------------\n")

if __name__ == "__main__":
    # Ex: python3 stress_test_concurrency.py 1000 50
    reqs = int(sys.argv[1]) if len(sys.argv) > 1 else 1000
    conc = int(sys.argv[2]) if len(sys.argv) > 2 else 50
    run_load_test(reqs, conc)
