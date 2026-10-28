# Contexto del proyecto: MovieExplorer

App Android académica que consume la API REST de TMDB (https://api.themoviedb.org/3/).

## Stack
- Kotlin, Jetpack Compose (Material 3)
- Retrofit + Gson + OkHttp logging interceptor
- Hilt con KSP para inyección de dependencias
- Paging 3 para scroll infinito
- Navigation Compose
- Coil para imágenes

## Arquitectura en capas (respetarla siempre)
- data/remote: interfaz de Retrofit y DTOs (sufijo Dto)
- data/paging: PagingSources
- data/repository: implementación del repositorio
- domain/model: modelos limpios de Kotlin (sin anotaciones de Gson)
- domain/repository: interfaz del repositorio
- di: módulos de Hilt
- ui: pantallas y componentes Compose; cada pantalla con su ViewModel (@HiltViewModel)
- util: utilidades como el monitor de conectividad

## Reglas
- La API key está en BuildConfig.TMDB_API_KEY. Nunca escribirla en el código ni leer local.properties para mostrarla.
- Los DTOs se mapean a modelos de dominio con funciones de extensión (toDomain()).
- La UI nunca usa DTOs directamente.
- Textos de la interfaz en español; peticiones con language=es-ES.
- Manejar errores de red y mostrar mensajes al usuario.
- No hacer commits ni push; el usuario los hace manualmente.
- Al terminar cada tarea, compilar y resumir los archivos creados/modificados.