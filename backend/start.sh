#!/usr/bin/env bash
cd "$(dirname "$0")"
mkdir -p logs

SERVICIOS="api-gateway:8080 usuario-service:8081 catalogo-service:8082 receta-service:8083 pedido-service:8084"

puerto_ocupado() {
	lsof -ti tcp:"$1" -sTCP:LISTEN >/dev/null 2>&1
}

esperar_salud() {
	local nombre=$1 puerto=$2 limite=$((SECONDS + 240))
	until curl -s "localhost:$puerto/actuator/health" | grep -q '"UP"'; do
		if [ $SECONDS -ge $limite ]; then
			echo "  ✗ $nombre no arrancó (revisa logs/$nombre.log)"
			return 1
		fi
		sleep 2
	done
	echo "  ✓ $nombre  http://localhost:$puerto"
}

levantar() {
	local nombre=$1 puerto=$2
	if puerto_ocupado "$puerto"; then
		echo "  • $nombre ya estaba corriendo en el puerto $puerto"
		return
	fi
	(cd "$nombre" && nohup ./mvnw -q spring-boot:run > "../logs/$nombre.log" 2>&1 &)
}

if ! docker info >/dev/null 2>&1; then
	echo "Docker no está corriendo. Abre Docker Desktop y vuelve a intentar."
	exit 1
fi

echo "Oracle"
docker compose up -d >/dev/null 2>&1
limite=$((SECONDS + 300))
until docker exec oursour-oracle healthcheck.sh >/dev/null 2>&1; do
	if [ $SECONDS -ge $limite ]; then
		echo "  ✗ Oracle no respondió. Revisa el contenedor en Docker Desktop."
		exit 1
	fi
	sleep 3
done
echo "  ✓ oursour-oracle  localhost:1521"

echo "Eureka"
levantar eureka-server 8761
until curl -s -o /dev/null localhost:8761/; do sleep 2; done
echo "  ✓ eureka-server  http://localhost:8761"

echo "Microservicios (la primera vez puede tardar unos minutos)"
for servicio in $SERVICIOS; do
	levantar "${servicio%%:*}" "${servicio##*:}"
done
errores=0
for servicio in $SERVICIOS; do
	esperar_salud "${servicio%%:*}" "${servicio##*:}" || errores=1
done

echo
if [ $errores -eq 0 ]; then
	echo "Todo arriba. Espera ~30 s a que el gateway vea los servicios y prueba:"
	echo "  http://localhost:8080/api/recetas"
else
	echo "Algún servicio falló. Revisa la carpeta logs/."
fi
echo "Logs: backend/logs/   ·   Para apagar: ./stop.sh"
