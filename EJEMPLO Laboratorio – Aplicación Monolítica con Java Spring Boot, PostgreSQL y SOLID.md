# LABORATORIO: APLICACIÓN MONOLÍTICA CON JAVA SPRING BOOT, POSTGRESQL Y PRINCIPIOS SOLID

## Landing Page + CRUD de Productos

---

# 1. Objetivo del laboratorio

Desarrollar desde cero una aplicación web monolítica utilizando:

- Java.
- Spring Boot.
- Spring MVC.
- Spring Data JPA.
- PostgreSQL.
- Thymeleaf.
- Bootstrap.
- Maven.
- Arquitectura por capas.
- Principios SOLID.

La aplicación tendrá:

1. Una Landing Page.
2. Un módulo de productos.
3. CRUD completo:
   - Crear.
   - Consultar.
   - Listar.
   - Actualizar.
   - Eliminar.
4. Persistencia en PostgreSQL.
5. Validaciones de negocio.

---

# 2. Caso

Una pequeña empresa necesita una aplicación web para administrar sus productos.

Cada producto tendrá:

| Campo | Tipo | Descripción |
|---|---|---|
| id | Long | Identificador |
| nombre | String | Nombre |
| descripcion | String | Descripción |
| precio | BigDecimal | Precio |
| stock | Integer | Cantidad disponible |

La aplicación mostrará primero una Landing Page.

Desde ella se podrá acceder a:

```text
Gestión de Productos
```

El módulo implementará:

```text
CREATE
READ
UPDATE
DELETE
```

---

# 3. Arquitectura

Utilizaremos una arquitectura monolítica organizada en capas:

```text
┌──────────────────────────────┐
│         Navegador            │
│      HTML + Bootstrap        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│         Controller           │
│         Spring MVC           │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│          Service             │
│     Reglas de negocio        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│        Repository            │
│      Spring Data JPA         │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│          Entity              │
│          Producto            │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│        PostgreSQL            │
└──────────────────────────────┘
```

Continúa siendo una:

```text
APLICACIÓN MONOLÍTICA
```

porque toda la aplicación se compila y despliega como una sola unidad.

---

# 4. Estructura final

```text
tienda-spring/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── pe/
│   │   │       └── edu/
│   │   │           └── empresa/
│   │   │               └── tienda/
│   │   │                   │
│   │   │                   ├── TiendaApplication.java
│   │   │                   │
│   │   │                   ├── controller/
│   │   │                   │   ├── HomeController.java
│   │   │                   │   └── ProductoController.java
│   │   │                   │
│   │   │                   ├── model/
│   │   │                   │   └── Producto.java
│   │   │                   │
│   │   │                   ├── repository/
│   │   │                   │   └── ProductoRepository.java
│   │   │                   │
│   │   │                   └── service/
│   │   │                       ├── ProductoService.java
│   │   │                       └── ProductoServiceImpl.java
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   └── css/
│   │       │       └── styles.css
│   │       │
│   │       ├── templates/
│   │       │   ├── index.html
│   │       │   └── productos/
│   │       │       ├── lista.html
│   │       │       ├── formulario.html
│   │       │       └── detalle.html
│   │       │
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

# 5. Requisitos previos

Instalar:

- JDK 17 o superior.
- Se recomienda Java 21.
- PostgreSQL.
- IntelliJ IDEA, Eclipse o Visual Studio Code.
- Navegador.

Verificar Java:

```bash
java -version
```

---

# 6. Verificar PostgreSQL

```bash
psql --version
```

---

# 7. Crear base de datos

Ingresar:

```bash
psql -U postgres
```

Crear:

```sql
CREATE DATABASE tienda_db;
```

Verificar:

```sql
\l
```

Salir:

```text
\q
```

---

# 8. Crear proyecto con Spring Initializr

Ingresar a:

```text
https://start.spring.io
```

Configurar:

```text
Project:
Maven

Language:
Java

Spring Boot:
versión estable

Group:
pe.edu.empresa

Artifact:
tienda

Name:
tienda

Description:
CRUD de productos con Spring Boot

Package name:
pe.edu.empresa.tienda

Packaging:
Jar

Java:
21
```

## Importante

El nuevo paquete base será:

```text
pe.edu.empresa.tienda
```



---

# 9. Dependencias

Agregar:

```text
Spring Web
Spring Data JPA
PostgreSQL Driver
Thymeleaf
Spring Boot DevTools
```

---

# 10. Descomprimir proyecto

Abrir:

```text
tienda/
```

en el IDE.

---

# 11. Dependencias principales

El `pom.xml` contendrá aproximadamente:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

# 12. Configurar PostgreSQL

Editar:

```text
src/main/resources/application.properties
```

Contenido:

```properties
spring.application.name=tienda

spring.datasource.url=jdbc:postgresql://localhost:5432/tienda_db
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true

spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

Cambiar la contraseña según la instalación local.

---

# 13. Crear paquetes

Dentro de:

```text
src/main/java/pe/edu/empresa/tienda
```

crear:

```text
controller
model
repository
service
```

Resultado:

```text
pe.edu.empresa.tienda
│
├── controller
├── model
├── repository
├── service
└── TiendaApplication.java
```

---

# 14. Crear Producto.java

Ubicación:

```text
src/main/java/pe/edu/empresa/tienda/model/Producto.java
```

Código:

```java
package pe.edu.empresa.tienda.model;

import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable = false,
        length = 100
    )
    private String nombre;

    @Column(
        length = 250
    )
    private String descripcion;

    @Column(
        nullable = false,
        precision = 10,
        scale = 2
    )
    private BigDecimal precio;

    @Column(
        nullable = false
    )
    private Integer stock;


    public Producto() {
    }


    public Producto(
            String nombre,
            String descripcion,
            BigDecimal precio,
            Integer stock
    ) {

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getDescripcion() {
        return descripcion;
    }


    public void setDescripcion(
            String descripcion
    ) {
        this.descripcion = descripcion;
    }


    public BigDecimal getPrecio() {
        return precio;
    }


    public void setPrecio(
            BigDecimal precio
    ) {
        this.precio = precio;
    }


    public Integer getStock() {
        return stock;
    }


    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
```

---

# 15. ProductoRepository

Ubicación:

```text
src/main/java/pe/edu/empresa/tienda/repository/ProductoRepository.java
```

Código:

```java
package pe.edu.empresa.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.tienda.model.Producto;


public interface ProductoRepository
        extends JpaRepository<Producto, Long> {

}
```

---

# 16. ProductoService

Ubicación:

```text
src/main/java/pe/edu/empresa/tienda/service/ProductoService.java
```

Código:

```java
package pe.edu.empresa.tienda.service;

import java.util.List;

import pe.edu.empresa.tienda.model.Producto;


public interface ProductoService {

    List<Producto> listarTodos();

    Producto obtenerPorId(Long id);

    Producto guardar(Producto producto);

    Producto actualizar(
        Long id,
        Producto producto
    );

    void eliminar(Long id);
}
```

---

# 17. ProductoServiceImpl

Ubicación:

```text
src/main/java/pe/edu/empresa/tienda/service/ProductoServiceImpl.java
```

Código:

```java
package pe.edu.empresa.tienda.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import pe.edu.empresa.tienda.model.Producto;
import pe.edu.empresa.tienda.repository.ProductoRepository;


@Service
public class ProductoServiceImpl
        implements ProductoService {

    private final ProductoRepository repository;


    public ProductoServiceImpl(
            ProductoRepository repository
    ) {
        this.repository = repository;
    }


    @Override
    public List<Producto> listarTodos() {

        return repository.findAll();
    }


    @Override
    public Producto obtenerPorId(Long id) {

        return repository
                .findById(id)
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        "Producto no encontrado"
                    )
                );
    }


    @Override
    public Producto guardar(
            Producto producto
    ) {

        validarProducto(producto);

        return repository.save(producto);
    }


    @Override
    public Producto actualizar(
            Long id,
            Producto datosProducto
    ) {

        Producto producto =
            obtenerPorId(id);

        validarProducto(datosProducto);

        producto.setNombre(
            datosProducto.getNombre()
        );

        producto.setDescripcion(
            datosProducto.getDescripcion()
        );

        producto.setPrecio(
            datosProducto.getPrecio()
        );

        producto.setStock(
            datosProducto.getStock()
        );

        return repository.save(producto);
    }


    @Override
    public void eliminar(Long id) {

        Producto producto =
            obtenerPorId(id);

        repository.delete(producto);
    }


    private void validarProducto(
            Producto producto
    ) {

        if (
            producto.getNombre() == null ||
            producto.getNombre().isBlank()
        ) {

            throw new IllegalArgumentException(
                "El nombre es obligatorio"
            );
        }


        if (
            producto.getPrecio() == null ||
            producto.getPrecio().compareTo(
                BigDecimal.ZERO
            ) <= 0
        ) {

            throw new IllegalArgumentException(
                "El precio debe ser mayor a cero"
            );
        }


        if (
            producto.getStock() == null ||
            producto.getStock() < 0
        ) {

            throw new IllegalArgumentException(
                "El stock no puede ser negativo"
            );
        }
    }
}
```

---

# 18. HomeController

Ubicación:

```text
src/main/java/pe/edu/empresa/tienda/controller/HomeController.java
```

Código:

```java
package pe.edu.empresa.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {


    @GetMapping("/")
    public String inicio() {

        return "index";
    }
}
```

---

# 19. ProductoController

Ubicación:

```text
src/main/java/pe/edu/empresa/tienda/controller/ProductoController.java
```

Código:

```java
package pe.edu.empresa.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import pe.edu.empresa.tienda.model.Producto;
import pe.edu.empresa.tienda.service.ProductoService;


@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;


    public ProductoController(
            ProductoService service
    ) {

        this.service = service;
    }


    @GetMapping
    public String listar(
            Model model
    ) {

        model.addAttribute(
            "productos",
            service.listarTodos()
        );

        return "productos/lista";
    }


    @GetMapping("/{id}")
    public String detalle(
            @PathVariable Long id,
            Model model
    ) {

        model.addAttribute(
            "producto",
            service.obtenerPorId(id)
        );

        return "productos/detalle";
    }


    @GetMapping("/nuevo")
    public String nuevo(
            Model model
    ) {

        model.addAttribute(
            "producto",
            new Producto()
        );

        return "productos/formulario";
    }


    @PostMapping
    public String guardar(
            @ModelAttribute Producto producto
    ) {

        service.guardar(producto);

        return "redirect:/productos";
    }


    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model
    ) {

        model.addAttribute(
            "producto",
            service.obtenerPorId(id)
        );

        return "productos/formulario";
    }


    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @ModelAttribute Producto producto
    ) {

        service.actualizar(
            id,
            producto
        );

        return "redirect:/productos";
    }


    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id
    ) {

        service.eliminar(id);

        return "redirect:/productos";
    }
}
```

---

# 20. TiendaApplication

Ubicación:

```text
src/main/java/pe/edu/empresa/tienda/TiendaApplication.java
```

Código:

```java
package pe.edu.empresa.tienda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TiendaApplication {


    public static void main(
            String[] args
    ) {

        SpringApplication.run(
            TiendaApplication.class,
            args
        );
    }
}
```

Es muy importante que:

```text
TiendaApplication.java
```

esté ubicado en:

```text
pe.edu.empresa.tienda
```

porque Spring Boot realizará automáticamente el escaneo de componentes en:

```text
pe.edu.empresa.tienda.controller
pe.edu.empresa.tienda.service
pe.edu.empresa.tienda.repository
pe.edu.empresa.tienda.model
```

---

# 21. Rutas

| Método | URL | Acción |
|---|---|---|
| GET | `/` | Landing Page |
| GET | `/productos` | Listar |
| GET | `/productos/nuevo` | Nuevo |
| POST | `/productos` | Crear |
| GET | `/productos/{id}` | Detalle |
| GET | `/productos/{id}/editar` | Editar |
| POST | `/productos/{id}` | Actualizar |
| POST | `/productos/{id}/eliminar` | Eliminar |

---

# 22. Landing Page

Crear:

```text
src/main/resources/templates/index.html
```

```html
<!DOCTYPE html>

<html
    lang="es"
    xmlns:th="http://www.thymeleaf.org"
>

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1"
    >

    <title>
        Spring Store
    </title>

    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >

    <link
        rel="stylesheet"
        th:href="@{/css/styles.css}"
    >

</head>

<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">

    <div class="container">

        <a
            class="navbar-brand"
            th:href="@{/}"
        >
            Spring Store
        </a>

        <a
            class="btn btn-outline-light"
            th:href="@{/productos}"
        >
            Productos
        </a>

    </div>

</nav>


<section class="hero">

    <div class="container text-center">

        <h1 class="display-3 fw-bold">
            Spring Store
        </h1>

        <p class="lead">
            Aplicación monolítica desarrollada
            con Java y Spring Boot.
        </p>

        <p>
            CRUD de productos utilizando
            PostgreSQL y principios SOLID.
        </p>

        <a
            class="btn btn-primary btn-lg"
            th:href="@{/productos}"
        >
            Administrar productos
        </a>

    </div>

</section>


<section class="container py-5">

    <div class="row g-4">

        <div class="col-md-4">

            <div class="card h-100 shadow-sm">

                <div class="card-body">

                    <h3>Java</h3>

                    <p>
                        Lenguaje orientado a objetos
                        utilizado en aplicaciones empresariales.
                    </p>

                </div>

            </div>

        </div>


        <div class="col-md-4">

            <div class="card h-100 shadow-sm">

                <div class="card-body">

                    <h3>Spring Boot</h3>

                    <p>
                        Framework para desarrollar
                        aplicaciones Java modernas.
                    </p>

                </div>

            </div>

        </div>


        <div class="col-md-4">

            <div class="card h-100 shadow-sm">

                <div class="card-body">

                    <h3>PostgreSQL</h3>

                    <p>
                        Motor de base de datos relacional
                        utilizado por la aplicación.
                    </p>

                </div>

            </div>

        </div>

    </div>

</section>

</body>

</html>
```

---

# 23. CSS

Crear:

```text
src/main/resources/static/css/styles.css
```

```css
body {
    background-color: #f8f9fa;
}

.hero {
    min-height: 500px;
    display: flex;
    align-items: center;
    background-color: #e9ecef;
}

.table {
    background-color: white;
}
```

---

# 24. Lista de productos

Crear:

```text
src/main/resources/templates/productos/lista.html
```

```html
<!DOCTYPE html>

<html
    lang="es"
    xmlns:th="http://www.thymeleaf.org"
>

<head>

    <meta charset="UTF-8">

    <title>Productos</title>

    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >

</head>

<body>

<nav class="navbar navbar-dark bg-dark">

    <div class="container">

        <a
            class="navbar-brand"
            th:href="@{/}"
        >
            Spring Store
        </a>

    </div>

</nav>


<div class="container py-5">

    <div
        class="d-flex
               justify-content-between
               align-items-center
               mb-4"
    >

        <h1>Productos</h1>

        <a
            class="btn btn-primary"
            th:href="@{/productos/nuevo}"
        >
            Nuevo producto
        </a>

    </div>


    <table class="table table-striped table-hover">

        <thead>

        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Precio</th>
            <th>Stock</th>
            <th>Acciones</th>
        </tr>

        </thead>

        <tbody>

        <tr th:each="producto : ${productos}">

            <td th:text="${producto.id}"></td>

            <td th:text="${producto.nombre}"></td>

            <td th:text="${'S/ ' + producto.precio}"></td>

            <td th:text="${producto.stock}"></td>

            <td>

                <a
                    class="btn btn-info btn-sm"
                    th:href="@{/productos/{id}(id=${producto.id})}"
                >
                    Ver
                </a>

                <a
                    class="btn btn-warning btn-sm"
                    th:href="@{/productos/{id}/editar(id=${producto.id})}"
                >
                    Editar
                </a>

                <form
                    th:action="@{/productos/{id}/eliminar(id=${producto.id})}"
                    method="post"
                    style="display:inline"
                >

                    <button
                        type="submit"
                        class="btn btn-danger btn-sm"
                        onclick="return confirm('¿Desea eliminar el producto?')"
                    >
                        Eliminar
                    </button>

                </form>

            </td>

        </tr>

        </tbody>

    </table>


    <div
        th:if="${#lists.isEmpty(productos)}"
        class="alert alert-info"
    >
        No existen productos registrados.
    </div>

</div>

</body>

</html>
```

---

# 25. Formulario

Crear:

```text
src/main/resources/templates/productos/formulario.html
```

```html
<!DOCTYPE html>

<html
    lang="es"
    xmlns:th="http://www.thymeleaf.org"
>

<head>

    <meta charset="UTF-8">

    <title>Producto</title>

    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >

</head>

<body>

<div class="container py-5">

    <h1
        th:text="${producto.id == null}
        ? 'Nuevo producto'
        : 'Editar producto'"
    ></h1>


    <form
        th:object="${producto}"
        th:action="${producto.id == null}
            ? @{/productos}
            : @{/productos/{id}(id=${producto.id})}"
        method="post"
    >

        <div class="mb-3">

            <label class="form-label">
                Nombre
            </label>

            <input
                type="text"
                class="form-control"
                th:field="*{nombre}"
                required
            >

        </div>


        <div class="mb-3">

            <label class="form-label">
                Descripción
            </label>

            <textarea
                class="form-control"
                th:field="*{descripcion}"
                rows="3"
            ></textarea>

        </div>


        <div class="mb-3">

            <label class="form-label">
                Precio
            </label>

            <input
                type="number"
                step="0.01"
                min="0.01"
                class="form-control"
                th:field="*{precio}"
                required
            >

        </div>


        <div class="mb-3">

            <label class="form-label">
                Stock
            </label>

            <input
                type="number"
                min="0"
                class="form-control"
                th:field="*{stock}"
                required
            >

        </div>


        <button
            type="submit"
            class="btn btn-success"
        >
            Guardar
        </button>

        <a
            class="btn btn-secondary"
            th:href="@{/productos}"
        >
            Cancelar
        </a>

    </form>

</div>

</body>

</html>
```

---

# 26. Detalle

Crear:

```text
src/main/resources/templates/productos/detalle.html
```

```html
<!DOCTYPE html>

<html
    lang="es"
    xmlns:th="http://www.thymeleaf.org"
>

<head>

    <meta charset="UTF-8">

    <title>
        Detalle del producto
    </title>

    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >

</head>

<body>

<div class="container py-5">

    <div class="card shadow">

        <div class="card-body">

            <h1 th:text="${producto.nombre}"></h1>

            <hr>

            <p>
                <strong>Código:</strong>
                <span th:text="${producto.id}"></span>
            </p>

            <p>
                <strong>Descripción:</strong>
                <span th:text="${producto.descripcion}"></span>
            </p>

            <p>
                <strong>Precio:</strong>
                S/
                <span th:text="${producto.precio}"></span>
            </p>

            <p>
                <strong>Stock:</strong>
                <span th:text="${producto.stock}"></span>
            </p>

            <a
                class="btn btn-secondary"
                th:href="@{/productos}"
            >
                Regresar
            </a>

            <a
                class="btn btn-warning"
                th:href="@{/productos/{id}/editar(id=${producto.id})}"
            >
                Editar
            </a>

        </div>

    </div>

</div>

</body>

</html>
```

---

# 27. Compilar

Windows:

```bash
mvnw.cmd clean package
```

Linux/macOS:

```bash
./mvnw clean package
```

---

# 28. Ejecutar

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Abrir:

```text
http://localhost:8080
```

---

# 29. Probar CRUD

## CREATE

Ingresar:

```text
Nombre:
Laptop Lenovo

Descripción:
Laptop Core i7 con 16 GB RAM

Precio:
3500.00

Stock:
10
```

---

## READ

Visitar:

```text
http://localhost:8080/productos
```

---

## UPDATE

Editar el producto y cambiar:

```text
Precio:
3200.00
```

---

## DELETE

Seleccionar:

```text
Eliminar
```

y confirmar.

---

# 30. Verificar PostgreSQL

```bash
psql -U postgres -d tienda_db
```

Ejecutar:

```sql
SELECT * FROM productos;
```

---

# 31. Principios SOLID

## S — Single Responsibility

```text
Producto
→ representar datos

ProductoRepository
→ persistencia

ProductoServiceImpl
→ lógica del negocio

ProductoController
→ peticiones HTTP

Thymeleaf
→ presentación
```

---

## O — Open/Closed

Podemos incorporar nuevos módulos:

```text
CategoriaController
CategoriaService
CategoriaRepository
```

sin alterar sustancialmente Productos.

---

## L — Liskov Substitution

Tenemos:

```java
public interface ProductoService
```

y:

```java
public class ProductoServiceImpl
        implements ProductoService
```

La implementación puede ser utilizada donde se espere la interfaz.

---

## I — Interface Segregation

`ProductoService` contiene únicamente operaciones relacionadas con productos.

```java
List<Producto> listarTodos();

Producto obtenerPorId(Long id);

Producto guardar(Producto producto);

Producto actualizar(
    Long id,
    Producto producto
);

void eliminar(Long id);
```

---

## D — Dependency Inversion

El Controller depende de:

```java
private final ProductoService service;
```

y no directamente de:

```java
ProductoServiceImpl
```

La dependencia se recibe mediante:

```java
public ProductoController(
        ProductoService service
) {

    this.service = service;
}
```

Spring realiza la inyección.

---

# 32. Arquitectura final

```text
             CLIENTE
                │
                ▼
       ┌─────────────────┐
       │     Browser     │
       └────────┬────────┘
                │
                ▼
       ┌─────────────────┐
       │   Controller    │
       └────────┬────────┘
                │
                ▼
         ProductoService
                ▲
                │
                │ implements
                │
       ProductoServiceImpl
                │
                ▼
       ┌─────────────────┐
       │   Repository    │
       └────────┬────────┘
                │
                ▼
       ┌─────────────────┐
       │ Spring Data JPA │
       └────────┬────────┘
                │
                ▼
       ┌─────────────────┐
       │    Hibernate    │
       └────────┬────────┘
                │
                ▼
       ┌─────────────────┐
       │   PostgreSQL    │
       └─────────────────┘
```

---

# 33. Ejercicios propuestos

## Ejercicio 1

Agregar:

```text
categoria
```

a Producto.

---

## Ejercicio 2

Agregar:

```text
fechaRegistro
```

usando:

```java
LocalDateTime
```

---

## Ejercicio 3

Agregar la regla:

```text
El stock máximo permitido será 1000.
```

La regla debe estar en:

```text
ProductoServiceImpl
```

---

## Ejercicio 4

Agregar:

```text
Productos sin stock
```

Investigar:

```java
findByStock(Integer stock)
```

---

## Ejercicio 5

Agregar búsqueda por nombre usando:

```java
findByNombreContainingIgnoreCase(
    String nombre
)
```

---

## Ejercicio 6

Ordenar productos por precio:

```java
findAllByOrderByPrecioAsc()
```

---

# 34. Evidencias

El alumno deberá presentar:

1. Landing Page funcionando.
2. Listado de productos.
3. Registro de un producto.
4. Actualización.
5. Eliminación.
6. Consulta PostgreSQL:

```sql
SELECT * FROM productos;
```

7. Captura de la estructura:

```text
pe.edu.empresa.tienda
│
├── controller
├── model
├── repository
└── service
```

---

# 35. Entregable

Entregar:

```text
Apellido_Nombre_Lab_SpringBoot.zip
```

No incluir:

```text
target/
```

El proyecto deberá poder ejecutarse con:

```bash
mvnw.cmd spring-boot:run
```

o:

```bash
./mvnw spring-boot:run
```

---

# 36. Estructura final verificada

```text
src/main/java/
└── pe/
    └── edu/
        └── empresa/
            └── tienda/
                │
                ├── TiendaApplication.java
                │
                ├── controller/
                │   ├── HomeController.java
                │   └── ProductoController.java
                │
                ├── model/
                │   └── Producto.java
                │
                ├── repository/
                │   └── ProductoRepository.java
                │
                └── service/
                    ├── ProductoService.java
                    └── ProductoServiceImpl.java
```

Todos los archivos Java deben comenzar con alguna de estas declaraciones:

```java
package pe.edu.empresa.tienda;
```

```java
package pe.edu.empresa.tienda.controller;
```

```java
package pe.edu.empresa.tienda.model;
```

```java
package pe.edu.empresa.tienda.repository;
```

```java
package pe.edu.empresa.tienda.service;
```

Los imports internos deben utilizar exclusivamente:

```java
import pe.edu.empresa.tienda...
```


La aplicación final mantiene el flujo:

```text
Landing Page
     ↓
Controller
     ↓
Service
     ↓
Repository
     ↓
Spring Data JPA
     ↓
Hibernate
     ↓
PostgreSQL
```

y constituye una aplicación **monolítica modular basada en principios SOLID**.