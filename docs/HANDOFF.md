# Guía de Entrega y Documentación para el Desarrollo de UI - MovieExplorer

¡Hola! Esta documentación resume la arquitectura, los ViewModels, el flujo de datos y la navegación del proyecto **MovieExplorer** para que puedas implementar la interfaz gráfica final de manera fluida y sin contratiempos.

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
- `Routes.LIST` (`"list"`): Pantalla principal de catálogo de películas.
- `Routes.DETAIL` (`"detail/{movieId}"`): Pantalla de detalle.
- `Routes.detail(movieId: Int)`: Función auxiliar para construir la ruta hacia el detalle de una película específica.

---

## 4. ViewModels y Estado de la UI

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

#### Métodos de Interacción:
- `onQueryChange(text: String)`: Llama a esta función al cambiar el texto del buscador.
- `onGenreSelected(genre: Genre?)`: Llama a esta función al presionar un *chip* de género.
- `retryGenres()`: Reintenta cargar la lista de géneros si ocurrió un error en `genresError`.

#### Manejo del Estado Offline en Paging 3:
Cuando `isOnline` cambie de `false` a `true`, puedes reintentar automáticamente la paginación llamando a `lazyPagingItems.retry()` en la UI.

---

### B. `MovieDetailViewModel` (`com.movieexplorer.ui.detail.MovieDetailViewModel`)
Inyectado automáticamente con Hilt via `@HiltViewModel`. Lee automáticamente el parámetro `"movieId"` desde el `SavedStateHandle`.

#### Estado expuesto:
- `uiState: StateFlow<DetailUiState>`
  - `DetailUiState.Loading`: La información del detalle se está cargando.
  - `DetailUiState.Success(val detail: MovieDetail)`: Detalle cargado con éxito.
  - `DetailUiState.Error(val message: String)`: Mensaje de error formateado en español.

#### Métodos de Interacción:
- `retry()`: Reintenta la carga del detalle de la película en caso de fallo.

---

## 5. Mapeo de Mensajes de Error (`util/ErrorMapper.kt`)
Cualquier error devuelto por la API o el repositorio se mapea a mensajes claros para el usuario final en español mediante `toUserMessage()`:
- Sin red -> *"Sin conexión a internet. Revisa tu red e inténtalo de nuevo."*
- Error 401 -> *"Error de autenticación con la API."*
- Error 404 -> *"No se encontró la información solicitada."*
- Otro error HTTP -> *"Error del servidor (código X)."*
- Otro error -> *"Ocurrió un error inesperado."*

---

## 6. Pantallas de Prueba Temporales
Para validar el funcionamiento, se incluyeron dos composables marcados con `// TEMPORAL: será reemplazada por la UI final`:
- `DebugListScreen.kt`
- `DebugDetailScreen.kt`

Puedes reemplazarlos por los composables definitivos respetando la estructura de ViewModels expuesta.

---

## 7. Recomendaciones para la UI
- **Claves duplicadas en Paging**: TMDB puede devolver la misma película en páginas distintas. En el `LazyColumn`, la `key` debe combinar el `id` con el índice (por ejemplo: `key = { index -> val movie = moviesPagingItems.peek(index); if (movie != null) "${movie.id}_$index" else index }`, tal como en `DebugListScreen`) para evitar crash por claves duplicadas.
- **Ciclo de vida en Compose**: Usar `collectAsStateWithLifecycle()` en lugar de `collectAsState()` para observar los StateFlows respetando el ciclo de vida de los Composable.
- **Manejo de Lista Vacía**: Cuando `loadState.refresh` sea `NotLoading` y `itemCount == 0`, mostrar un mensaje amigable al usuario como *"No se encontraron películas"*.
- **Mockups de referencia**: Si existen archivos de diseño en `docs/mockups/`, usarlos como referencia visual previa para la construcción de los componentes finales.
