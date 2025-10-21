# Proyecto Base Implementando Clean Architecture


## Antes de Iniciar

Se utilizó el pluggin de clean architecture que se encuentra en un repositorio publico de github.

Empezaremos por explicar los diferentes componentes del proyecto y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.


# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

- Métodos:
    - `Object getProductById(String id)` — devuelve el detalle de un producto (actualmente `Object`).
    - `List<ReviewsModel> getRecomendationByProductId(String id)` — lista de reseñas por producto.
    - `Map<String, Object> getAllProducts(String title, Double minPrice, Double maxPrice, String category, int page, int size)` — búsqueda y paginación.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

- Carga datos desde recursos (`data/items.json` y `data/reviews.csv`).
- Mantiene caches en memoria (`items`, `reviews`) y usa `synchronized` en `loadProducts()` para inicialización segura.
- Lógica de filtrado:
    - Filtra por `title` (contains, case-insensitive), `category` (equalsIgnoreCase), `minPrice`, `maxPrice`.
    - Combina predicados y aplica sobre la lista completa.
- Paginación:
    - Valida `size > 0` (lanza `IllegalArgumentException` si no).
    - Calcula `totalItems`, `totalPages`, y sublista `data` según `page` y `size`.
    - Si `page` < 1, se normaliza a 1; si `page` > `totalPages` lanza `IllegalArgumentException`.
- Búsqueda por id / recomendaciones:
    - `getProductById` devuelve `Map` con `item` y `best_recommendation` (mejor reseña por rating) o `null` si no existe.
    - `getRecomendationByProductId` devuelve lista de reseñas filtradas por `item_id`.
- Manejo de errores: envuelve IO en `UncheckedIOException` para propagar fallos de lectura.

## Infrastructure


### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

- Endpoints:
    - `GET /products/all-products` — recibe filtros (`title`, `minPrice`, `maxPrice`, `category`) y paginación (`page`, `size`). Llama a `getAllProducts` y responde con `ResponseUtil`.
    - `GET /products/product-by-id?id={id}` — busca un producto por id y responde 200/404.
    - `GET /products/recommendation-by-product-id?id={id}` — devuelve reseñas del producto o 404 si no hay.
- Manejo de excepciones:
    - Captura `IllegalArgumentException` y responde 400 con el mensaje.
    - Captura `Exception` y responde 500.
- Dependencia inyectada: `ProductsDataUseCaseInterface`.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función �public static void main(String[] args)�.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**

## Datos y Formatos

- Recursos en classpath:
    - `data/items.json` — cargado con `ObjectMapper` a `List<ItemModel>` y convertido a `Map` por `id`.
    - `data/reviews.csv` — leído manualmente con `BufferedReader` y `String.split(",")`.