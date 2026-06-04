# python3 -m pip install requests && python3 test_rate_limit.py
import requests
import json
import time

def run_rate_limit_test():
    # URL do seu TestController (ajuste a porta se necessário)
    url = "http://localhost:8080"
    # Quantidade X de requisições para testar (acima do limite de 10 configurado)
    total_requests = 15

    print(f"Iniciando teste de Rate Limit: {total_requests} requisições para {url}\n")

    for i in range(1, total_requests + 1):
        try:
            response = requests.get(url)
            status = response.status_code
            content = response.text
            print(f"Requisição {i:02d} | Status: {status} | Resposta: {content}")
        except Exception as e:
            print(f"Requisição {i:02d} | Falhou: {e}")

    print("\nTeste1 finalizado.")

    print(f"\nIniciando Teste2 com sleep de 1s entre as requisições...\n")
    for i in range(1, total_requests + 1):
        try:
            time.sleep(1)
            response = requests.get(url)
            status = response.status_code
            content = response.text
            print(f"Requisição {i:02d} | Status: {status} | Resposta: {content}")
        except Exception as e:
            print(f"Requisição {i:02d} | Falhou: {e}")

    print("\nTeste2 finalizado.")

if __name__ == "__main__":
    run_rate_limit_test()