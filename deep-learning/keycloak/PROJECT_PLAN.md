# Project Plan for Keycloak Server

## Goal
Create a reproducible **test** Keycloak authentication server (no production intent).

## High‑Level Milestones

| Milestone | Description | Owner (Agent) | Artefact |
|-----------|-------------|---------------|----------|
| **1. Environment Setup** | Install Docker, create a base `docker-compose.yml` (or Helm chart) for Keycloak. | SetupAgent | `setup_output.md` |
| **2. Base Configuration** | Define realms, clients, users in JSON/YAML. Provide scripts to import via Admin REST API. | ConfigAgent | `config_spec.md` |
| **3. HTTP Request Files** | Create `.http` files for manual API testing (IntelliJ/VSCode HTTP client). | TestAgent | `http_files.md` |
| **4. Python Test Scripts** | Write Python scripts (using `requests`) to automate login, token issuance, and SSO flow tests. | TestAgent | `python_tests.py` |
| **5. CI/CD Pipeline** | Configure GitHub Actions to build Docker image, run the Python test suite, and push image to registry. | CI/CD Agent | `ci_cd_workflow.yml` |
| **6. Deployment Automation** | Deploy to staging/production using Docker Compose or Helm, with versioned releases and rollback. | DeployAgent | `deployment_guide.md` |
| **7. Operations Setup** | Add Prometheus metrics, Grafana dashboards, logging, backup strategy. | OpsAgent | `ops_runbook.md` |

## Detailed Steps
1. **Create repository structure**
   ```
   keycloak/
   ├─ docs/                 # markdown planning artefacts
   │   ├─ AGENTS.md          # agent responsibilities (already created)
   │   ├─ PROJECT_PLAN.md    # this document
   │   └─ ...
   ├─ docker/               # Dockerfile, compose, helm chart
   ├─ config/               # realm/client JSON files
   ├─ tests/                # integration test suite
   ├─ .github/workflows/    # CI/CD workflow files
   └─ README.md
   ```
2. **Define Docker Compose** – include Keycloak image, PostgreSQL DB, expose ports, set env vars.
3. **Write realm definition** – minimal realm with one client (`my-app`) and a test user.
4. **Develop test suite** – use Python `requests` or Java `Keycloak Admin Client` to automate login flow.
5. **CI/CD** – build Docker image, run tests against a temporary container, push image to registry on success.
6. **Deploy** – scripts to pull image, apply Docker Compose/Helm, perform post‑deployment import of configuration.
7. **Ops** – enable Keycloak metrics, expose `/metrics`, configure Grafana dashboard, set up daily DB backup.

## Next Actions
- Create `docker-compose.yml` and related Docker files.
- Draft initial realm JSON (`realm.json`).
- Scaffold test directory with a sample authentication test.
- Add a placeholder GitHub Actions workflow.

*All agents should write their outputs to the `docs/` folder as markdown artifacts and update `agent_status.json`.*


Para rodar localmente e copiar o novo JAR do provider:

    ./gradlew :keycloak-custom:jar
    cp keycloak-custom/build/libs/keycloak-custom-1.0.0-SNAPSHOT.jar local-build-test/keycloak-dist/providers/
    local-build-test/run-keycloak-local.sh