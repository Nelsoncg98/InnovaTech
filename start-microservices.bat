@echo off
echo ========================================================
echo     🚀 InnovaTech SOA - Iniciador Escalonado 🚀
echo ========================================================
echo Evitando sobrecarga de CPU... Iniciando en progresion.
echo.

echo [1/8] Levantando Eureka Server (Registro)...
start "Eureka Server [8761]" cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 15 /nobreak

echo [2/8] Levantando API Gateway (Enrutador)...
start "API Gateway [8080]" cmd /k "cd api-gateway && mvn spring-boot:run"
timeout /t 10 /nobreak

echo [3/8] Levantando Servicio Inventario (Dueño de Kardex)...
start "Servicio Inventario [8081]" cmd /k "cd servicio-inventario && mvn spring-boot:run"
timeout /t 10 /nobreak

echo [4/8] Levantando Servicio Clientes (Validacion DNI)...
start "Servicio Clientes [8083]" cmd /k "cd servicio-clientes && mvn spring-boot:run"
timeout /t 10 /nobreak

echo [5/8] Levantando Servicio Pagos (Proxy Niubiz)...
start "Servicio Pagos [8085]" cmd /k "cd servicio-pagos && mvn spring-boot:run"
timeout /t 10 /nobreak

echo [6/8] Levantando Orquestadores SAGA (Venta POS y Web)...
start "Venta POS [8082]" cmd /k "cd servicio-ventapos && mvn spring-boot:run"
timeout /t 5 /nobreak
start "Venta Web [8087]" cmd /k "cd servicio-ventaweb && mvn spring-boot:run"
timeout /t 10 /nobreak

echo [7/8] Levantando Utilitarios Logísticos (Proveedores, Transporte)...
start "Proveedores [8088]" cmd /k "cd servicio-proveedores && mvn spring-boot:run"
timeout /t 5 /nobreak
start "Transporte [8089]" cmd /k "cd servicio-transporte && mvn spring-boot:run"
timeout /t 5 /nobreak

echo [8/8] Levantando Utilitarios Asíncronos (Notificaciones, ERP)...
start "Notificaciones [8090]" cmd /k "cd servicio-notificaciones && mvn spring-boot:run"
timeout /t 5 /nobreak
start "ERP SAP [8086]" cmd /k "cd servicio-erp-sap && mvn spring-boot:run"

echo.
echo ========================================================
echo ✅ Secuencia Finalizada. 
echo ⚠️ Revisa las ventanas negras para verificar que no haya errores de compilacion.
echo 🐳 No olvides que Docker (WSO2, Kafka, BDs) debe estar corriendo.
echo ========================================================
pause
