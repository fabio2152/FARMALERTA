# FARMALERTA

Aplicación web **monolítica** para farmacias, boticas y cadenas que controla cada lote de producto farmacéutico por su fecha de vencimiento, avisa con anticipación, recomienda con **Inteligencia Artificial** qué hacer con lo que está por vencer e **impide vender lo vencido**.

- Qué se construye: [especificacion-farmalerta.md](especificacion-farmalerta.md)
- Molde técnico: [EJEMPLO Laboratorio](EJEMPLO%20Laboratorio%20–%20Aplicación%20Monolítica%20con%20Java%20Spring%20Boot,%20PostgreSQL%20y%20SOLID.md)

## Tecnologías

Java 21 · Spring Boot 4 (Spring Web MVC, Spring Data JPA) · Thymeleaf · PostgreSQL · Bootstrap 5 (CDN) · Maven Wrapper · `spring-security-crypto` (solo BCrypt).

Arquitectura en capas, igual que el EJEMPLO:

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

| Usuario | Rol | Empresa / sucursal |
|---|---|---|
| `admin` | Administrador de la plataforma | — |
| `gerente1` | Gerente | Boticas Salud Total (todas sus sucursales) |
| `qf_miraflores` | Químico farmacéutico | Salud Total · Miraflores |
| `tecnico_miraflores` | Técnico en farmacia | Salud Total · Miraflores |
| `qf_sjl` | Químico farmacéutico | Salud Total · San Juan de Lurigancho |
| `tecnico_sjl` | Técnico en farmacia | Salud Total · San Juan de Lurigancho |
| `gerente_esperanza` | Gerente | Botica La Esperanza |
| `qf_esperanza` | Químico farmacéutico | Botica La Esperanza |
| `tecnico_esperanza` | Técnico en farmacia | Botica La Esperanza |

Los datos iniciales incluyen lotes en todos los niveles del semáforo (uno que vence hoy y uno vencido), 60 días de ventas históricas y un caso en el que la IA recomienda transferir Ibuprofeno de Miraflores a San Juan de Lurigancho. Las fechas se calculan a partir del día en que se cargan.

## 4. Módulos

| Módulo | Qué hace |
|---|---|
| Base | Login con `HttpSession`, contraseñas BCrypt, landing page, barra de navegación, datos iniciales. |
| Catálogo | Empresas y sucursales (admin), usuarios y configuración (gerente), productos y proveedores. |
| Inventario | Ingreso de lotes (mes/año → último día del mes, empaques → unidades, vida útil mínima), semáforo. |
| Ventas | FEFO, venta completa o nada, cobertura del tratamiento, descuento por nivel con piso de costo, venta del último día con confirmación del químico farmacéutico, ofertas públicas sin productos con receta. |
| Sanitario | Revisión diaria automática (00:05, Lima): bloquea vencidos, notifica y genera recomendaciones. Alertas sanitarias y bajas. |
| IA | Suavizado exponencial de la demanda, simulación FEFO de unidades en riesgo y recomendación TRANSFERIR → DEVOLVER → DESCUENTO → BAJA, con su justificación. |
| Movimientos | Devoluciones al proveedor (ventana de canje) y transferencias entre sucursales con reserva. |
| Reportes | Reporte mensual y acta de bajas descargable en CSV. |
| Demo | `/demo`: simular la fecha, restablecerla y ejecutar la revisión diaria. |

## 5. Guion de demostración

1. Landing page e inicio de sesión como `tecnico_miraflores`.
2. **Panel:** el semáforo muestra lotes en rojo y uno en último día (S-301).
3. **FEFO:** vender 10 *Paracetamol 500 mg*: sale del lote P-101 (ROJO) con 25 % de descuento.
4. **Cobertura:** vender *Amoxicilina 500 mg* para 7 días: se omite el lote A-201 (vence en 3 días) y se explica por qué.
5. **Último día:** vender *Sales de Rehidratación Oral*: la venta queda pendiente. Entrar como `qf_miraflores` → *Pendientes* → confirmar: aparece la advertencia "Debe usarse hoy".
6. **Bloqueo:** en `/demo`, avanzar 1 día y ejecutar la revisión diaria: S-301 queda BLOQUEADO y ya no se vende.
7. **IA:** en *IA*, la recomendación TRANSFERIR Ibuprofeno a San Juan de Lurigancho muestra sus números. Aceptarla crea la transferencia; recibirla como `tecnico_sjl`.
8. **Alerta sanitaria:** como `qf_miraflores`, registrar una alerta sobre el lote N-601 de *Insulina*: queda bloqueado en ambas sucursales.
9. **Cierre:** *Gestión → Reportes*: reporte del mes y descarga del acta de bajas.

## 6. Verificación

El proyecto **no usa TDD** ni pruebas automatizadas: se verifica ejecutando en la aplicación los casos V01–V63 de la sección 16 de la especificación. El módulo opcional de cadena de frío no está incluido.

## 7. Decisión de diseño fuera de la especificación

`Salida` guarda además `producto` y `cantidadSolicitada`: una venta del último día queda PENDIENTE_CONFIRMACION sin movimientos de stock, y al confirmarla hay que volver a armar el plan FEFO con esos datos.
