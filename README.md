<h4 align="center">
  🎓 Alura + ONE
</h4>

<h1 align="center">
  📚 BookTrackr - Catálogo de Libros
</h1>

<p align="center">
  Este documento contiene el desarrollo y la documentación del proyecto BookTrackr, un catálogo de libros utilizando Java del Programa Alura + ONE.
</p>

## 📝 Descripción del Proyecto

BookTrackr es una aplicación de consola desarrollada en Java que permite a los usuarios buscar libros a través de una API, registrar información sobre estos libros en una base de datos, y realizar varias consultas sobre los libros almacenados.

## 📋 Funcionalidades

- **Buscar Libros por Título**: Permite buscar un libro por su nombre utilizando una API y registrar los detalles en la base de datos.
- **Listar Libros Registrados**: Muestra todos los libros almacenados en la base de datos.
- **Listar Autores Registrados**: Muestra los autores cuyos libros están en la base de datos.
- **Listar Autores Vivos en un Año Específico**: Permite buscar autores vivos en un año determinado.
- **Listar Libros por Idioma**: Filtra los libros almacenados según el idioma especificado.

## 📁 Estructura del Proyecto

```plaintext
📦 com.thiagov2a.booktrackr
 ┣ 📂 main
 ┃ ┗ 📜 Principal.java
 ┣ 📂 model
 ┃ ┣ 📜 Autor.java
 ┃ ┣ 📜 Datos.java
 ┃ ┣ 📜 DatosLibro.java
 ┃ ┗ 📜 Libro.java
 ┣ 📂 repository
 ┃ ┣ 📜 AutorRepository.java
 ┃ ┗ 📜 LibroRepository.java
 ┣ 📂 service
 ┃ ┣ 📜 ConsumoAPI.java
 ┃ ┗ 📜 ConvierteDatos.java
 ┗ 📜 BookTrackr.java
```

- **`com.thiagov2a.booktrackr`**: Contiene la clase principal `BookTrackr` que maneja la lógica de interacción con el usuario.
- **`com.thiagov2a.booktrackr.main`**: Contiene la clase `Principal` que maneja la lógica principal del programa.
- **`com.thiagov2a.booktrackr.model`**: Define las clases `Libro`, `Autor`, `Datos` y `DatosLibro` para representar los libros, autores y datos de la API en la base de datos.
- **`com.thiagov2a.booktrackr.repository`**: Incluye las interfaces `AutorRepository` y `LibroRepository` para las operaciones CRUD de `Autor` y `Libro`.
- **`com.thiagov2a.booktrackr.service`**: Incluye las clases de servicio `ConsumoAPI` y `ConvierteDatos` para manejar la comunicación con la API y las operaciones en la base de datos.

## 🚀 Cómo Ejecutar el Proyecto

1. **Clonar el Repositorio**: `git clone https://github.com/tu-usuario/booktrackr.git`
2. **Configurar la API Key**: Inserte su clave de API en la configuración del proyecto.
3. **Ejecutar el Proyecto**: Utilice su IDE favorito para compilar y ejecutar la clase principal `BookTrackr`.

## 🛠 Tecnologías Utilizadas

- **Java**: 💻 Lenguaje de programación principal.
- **Spring Boot**: 🧑‍💻 Framework para el desarrollo de aplicaciones Java.
- **PostgreSQL**: 🗃 Base de datos utilizada para almacenar información.
- **Git**: 🌳 Sistema de control de versiones.
- **GitHub**: 🌐 Plataforma de hospedaje de código fuente y colaboración.

<p align="center">
  Alura + ONE | BookTrackr - Catálogo de Libros
</p>
