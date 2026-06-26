# 📊 Matriz de Asignación de Tareas - Hito APF3
**Proyecto:** InnovaTech SOA  
**Sprint:** Avance de Proyecto Final 3 (Implementación al 75%)

Este documento formaliza la división de responsabilidades del equipo para cubrir el 100% de la rúbrica exigida en la entrega del APF3. La ejecución se realizará en paralelo para asegurar el cumplimiento de los tiempos.

---

## 🎯 Objetivo de la Fase
Transformar el diseño arquitectónico (APF2) en código funcional. El entregable consta de los microservicios core desplegados localmente (Docker), asegurando la integración con las bases de datos requeridas (SQL y NoSQL) y superando las pruebas de aseguramiento de calidad (QA).

---

## 👨‍💻 Asignación de Roles y Desarrollo

### Asignación A: Módulo de Catálogo y Aseguramiento de Calidad
**Responsable:** Luis  
**Puntos Rúbrica Cubiertos:** Modelo NoSQL (1 pt) + Pruebas Unitarias (2 pts).

**Responsabilidades Técnicas:**
1.  **Desarrollo del `servicio-catalogo`:**
    *   Stack: Spring Boot 3, Java 17.
    *   Base de Datos: **MongoDB** (Implementar el patrón Database-per-Service requerido por la rúbrica).
    *   Contrato: Exponer API REST para consulta y listado de productos (Formato JSON).
2.  **Ingeniería de Pruebas (TDD):**
    *   Implementar pruebas unitarias utilizando **JUnit 5** y **Mockito**.
    *   Validar la lógica de los controladores y repositorios aislando la base de datos (mocks).

---

### Asignación B: Módulo Transaccional, Orquestación e Infraestructura
**Responsable:** Nelson  
**Puntos Rúbrica Cubiertos:** Programación Clientes/Consumos (8 pts) + Pruebas de Integración (2 pts).

**Responsabilidades Técnicas:**
1.  **Ingeniería DevOps (Docker):**
    *   Creación y mantenimiento del `docker-compose.yml` para levantar PostgreSQL y MongoDB.
2.  **Desarrollo del `servicio-inventario`:**
    *   Implementar la segregación de bodegas físicas y lógicas sobre **PostgreSQL**.
3.  **Desarrollo del Orquestador (`servicio-ventapos`):**
    *   Implementar la lógica de agregación (BFF) consumiendo inventario y pasarelas externas.
    *   Manejar el Patrón SAGA para compensación (Rollback) de inventario.
4.  **Pruebas de Integración End-to-End:**
    *   Desarrollar la colección de **Postman** que certifique el flujo omnicanal completo a través del API Gateway.

---

## 📌 Siguientes Pasos
1. Cada miembro clonará el repositorio oficial base que ya contiene la inicialización de Maven y Docker.
2. Todo el desarrollo debe respetar el Modelo Canónico JSON acordado en el documento de Arquitectura APF2.
3. Fecha límite de integración y pruebas conjuntas: *(Definir internamente)*.
