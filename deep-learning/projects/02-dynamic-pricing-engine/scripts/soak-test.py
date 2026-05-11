import threading
import json
import time
import random
import urllib.request

# Configurações para SOAK TEST (Longa Duração)
URL = "http://localhost:8080/price"
CONCURRENT_USERS = 40
DURATION_MINUTES = 5 # Definido para 5 min para teste, pode ser aumentado
REPORT_INTERVAL = 30 # Reportar a cada 30s

stats = {
    "total": 0,
    "ok": 0,
    "fallback": 0,
    "err": 0,
    "latencies": []
}
lock = threading.Lock()

def send_request():
    # Mistura de cache hits e miss
    service = random.choice(["uber-x", "uber-black", "unknown-service"])
    payload = {
        "serviceId": service,
        "lat": -23.55, "lon": -46.63, 
        "userId": f"soak-user-{random.randint(1, 100)}"
    }
    data = json.dumps(payload).encode('utf-8')
    req = urllib.request.Request(URL, data=data, headers={'Content-Type': 'application/json'})
    
    start = time.time()
    try:
        with urllib.request.urlopen(req, timeout=2.0) as res:
            lat = (time.time() - start) * 1000
            with lock:
                stats["total"] += 1
                if res.getcode() == 200: stats["ok"] += 1
                elif res.getcode() == 206: stats["fallback"] += 1
                stats["latencies"].append(lat)
    except:
        with lock:
            stats["total"] += 1
            stats["err"] += 1

def reporter():
    start_time = time.time()
    end_time = start_time + (DURATION_MINUTES * 60)
    while time.time() < end_time:
        time.sleep(REPORT_INTERVAL)
        with lock:
            elapsed = time.time() - start_time
            rps = stats["total"] / elapsed
            avg = sum(stats["latencies"]) / len(stats["latencies"]) if stats["latencies"] else 0
            print(f"[SOAK PROGRESS] {elapsed:.0f}s | Req: {stats['total']} | RPS: {rps:.2f} | Avg Lat: {avg:.2f}ms | Err: {stats['err']}")

def worker():
    end_time = time.time() + (DURATION_MINUTES * 60)
    while time.time() < end_time:
        send_request()
        time.sleep(0.1)

print(f"--- INICIANDO SOAK TEST ({DURATION_MINUTES} min) ---")
threading.Thread(target=reporter, daemon=True).start()

threads = [threading.Thread(target=worker) for _ in range(CONCURRENT_USERS)]
for t in threads: t.start()
for t in threads: t.join()

print("\n--- RESULTADO FINAL SOAK TEST ---")
print(f"Total: {stats['total']} | Sucesso: {stats['ok']} | Fallback: {stats['fallback']} | Erros: {stats['err']}")
