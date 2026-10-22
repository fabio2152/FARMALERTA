# FARMALERTA

Aplicación web para farmacias, boticas y cadenas que controla cada lote de producto farmacéutico por su fecha de vencimiento, avisa con anticipación e **impide vender lo vencido**.

## Tecnologías

Java 21 · Spring Boot 4 (Spring Web MVC, Spring Data JPA) · Thymeleaf · PostgreSQL · Bootstrap 5 (CDN) · Maven Wrapper · `spring-security-crypto` (solo BCrypt).

Arquitectura en capas:

```text
Navegador → Controller → Service (interfaz + Impl) → Repository → Entity → PostgreSQL
```

Paquete base `pe.edu.empresa.farmalerta` con `controller`, `model`, `repository` y `service`.

## 1. Crear la base de datos

```bash
psql -U postgres
```

```sql
CREATE DATABASE farmalerta_db;
```

La conexión está en `src/main/resources/application.properties` (usuario `postgres`, contraseña `postgres`). Si tu contraseña es otra, cámbiala ahí o pásala al ejecutar con la variable de entorno `SPRING_DATASOURCE_PASSWORD`.

Las tablas se crean solas (`spring.jpa.hibernate.ddl-auto=update`) y, si la base está vacía, la clase `DatosIniciales` carga los datos de demostración.

## 2. Ejecutar

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Abrir <http://localhost:8080>.

## 3. Cuentas de prueba

Todas las contraseñas son **`clave123`**.

| Usuario | Rol |
|---|---|
| `admin` | Administrador de la plataforma |
| `gerente` | Gerente |
| `qf` | Químico farmacéutico |
| `tecnico` | Técnico en farmacia |

Los datos iniciales incluyen lotes en todos los niveles del semáforo, entre ellos uno que vence hoy y uno vencido. Las fechas se calculan a partir del día en que se cargan.

## 4. Módulos

| Módulo | Qué hace |
|---|---|
| Base | Login con `HttpSession`, contraseñas BCrypt, landing page, barra de navegación, datos iniciales. |
| Catálogo | Empresas y sucursales (admin), usuarios y configuración (gerente), productos y proveedores. |
| Inventario | Ingreso de lotes (mes/año → último día del mes, empaques → unidades, vida útil mínima), semáforo. |
| Ventas | FEFO, venta completa o nada, cobertura del tratamiento, descuento por nivel con piso de costo, venta del último día con confirmación del químico farmacéutico, ofertas públicas sin productos con receta. |
| Sanitario | Revisión diaria automática (00:05, Lima): bloquea vencidos y notifica. Alertas sanitarias y bajas. |
| Movimientos | Devoluciones al proveedor (ventana de canje) y transferencias entre sucursales con reserva. |
| Reportes | Reporte mensual y acta de bajas descargable en CSV. |
| Demo | `/demo`: simular la fecha, restablecerla y ejecutar la revisión diaria. |
