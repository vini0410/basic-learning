import http.client
import json
import uuid
import time

def test_idempotency_api():
    print("\n[CENÁRIO 1] Testando Idempotência no AccountController (API)")
    key = "TEST-IDEM-" + str(uuid.uuid4())[:8]
    payload = {"sourceAccount": "1111-1", "destinationAccount": "1234-5", "amount": 10.0}
    headers = {'Content-type': 'application/json', 'X-Idempotency-Key': key}
    
    conn = http.client.HTTPConnection("localhost", 8080)
    
    # Primeira chamada
    print(f"1. Enviando requisição original (Key: {key})...")
    conn.request("POST", "/accounts/transfer", json.dumps(payload), headers)
    r1 = conn.getresponse()
    d1 = r1.read().decode()
    print(f"   Status: {r1.status} | ID Transação: {json.loads(d1).get('id')}")
    
    # Segunda chamada (Duplicada)
    print("2. Enviando duplicata imediata (Mesma Key)...")
    conn.request("POST", "/accounts/transfer", json.dumps(payload), headers)
    r2 = conn.getresponse()
    d2 = r2.read().decode()
    print(f"   Status: {r2.status} (Esperado 200 do cache/redis) | ID Transação: {json.loads(d2).get('id')}")
    
    if json.loads(d1).get('id') == json.loads(d2).get('id'):
        print("   >>> SUCESSO: O sistema retornou o mesmo objeto transacional.")
    else:
        print("   >>> FALHA: IDs diferentes detectados.")
    
    conn.close()

def test_idempotency_consumer_simulation():
    print("\n[CENÁRIO 2] Info: Idempotência no Consumer")
    print("Este teste é verificado via logs do sistema.")
    print("Se você enviar o mesmo TransactionId via Kafka manualmente ou se o Kafka retransmitir,")
    print("o log mostrará: 'Evento de liquidação duplicado detectado para transação ID: ...'")

def test_outbox_dlq_simulation():
    print("\n[CENÁRIO 3] Info: Teste de Resiliência Outbox (Retry & DLQ)")
    print("Para validar as retentativas e a movimentação para Dead Letter Table:")
    print("1. INTERROMPA o Kafka (docker-compose stop kafka).")
    print("2. Faça uma transferência via API.")
    print("3. Monitore o log da aplicação:")
    print("   - 'Erro ao processar evento Outbox ...'")
    print("   - 'Evento marcado para retentativa (Tentativa 1/5)'")
    print("   - ... até ...")
    print("   - 'Evento excedeu 5 tentativas. Movendo para a Dead Letter Table.'")
    print("4. Verifique a tabela no banco: SELECT * FROM outbox_dead_letter;")

if __name__ == "__main__":
    test_idempotency_api()
    test_idempotency_consumer_simulation()
    test_outbox_dlq_simulation()
