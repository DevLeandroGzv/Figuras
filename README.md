# Figuras
Aplicación móvil Android cuyo objetivo es consumir un servicio REST para obtener un listado de figuras geométricas y procesar la información visualmente.
Se implementa una arquitectura modular basada en Clean Architecture + MVVM, garantizando mantenibilidad, testabilidad y escalabilidad.

🧩 Librerías principales
Propósito	 -------------  Librería
Dependency Injection	   Hilt
Llamadas                 HTTP	Retrofit
Cliente de red	         OkHttp
Serialización JSON	     Gson
Asincronía	             Kotlin Coroutines

Buenas prácticas implementadas

Arquitectura desacoplada por capas
Repository pattern con dominio independiente del framework
Retrofit con corrutinas (sin callbacks legacy)
Inyección de dependencias con Hilt
Mapeo DTO → Domain para evitar filtrar modelos de red hacia la UI
