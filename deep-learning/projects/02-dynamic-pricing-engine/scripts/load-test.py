import threading
import json
import time
import random
import urllib.request
import urllib.error

# Configurações do teste
URL = "http://localhost:8080/price"
CONCURRENT_USERS = 60
DURATION_SECONDS = 20
TIMEOUT = 3.0

stats = {
    "total_requests": 0,
    "cache_hits": 0,
    "success_200": 0,
    "success_206": 0,
    "errors": 0,
    "latencies": []
}
stats_lock = threading.Lock()

# Payloads fixos para testar CACHE (mesma área, mesmo serviço)
fixed_payloads = [
    {"serviceId": "uber-x", "lat": -23.5501, "lon": -46.6331, "userId": "frequent-user-1"},
    {"serviceId": "uber-black", "lat": -23.5505, "lon": -46.6335, "userId": "frequent-user-2"}
]

def send_request():
    # 70% das vezes usa um payload fixo (testa CACHE)
    # 30% das vezes usa um aleatório (testa PROCESSAMENTO REAL)
    if random.random() < 0.7:
        payload = random.choice(fixed_payloads)
        is_cache_candidate = True
    else:
        payload = {
            "serviceId": "uber-x",
            "lat": -23.55 + (random.random() * 0.01),
            "lon": -46.63 + (random.random() * 0.01),
            "userId": f"random-user-{random.randint(1000, 9999)}"
        }
        is_cache_candidate = False

    data = json.dumps(payload).encode('utf-8')
    req = urllib.request.Request(URL, data=data, headers={'Content-Type': 'application/json'})
    
    start_time = time.time()
    try:
        with urllib.request.urlopen(req, timeout=TIMEOUT) as response:
            latency = (time.time() - start_time) * 1000
            status = response.getcode()
            with stats_lock:
                stats["total_requests"] += 1
                if status == 200:
                    stats["success_200"] += 1
                elif status == 206:
                    stats["success_206"] += 1
                
                # Se a latência for ultra-baixa (< 5ms), provavelmente veio do Cache L1
                if is_cache_candidate and latency < 5:
                    stats["cache_hits"] += 1
                    
                stats["latencies"].append(latency)
    except Exception as e:
        with stats_lock:
            stats["total_requests"] += 1
            stats["errors"] += 1

def worker():
    end_time = time.time() + DURATION_SECONDS
    while time.time() < end_time:
        send_request()
        time.sleep(0.01) # Alta frequência

print(f"Iniciando Estresse: {CONCURRENT_USERS} usuários em paralelo por {DURATION_SECONDS}s...")
threads = []
for i in range(CONCURRENT_USERS):
    t = threading.Thread(target=worker)
    t.start()
    threads.append(t)

for t in threads:
    t.join()

print("\n--- Relatório Final de Concorrência ---")
print(f"Total Requisições: {stats['total_requests']}")
print(f"Sucesso (Total): {stats['success_200'] + stats['success_206']}")
print(f"Estimativa de Cache Hits (L1): {stats['cache_hits']} ({(stats['cache_hits']/stats['total_requests'])*100:.1f}%)")
print(f"Fallbacks (Throughput Protegido): {stats['success_206']}")
print(f"Erros: {stats['errors']}")

if stats["latencies"]:
    latencies = sorted(stats["latencies"])
    avg = sum(latencies) / len(latencies)
    p95 = latencies[int(len(latencies) * 0.95)]
    p99 = latencies[int(len(latencies) * 0.99)]
    print(f"Latência Média: {avg:.2f} ms")
    print(f"Latência P95: {p95:.2f} ms")
    print(f"Latência P99: {p99:.2f} ms (pico)")
