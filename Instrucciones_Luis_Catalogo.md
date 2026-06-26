# 📄 Contexto de Desarrollo: Servicio Catálogo (Para: Luis)

¡Hola Luis! Nelson y Atlas aquí. Hemos dejado lista la arquitectura base del proyecto y el entorno Docker para el APF3. 
Tu misión es desarrollar el `servicio-catalogo`. Es una tarea 100% aislada, así que no dependes de nosotros para avanzar.

## 🎯 Tu objetivo en la rúbrica (Puntos APF3)
Con este servicio vas a asegurar **3 puntos críticos** de nuestra rúbrica:
1. **Base de Datos No Relacional (1 pt):** Tu servicio es el único que usará **MongoDB**. (Ya configuramos las dependencias en tu `pom.xml` y la conexión en tu `application.yml`).
2. **Pruebas Unitarias (2 pts):** Debes escribir los tests de tus controladores y repositorios usando `JUnit 5` y `Mockito`.

## 🛠️ Entorno de Trabajo
1. Ya tienes la carpeta `servicio-catalogo` con el `pom.xml` listo (Java 17, Spring Boot 3.2.4, MongoDB, Lombok y Swagger).
2. Para levantar la base de datos localmente, solo tienes que ejecutar `docker-compose up -d` en la carpeta raíz del proyecto. Eso te levantará MongoDB en el puerto `27017` con usuario `admin` y contraseña `secretpassword`.
3. Tu microservicio correrá en el puerto `8084`. Cuando lo corras, la documentación Swagger se generará sola en: `http://localhost:8084/swagger-ui.html`.

## 📜 Contrato a Implementar (API-First)
Tu controlador (`CatalogoController.java`) debe exponer los siguientes endpoints exactos para que nuestro orquestador pueda consumirlos sin fallar:

*   **GET `/api/v1/catalogo/productos`**: Lista todos los productos.
*   **GET `/api/v1/catalogo/productos/{codigoArticulo}`**: Busca un producto específico (Ej: `SKU-LAP-001`).

## 📦 Modelo de Datos Canónico (Entity)
Tu clase Java (ej. `Producto.java`) anotada con `@Document(collection = "productos")` debe respetar esta estructura JSON exacta. No uses Foreign Keys, todo va anidado.

```json
{
  "codigoArticulo": "SKU-LAP-001",
  "categoria": "COMPUTO",
  "detalles": {
    "marca": "Asus",
    "descripcion": "Laptop Gamer 15.6 pulgadas",
    "especificacionesTecnicas": {
      "ram": "16GB",
      "procesador": "Intel Core i7"
    }
  },
  "precios": {
    "precioBase": 3500.00,
    "moneda": "PEN"
  },
  "imagenesUrl": ["https://cdn.innovatech.com/img/lap001_1.jpg"]
}
```

¡Éxitos con el código! Si cambias alguna ruta del contrato, avísale a Nelson para que él actualice el API Gateway y el Orquestador.
