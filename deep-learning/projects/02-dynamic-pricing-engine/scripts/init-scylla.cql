CREATE KEYSPACE IF NOT EXISTS pricing WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE pricing;

CREATE TABLE IF NOT EXISTS base_prices (
    service_id text PRIMARY KEY,
    base_price double,
    description text
);

-- Dados iniciais para teste
INSERT INTO base_prices (service_id, base_price, description) VALUES ('uber-x', 10.0, 'Serviço Econômico');
INSERT INTO base_prices (service_id, base_price, description) VALUES ('uber-black', 25.0, 'Serviço Premium');
INSERT INTO base_prices (service_id, base_price, description) VALUES ('load-test-service', 50.0, 'Serviço de Teste de Carga');
