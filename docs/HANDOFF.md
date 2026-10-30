# Guía de Entrega y Documentación para el Desarrollo de UI - MovieExplorer

Esta documentación resume la arquitectura, la estructura de la UI, los ViewModels, el flujo de datos y la navegación del proyecto **MovieExplorer**.

---

## 1. Archivos que NO deben modificarse
Por favor, **no modifiques** los siguientes archivos ni paquetes salvo coordinación explícita:
- `gradle/libs.versions.toml`, `build.gradle.kts` (raíz y app).
- `AndroidManifest.xml`.
- Paquetes de arquitectura base: `di/`, `data/`, `domain/`.

---

## 2. Requisito de Configuración Local
Asegúrate de contar con tu propia clave de API de TMDB configurada en tu archivo `local.properties`:
```properties
TMDB_API_KEY=tu_api_key_aqui
```
El archivo `local.properties` está ignorado en Git para proteger la clave.

---

## 3. Navegación (`ui/navigation/Routes.kt`)
La navegación se gestiona centralizadamente mediante el objeto `Routes`:
- `Routes.LIST` (`"list"`): Pantalla principal de catálogo de películas (`MovieListScreen`).
- `Routes.DETAIL` (`"detail/{movieId}"`): Pantalla de detalle de película (`MovieDetailScreen`).
- `Routes.detail(movieId: Int)`: Función auxiliar para construir la ruta hacia el detalle de una película específica.

---

## 4. Estructura de la UI

### Pantallas Principales
- **`MovieListScreen`** (`ui/list/MovieListScreen.kt`):
  Pantalla principal que muestra el catálogo paginado de películas populares, búsqueda por título y filtrado por género. Incluye la barra superior `TopAppBar`, el banner de estado offline `OfflineBanner`, el buscador `SearchBar`, los chips de género `GenreChips`, y el manejo de estados de paginación con `MovieCard`, `LoadingFooter`, `ErrorState` y `EmptyState`.
- **`MovieDetailScreen`** (`ui/detail/MovieDetailScreen.kt`):
  Pantalla de detalle que muestra la imagen de fondo (backdrop) con degradado, botón único de retorno con insets de barra de estado, póster superpuesto, título, frase emblemática (tagline), fila envolvente de información (calificación, recuento de votos, duración y año), chips de géneros y tarjeta con la sinopsis desplegable ("Leer más" / "Leer menos").

### Componentes Reutilizables (`ui/components/`)
- **`MovieCard`**: Tarjeta horizontal para cada película en la lista con póster, título, año, sinopsis e insignia de calificación.
- **`OfflineBanner`**: Banner animado con advertencia de falta de conexión a internet.
- **`SearchBar`**: Campo de búsqueda redondeado con ícono de lupa y botón para borrar texto.
- **`GenreChips`**: Fila desplazable de chips para filtrado por género con la opción "Todos".
- **`ErrorState`**: Vista de error centrada con ícono en círculo oscuro, título, mensaje y botón "Reintentar".
- **`EmptyState`**: Vista centrada cuando no se encuentran resultados.
- **`LoadingFooter`**: Indicador de carga paginada al final de la lista con texto informativo.
- **`Formatters.kt`**: Funciones auxiliares para formatear duración (`formatRuntime`), recuento de votos corto (`formatVotes`) y año (`extractYear`).

---

## 5. ViewModels y Estado de la UI

### A. `MovieListViewModel` (`com.movieexplorer.ui.list.MovieListViewModel`)
Inyectado automáticamente con Hilt via `@HiltViewModel`.

#### Estados expuestos:
1. `isOnline: StateFlow<Boolean>`
   - Estado de conectividad en tiempo real (`true` online, `false` offline).
2. `query: StateFlow<String>`
   - Texto actual del campo de búsqueda.
3. `selectedGenre: StateFlow<Genre?>`
   - Género seleccionado actualmente (`null` si no hay género seleccionado).
4. `genres: StateFlow<List<Genre>>`
   - Lista de géneros disponibles obtenida de la API en el `init`.
5. `genresError: StateFlow<String?>`
   - Mensaje de error amigable en español si falla la carga inicial de géneros (o `null` si no hay error).
6. `movies: Flow<PagingData<Movie>>`
   - Flujo paginado de películas listo para consumirse con `collectAsLazyPagingItems()`.

#### Comportamiento Búsqueda vs Género (Excluyentes):
- Si el usuario escribe en la búsqueda (`onQueryChange(text)`), la query se actualiza y el género seleccionado se limpia a `null`. Aplica un *debounce* automático de 400 ms.
- Si el usuario selecciona un género (`onGenreSelected(genre)`), la búsqueda se borra a `""` y se filtra por ese género. Seleccionar el mismo género de nuevo lo deselecciona.
- Si ambos están vacíos/nulos, el flujo retorna automáticamente las películas populares.

---

### B. `MovieDetailViewModel` (`com.movieexplorer.ui.detail.MovieDetailViewModel`)
Inyectado automáticamente con Hilt via `@HiltViewModel`. Lee automáticamente el parámetro `"movieId"` desde el `SavedStateHandle`.

#### Estado expuesto:
- `uiState: StateFlow<DetailUiState>`
  - `DetailUiState.Loading`: La información del detalle se está cargando.
  - `DetailUiState.Success(val detail: MovieDetail)`: Detalle cargado con éxito.
  - `DetailUiState.Error(val message: String)`: Mensaje de error formateado en español.

---

## 6. Mapeo de Mensajes de Error (`util/ErrorMapper.kt`)
Cualquier error devuelto por la API o el repositorio se mapea a mensajes claros para el usuario final en español mediante `toUserMessage()`:
- Sin red -> *"Sin conexión a internet. Revisa tu red e inténtalo de nuevo."*
- Error 401 -> *"Error de autenticación con la API."*
- Error 404 -> *"No se encontró la información solicitada."*
- Otro error HTTP -> *"Error del servidor (código X)."*
- Otro error -> *"Ocurrió un error inesperado."*
