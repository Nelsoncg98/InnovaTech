# 🏛️ InnovaTech SOA - Sistema Omnicanal E-Commerce

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![MongoDB](https://img.shields.io/badge/MongoDB-6.0-green)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

Repositorio oficial para el desarrollo e implementación del **Avance de Proyecto Final 3 (APF3)** del curso Arquitectura Orientada al Servicio.

## 📋 Descripción del Proyecto
InnovaTech es una plataforma de comercio electrónico omnicanal construida bajo los principios estrictos de **Arquitectura Orientada a Servicios (SOA)**. El sistema erradica los silos de datos tradicionales integrando las operaciones de tiendas físicas (POS) y canales digitales (Web) mediante microservicios independientes, orquestación síncrona/asíncrona y un API Gateway centralizado.

## 🏗️ Topología de la Arquitectura
El sistema implementa el patrón **Database-per-Service** para garantizar el aislamiento de dominios, soportando consistencia eventual mediante eventos.

### Capa de Dominio (Entity Services)
*   `servicio-catalogo` (MongoDB): Gestión de productos y precios.
*   `servicio-inventario` (PostgreSQL): Control de Kardex con segregación lógica de bodegas (Web/Física).
*   `servicio-clientes` (PostgreSQL): Registro y validación de usuarios.
*   `servicio-proveedores` (Port 8088): Validación de Guías de Remisión y Políticas de Garantía B2B.

### Capa de Orquestación (Task Services / BFF)
*   `servicio-ventapos` (Port 8082): Orquestador de transacciones físicas, maneja clientes, inventario y pasarelas de pago (Patrón SAGA).
*   `servicio-ventaweb` (Port 8087): Orquestador del E-commerce, coordina inventario, pagos y despachos (Patrón SAGA).
*   `api-gateway` (Port 8080): Punto de entrada único y enrutamiento central.
*   `eureka-server` (Port 8761): Service Discovery (Registro de microservicios).

### Capa de Utilitarios (Utility Services)
*   `servicio-pagos` (Port 8085): Proxy abstracto hacia la pasarela de Visa/Niubiz.
*   `servicio-transporte` (Port 8089): Generador de tracking de última milla (Olva Courier).
*   `servicio-notificaciones` (Port 8090): Motor asíncrono de alertas (Email, SMS).

### Capa de Integración & Eventos
*   `servicio-erp-sap` (Port 8086): Emulador del ERP central que publica ingresos de mercadería.
*   **Apache Kafka**: Bus de mensajería (Event-Driven) para sincronización asíncrona de inventario.

### Capa de Presentación (Frontends)
*   `frontend-web`: E-commerce en React + Vite.
*   `frontend-erp`: Interfaz de ingresos al almacén (React + Vite).
*   `frontend-pos`: Prototipo de caja (React) - *Próximamente en C# .NET + WSO2*.

## 🚀 Guía de Despliegue Local (Desarrollo)

### 1. Requisitos Previos
*   Docker y Docker Compose instalados.
*   Java 17 (JDK) y Maven instalados.

### 2. Levantar Infraestructura Base
Ejecutar el siguiente comando en la raíz del proyecto para inicializar las bases de datos (PostgreSQL y MongoDB):
```bash
docker-compose up -d
```
Verificar que los puertos `5432` y `27017` estén activos.

### 3. Ejecución de Microservicios
Cada microservicio es un proyecto Spring Boot independiente. Se debe acceder a cada directorio y ejecutar:
```bash
mvn spring-boot:run
```
La documentación interactiva de la API estará disponible automáticamente en la ruta `/swagger-ui.html` de cada servicio activo.
