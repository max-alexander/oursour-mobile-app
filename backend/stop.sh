#!/usr/bin/env bash
cd "$(dirname "$0")"

SERVICIOS="pedido-service:8084 receta-service:8083 catalogo-service:8082 usuario-service:8081 api-gateway:8080 eureka-server:8761"

for servicio in $SERVICIOS; do
	nombre=${servicio%%:*}
	puerto=${servicio##*:}
	pids=$(lsof -ti tcp:"$puerto" -sTCP:LISTEN 2>/dev/null)
	if [ -n "$pids" ]; then
		kill $pids
		echo "  ✓ $nombre detenido"
	else
		echo "  • $nombre no estaba corriendo"
	fi
done

if [ "$1" = "--todo" ]; then
	docker compose stop >/dev/null 2>&1 && echo "  ✓ Oracle detenido"
else
	echo "Oracle sigue corriendo (usa ./stop.sh --todo para apagarlo también)"
fi
