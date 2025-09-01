# Proyecto Cloud Native II [Grupo 3]

Este proyecto gestiona un inventario de productos utilizando una arquitectura de funciones servrless
con Azure Functions y un microservicio de Backend-for-Frontned que los convoca vía request HTTP.
Las Azure Functions son la conexión con la base de datos Postgres SQL para la operaciones _CRUD_
sobre el modelo de datos. La conexión con la BBDD fue realizada utilizando JDBC en cada Azure
Function.

### Integrantes:

- Hugo Ramos
- Sebastián Kravetz

## Descripción General

La arquitectura se compone de dos partes principales:

1. **API Principal (Backend for Frontend - BFF)**: Es una aplicación hecha en **Spring Boot** que
   funciona como la única puerta de entrada para cualquier aplicación cliente (por ejemplo, una
   página web). Su trabajo es recibir peticiones y llamar a los microservicios correspondientes.
   Esta aplicación esta instalada en una **Azure Virtual Machine**, utilizando una imagen Docker
   y un script Docker Compose.

2. **Funciones Serverless (Azure Functions)**: Son pequeñas aplicaciones independientes, cada una
   con una
   única tarea (crear, leer, actualizar o borrar productos). Están construidas con **Java** y se
   ejecutan en la plataforma _serverless_ de **Azure Functions**. Estas funciones son el canal
   de comunicación con la Base de Datos.
3. **Base de Datos (Postgres SQL)**: Base de datos con tablas relacionales para las entidades de
   negocio.

El flujo es simple: un cliente habla con el API Principal (BFF), y el BFF habla con las funciones
para completar la tarea, las funciones ejecutan sentencias SQL sobre la BBDD.

---

## Endpoints del BFF

A continuación se describen los endpoints expuestos tanto por el BFF como por las Azure Functions.

### 1. API Principal (BFF)

Estos son los endpoints que una aplicación cliente debe consumir.

- `GET /api/v1/inventory/warehouses`
    - **Descripción**: Obtiene la lista completa de bodegas y su cantidad de productos.

- `GET /api/v1/inventory/products`
    - **Descripción**: Obtiene la lista completa de productos y sus bodegas asociadas.

- `GET /api/v1/inventory/products/{id}`
    - **Descripción**: Obtiene un único producto según su `id`.

- `POST /api/v1/inventory/products/add`
    - **Descripción**: Agrega un producto nuevo al inventario.

- `PATCH /api/v1/inventory/products/update-price/{id}`
    - **Descripción**: Actualiza el precio de un producto existente.

- `DELETE /api/v1/inventory/products/{id}`
    - **Descripción**: Elimina un producto del inventario usando su `id`.

### 2. Azure Functions

Estas funciones son convocadas por el BFF. Estas funciones proveen de una abstracción para
hablar con la Base de Datos (Postgres DB)

- **CN2-G3-CRUDFN-1:**
    - `GET /api/get-products`
        - **Descripción**: Devuelve la lista de todos los productos desde la base de datos.
        - **Proyecto**: `cn2-g3-crudfn-1`

- **CN2-G3-CRUDFN-2:**
    - `POST /api/update-product?action={add|update}`
        - **Descripción**: Realiza dos posibles acciones:
            - `?action=add`: Crea un nuevo producto.
            - `?action=update`: Actualiza el precio de un producto existente.
        - **Proyecto**: `cn2-g3-crudfn-2`

- **CN2-G3-CRUDFN-3:**
    - `DELETE /api/delete-product/{productId}`
        - **Descripción**: Elimina un producto específico según su `productId`.
        - **Proyecto**: `cn2-g3-crudfn-3`

---

## Estructura de la Base de Datos

La base de datos relacional se compone de las siguientes tablas:

### Tabla: `categoria`

Almacena las categorías a las que puede pertenecer un producto.

| Columna  | Descripción                                            |
|----------|--------------------------------------------------------|
| `id`     | Identificador único (UUID) de la categoría.            |
| `nombre` | Nombre de la categoría (ej. "Lácteos", "Electrónica"). |

### Tabla: `producto`

Contiene la información de los productos individuales del inventario.

| Columna        | Descripción                                                   |
|----------------|---------------------------------------------------------------|
| `id`           | Identificador único (UUID) del producto.                      |
| `precio`       | Precio del producto.                                          |
| `nombre`       | Nombre comercial del producto.                                |
| `marca`        | Marca del producto.                                           |
| `categoria_id` | Llave foránea que referencia al `id` en la tabla `categoria`. |

### Tabla: `bodega`

Guarda la información de las diferentes bodegas o almacenes físicos.

| Columna     | Descripción                              |
|-------------|------------------------------------------|
| `id`        | Identificador único (UUID) de la bodega. |
| `nombre`    | Nombre de la bodega.                     |
| `direccion` | Dirección de la bodega.                  |
| `ciudad`    | Ciudad donde se encuentra la bodega.     |

### Tabla: `producto_bodega`

Tabla intermedia para crear una relación "muchos a muchos" entre productos y bodegas. Permite saber
qué productos hay en cada bodega.

| Columna       | Descripción                                                  |
|---------------|--------------------------------------------------------------|
| `producto_id` | Llave foránea que referencia al `id` en la tabla `producto`. |
| `bodega_id`   | Llave foránea que referencia al `id` en la tabla `bodega`.   |
