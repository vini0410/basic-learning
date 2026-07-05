import logging

import os

from flask import Flask, jsonify, request

app = Flask(__name__)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s [%(name)s] %(message)s"
)
logger = logging.getLogger("service")

@app.get("/health")
def health():
    logger.info("health check from %s", request.remote_addr)
    return jsonify({"status": "ok"})

@app.get("/route-a")
def route_a():
    logger.info("route a hit from %s", request.remote_addr)
    return jsonify({"route": "a", "message": "hi from route a"})

@app.get("/route-b")
def route_b():
    logger.info("route b hit from %s", request.remote_addr)
    return jsonify({"route": "b", "message": "hi from route b"})

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    logger.info("Starting on %s", port)
    app.run(host="0.0.0.0", port=port)
