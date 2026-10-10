# AGENTS.md

This document outlines the roles and responsibilities of the various agents that will be involved in building and maintaining the **Keycloak server** project.

## Agent Overview

| Agent Name | Primary Responsibility | Interaction Points |
|------------|------------------------|--------------------|
| **SetupAgent** | Provision the Keycloak server environment (Docker, Kubernetes, or bare‑metal). Handles installation of required dependencies and initial server startup. | Works with **ConfigAgent** to apply base configuration after the server is up. |
| **ConfigAgent** | Apply and manage Keycloak realms, clients, users, and authentication flows. Generates configuration files (JSON/YAML) and updates the server via the Admin REST API. | Consumes outputs from **SetupAgent** and provides data for **TestAgent**. |
| **TestAgent** | Executes integration and health‑check tests against the running Keycloak instance. Validates authentication, token issuance, and SSO flows. | Reports results to **CI/CD Agent** and **OpsAgent**. |
| **CI/CD Agent** | Automates build and test pipelines for a test environment (e.g., GitHub Actions). No production deployment. | Automates build and test pipelines for a test environment (e.g., GitHub Actions). No production deployment. |
| **DeployAgent** | Deploys the Keycloak server to target environments (Docker Compose, Helm chart, or custom scripts). Handles roll‑backs and version upgrades. | Coordinates with **OpsAgent** for monitoring and scaling. |
| **OpsAgent** | Sets up monitoring, logging, and alerting (Prometheus, Grafana, Loki). Manages backup/restore processes and scaling policies. | Receives health status from **TestAgent** and logs from **DeployAgent**. |

## Agent Communication

Agents will primarily interact through **markdown artifacts** placed in the project's `artifacts/` directory and via **standardized JSON messages** on a shared message bus (e.g., a simple file‑based queue). Each agent should:

1. Write its output to a uniquely named artifact (e.g., `setup_output.md`).
2. Update a status file (`agent_status.json`) with a timestamp and success flag.
3. Optionally invoke other agents using the Antigravity `invoke_subagent` tool when further action is required.

## Extensibility

The architecture is designed to be modular. New agents can be added for specific concerns such as **SecurityAuditAgent** or **PerformanceTuningAgent** without modifying existing agents, as long as they follow the artifact‑based communication contract.
