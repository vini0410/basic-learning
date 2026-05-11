# 02-dynamic-pricing-engine

Engine de Preços Dinâmicos — base alinhada ao roadmap.

Visão resumida:
- Calcula preços em tempo real com baixa latência (surge pricing, geofencing, caching L1/L2).
- Tecnologias sugeridas: Quarkus (reativo), Caffeine (L1), Redis (L2), ScyllaDB/Mongo (NoSQL), Prometheus.

Como iniciar localmente:
- docker compose up -d  # sobe Redis e ScyllaDB (ou serviços de suporte)
- mvn package
- java -jar target/dynamic-pricing-engine-0.0.1-SNAPSHOT-runner.jar

Consulte o arquivo future-projects-roadmap.md na raiz para requisitos detalhados.
