#!/usr/bin/env python3
"""
Script de validação do proxy OpenResty → Flask.
Faz uma série de requisições automatizadas e exibe os resultados de forma legível.

Uso:
    python test_proxy.py                    # usa proxy em localhost:8081 (padrão)
    python test_proxy.py --host localhost --port 8081
    python test_proxy.py --direct           # aponta direto para o Flask em :5000
"""

import argparse
import json
import sys
import urllib.request
import urllib.error
from datetime import datetime


# ─────────────────────────── configuração ───────────────────────────────────

DEFAULT_PROXY_HOST = "localhost"
DEFAULT_PROXY_PORT = 8081
DEFAULT_FLASK_PORT = 5000

REQUESTS = [
    ("GET", "/health"),
    ("GET", "/route-a"),
    ("GET", "/route-b"),
    ("GET", "/health"),
    ("GET", "/route-a"),
    ("GET", "/route-b"),
    ("GET", "/health"),
    ("GET", "/route-a"),
    ("GET", "/route-b"),
    ("GET", "/health"),
]

# ─────────────────────────── helpers ────────────────────────────────────────

RESET  = "\033[0m"
GREEN  = "\033[32m"
RED    = "\033[31m"
CYAN   = "\033[36m"
YELLOW = "\033[33m"
BOLD   = "\033[1m"


def color(text: str, code: str) -> str:
    return f"{code}{text}{RESET}"


def print_separator(char: str = "─", width: int = 60) -> None:
    print(color(char * width, CYAN))


def make_request(url: str) -> dict:
    """Faz uma requisição GET e retorna status, headers relevantes e body."""
    start = datetime.now()
    try:
        req = urllib.request.Request(url)
        with urllib.request.urlopen(req, timeout=5) as resp:
            elapsed_ms = int((datetime.now() - start).total_seconds() * 1000)
            body_raw = resp.read().decode("utf-8")
            try:
                body = json.loads(body_raw)
            except json.JSONDecodeError:
                body = body_raw

            return {
                "ok": True,
                "status": resp.status,
                "elapsed_ms": elapsed_ms,
                "x_proxy_flavour": resp.headers.get("x-proxy-flavour", "—"),
                "server": resp.headers.get("Server", "—"),
                "body": body,
            }
    except urllib.error.HTTPError as e:
        elapsed_ms = int((datetime.now() - start).total_seconds() * 1000)
        return {"ok": False, "status": e.code, "elapsed_ms": elapsed_ms, "error": str(e)}
    except Exception as e:
        elapsed_ms = int((datetime.now() - start).total_seconds() * 1000)
        return {"ok": False, "status": 0, "elapsed_ms": elapsed_ms, "error": str(e)}


# ─────────────────────────── main ───────────────────────────────────────────

def run(base_url: str, label: str) -> None:
    print()
    print(color(f"  🔁  Validação automatizada do proxy OpenResty → Flask  ", BOLD))
    print(color(f"  Alvo : {base_url}  ({label})", YELLOW))
    print(color(f"  Total: {len(REQUESTS)} requisições", YELLOW))
    print_separator()

    results = []
    for i, (method, path) in enumerate(REQUESTS, 1):
        url = f"{base_url}{path}"
        result = make_request(url)
        results.append((method, path, result))

        status_str = str(result["status"])
        ok_marker  = color("✓", GREEN) if result["ok"] else color("✗", RED)
        status_col = color(status_str, GREEN if result["ok"] else RED)

        print(
            f"  [{i:02d}] {ok_marker}  {method:<4} {path:<12}"
            f"  {status_col}  {result['elapsed_ms']:>4}ms"
            f"  x-proxy-flavour: {color(result.get('x_proxy_flavour', '—'), CYAN)}"
        )
        if not result["ok"]:
            print(color(f"       ↳ erro: {result.get('error', '?')}", RED))

    # ── resumo ──
    print_separator()
    total  = len(results)
    passed = sum(1 for _, _, r in results if r["ok"])
    failed = total - passed

    print(f"  {color('Resultado:', BOLD)} {color(f'{passed}/{total} OK', GREEN)}  "
          f"{color(f'{failed} falhas', RED) if failed else ''}")

    if passed > 0:
        times = [r["elapsed_ms"] for _, _, r in results if r["ok"]]
        print(f"  Latência: min={min(times)}ms  max={max(times)}ms  avg={sum(times)//len(times)}ms")

    print_separator()
    print()

    # ── último body de cada rota ──
    print(color("  📦  Último body por rota:", BOLD))
    seen = {}
    for method, path, result in reversed(results):
        if path not in seen and result.get("body"):
            seen[path] = result["body"]

    for path, body in seen.items():
        print(f"  {color(path, CYAN)} → {json.dumps(body, ensure_ascii=False)}")

    print()
    sys.exit(0 if failed == 0 else 1)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Valida o proxy OpenResty automaticamente.")
    parser.add_argument("--host", default=DEFAULT_PROXY_HOST, help="Host do alvo (padrão: localhost)")
    parser.add_argument("--port", type=int, default=DEFAULT_PROXY_PORT, help="Porta do proxy (padrão: 8081)")
    parser.add_argument("--direct", action="store_true",
                        help=f"Aponta direto para o Flask em :{DEFAULT_FLASK_PORT} (pula o proxy)")
    args = parser.parse_args()

    port  = DEFAULT_FLASK_PORT if args.direct else args.port
    label = f"Flask direto (sem proxy)" if args.direct else "via proxy OpenResty"
    run(f"http://{args.host}:{port}", label)
