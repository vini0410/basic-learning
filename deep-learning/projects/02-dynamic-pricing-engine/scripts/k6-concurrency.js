import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 50, // 50 usuários simultâneos (como no roadmap)
    duration: '30s',
};

const BASE_URL = 'http://localhost:8080/price';

export default function () {
    const payload = JSON.stringify({
        serviceId: 'surge-test-service',
        lat: -23.55 + (Math.random() * 0.01), // Simula geofencing em micro-região
        lon: -46.63 + (Math.random() * 0.01),
        userId: `user-${__VU}-${__ITER}`
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.post(BASE_URL, payload, params);

    check(res, {
        'is status 200 or 206': (r) => r.status === 200 || r.status === 206,
        'transaction time < 200ms': (r) => r.timings.duration < 200,
    });

    sleep(0.1); // 100ms entre requisições por VU
}
