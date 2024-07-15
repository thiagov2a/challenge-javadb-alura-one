package com.thiagov2a.booktrackr.main;

import com.thiagov2a.booktrackr.model.Autor;
import com.thiagov2a.booktrackr.model.Datos;
import com.thiagov2a.booktrackr.model.DatosLibro;
import com.thiagov2a.booktrackr.model.Libro;
import com.thiagov2a.booktrackr.repository.AutorRepository;
import com.thiagov2a.booktrackr.repository.LibroRepository;
import com.thiagov2a.booktrackr.service.ConsumoAPI;
import com.thiagov2a.booktrackr.service.ConvierteDatos;

import java.time.Year;
import java.util.*;

public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";
    private static final String URL_BUSQUEDA = "?search=";
    private static final int LIMITE_LIBROS = 10; // Límite de libros a mostrar
    private static final int LIMITE_MAXIMO = 50; // Límite de libros para volver al menú

    private final Scanner input;
    private final ConsumoAPI consumoAPI;
    private final ConvierteDatos convierteDatos;
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.input = new Scanner(System.in);
        this.consumoAPI = new ConsumoAPI();
        this.convierteDatos = new ConvierteDatos();
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void menu() {
        boolean salir = false;

        while (!salir) {
            System.out.print("""
                    \n===============================
                                BookTrackr
                    ===============================
                    1. Buscar libros por título
                    2. Listar libros registrados
                    3. Listar autores registrados
                    4. Listar autores vivos en un año específico
                    5. Listar autores nacidos en un año específico
                    6. Listar autores por año de muerte
                    7. Listar libros en un idioma específico
                    0. Salir
                    ===============================
                    Seleccione una opción:\s""");

            var opcion = obtenerOpcion();

            switch (opcion) {
                case 1 -> buscarLibrosPorTitulo();
                case 2 -> listarLibros();
                case 3 -> listarAutores();
                case 4 -> listarAutoresVivosEnUnDeterminadoAnio();
                case 5 -> listarAutoresNacidosEnUnDeterminadoAnio();
                case 6 -> listarAutoresPorAnioDeSuMuerte();
                case 7 -> listarLibrosEnUnDeterminadoIdioma();
                case 0 -> {
                    System.out.println("\n¡Gracias por usar BookTrackr! ¡Hasta luego!");
                    salir = true;
                }
                default -> System.out.println("Opción no válida. Por favor, seleccione un número del 0 al 7.");
            }
        }
        input.close();
    }

    private Integer obtenerOpcion() {
        while (true) {
            try {
                return Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor, ingrese un número válido: ");
            }
        }
    }

    private void buscarLibrosPorTitulo() {
        System.out.println("\nIngrese el título del libro que desea buscar.");
        System.out.print("Título: ");
        var nombreLibro = obtenerNombre();

        Libro libro = buscarLibro(nombreLibro);

        if (libro != null) {
            // Buscar el libro en la base de datos
            Optional<Libro> libroOptional = libroRepository.findByTitulo(libro.getTitulo());

            if (libroOptional.isEmpty()) {
                System.out.println("\nLibro encontrado:");
                System.out.println("==============================");
                System.out.println(libro);
                System.out.println("==============================");

                // Verificar y asignar el autor
                Autor autor = libro.getAutor();
                if (autor != null) {
                    Optional<Autor> autorOptional = autorRepository.findByNombre(autor.getNombre());

                    if (autorOptional.isEmpty()) {
                        // Guardar el autor si no existe en la base de datos
                        try {
                            autor = autorRepository.save(autor);
                            System.out.println("El autor ha sido guardado correctamente en la base de datos.");
                        } catch (Exception e) {
                            System.out.println("Error al guardar el autor: " + e.getMessage());
                            return; // Salir si no se puede guardar el autor
                        }
                    } else {
                        // Usar el autor existente
                        autor = autorOptional.get();
                    }

                    // Asignar el autor al libro
                    libro.setAutor(autor);
                }

                try {
                    // Guardar el libro
                    libroRepository.save(libro);
                    System.out.println("El libro ha sido guardado correctamente en la base de datos.");
                } catch (Exception e) {
                    System.out.println("Error al guardar el libro: " + e.getMessage());
                }
            } else {
                System.out.println("El libro ya está registrado en la base de datos.");
            }
        }
    }

    private String obtenerNombre() {
        return input.nextLine().trim();
    }

    private Libro buscarLibro(String nombreLibro) {
        String json = consumoAPI.obtenerDatos(URL_BASE + URL_BUSQUEDA + nombreLibro.replace(" ", "+").toLowerCase());
        Datos datos = convierteDatos.obtenerDatos(json, Datos.class);

        if (datos.libros().isEmpty()) {
            System.out.println("\nNo se encontraron libros que coincidan con \"" + nombreLibro + "\".");
            return null;
        }

        if (datos.libros().size() > LIMITE_MAXIMO) {
            System.out.println("\nSe encontraron demasiados libros. Por favor, refine su búsqueda para obtener resultados más específicos.");
            return null;
        }

        List<Libro> librosLimitados = datos.libros()
                .stream()
                .sorted(Comparator.comparing(DatosLibro::descargas).reversed())
                .limit(LIMITE_LIBROS)
                .map(Libro::new)
                .toList();

        System.out.println("\nResultados encontrados:");
        System.out.println("==============================");
        for (int i = 0; i < librosLimitados.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, librosLimitados.get(i));
        }

        // Si hay más libros que el límite, mostrar un mensaje
        if (datos.libros().size() > LIMITE_LIBROS) {
            System.out.println("\nSe mostraron los primeros " + LIMITE_LIBROS + " libros. Para ver más resultados, refine su búsqueda.");
        }

        // Permitir que el usuario elija un libro
        System.out.print("\nSeleccione el número del libro que desea guardar (1-" + librosLimitados.size() + "): ");
        int opcion = obtenerOpcion();

        // Validar la opción del usuario
        while (opcion < 1 || opcion > librosLimitados.size()) {
            System.out.print("Opción no válida. Por favor, seleccione un número entre 1 y " + librosLimitados.size() + ": ");
            opcion = obtenerOpcion();
        }

        // Devolver el libro seleccionado
        return librosLimitados.get(opcion - 1);
    }

    private void listarLibros() {
        List<Libro> libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("\nNo hay libros registrados en la base de datos.");
        } else {
            System.out.println("\nLibros registrados:");
            System.out.println("==============================");
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getDescargas).reversed())
                    .forEach(System.out::println);
        }
    }

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("\nNo hay autores registrados en la base de datos.");
        } else {
            System.out.println("\nAutores registrados:");
            System.out.println("==============================");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }
    }

    private void listarAutoresVivosEnUnDeterminadoAnio() {
        System.out.print("\nIngrese el año de actividad de los autores: ");
        var anio = obtenerAnio();

        List<Autor> autores = autorRepository.buscarAutoresVivosPorAnio(anio);

        if (autores.isEmpty()) {
            System.out.println("\nNo se encontraron autores vivos en " + anio + ".");
        } else {
            System.out.println("\nAutores vivos en " + anio + ":");
            System.out.println("==============================");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }
    }

    private Year obtenerAnio() {
        while (true) {
            try {
                int anio = Integer.parseInt(input.nextLine().trim());
                return Year.of(anio);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor, ingrese un año válido: ");
            }
        }
    }

    private void listarAutoresNacidosEnUnDeterminadoAnio() {
        System.out.print("\nIngrese el año de nacimiento de los autores: ");
        var anio = obtenerAnio();

        List<Autor> autores = autorRepository.findByAnioNacimiento(anio);

        if (autores.isEmpty()) {
            System.out.println("\nNo se encontraron autores nacidos en " + anio + ".");
        } else {
            System.out.println("\nAutores nacidos en " + anio + ":");
            System.out.println("==============================");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }
    }

    private void listarAutoresPorAnioDeSuMuerte() {
        System.out.print("\nIngrese el año de fallecimiento de los autores: ");
        var anio = obtenerAnio();

        List<Autor> autores = autorRepository.findByAnioFallecimiento(anio);

        if (autores.isEmpty()) {
            System.out.println("\nNo se encontraron autores que hayan muerto en " + anio + ".");
        } else {
            System.out.println("\nAutores que hayan muerto en " + anio + ":");
            System.out.println("==============================");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }
    }

    private void listarLibrosEnUnDeterminadoIdioma() {
        System.out.print("""
                \nIdiomas disponibles
                ==============================
                1. Inglés (en)
                2. Francés (fr)
                3. Finlandés (fi)
                4. Alemán (de)
                5. Español (es)
                \nSeleccione una opción:\s""");
        var opcion = obtenerOpcion();

        switch (opcion) {
            case 1 -> listarLibrosPorIdioma("Inglés", "en");
            case 2 -> listarLibrosPorIdioma("Francés", "fr");
            case 3 -> listarLibrosPorIdioma("Finlandés", "fi");
            case 4 -> listarLibrosPorIdioma("Alemán", "de");
            case 5 -> listarLibrosPorIdioma("Español", "es");
            default -> System.out.println("Opción no válida. Por favor, seleccione un número del 1 al 5.");
        }
    }

    private void listarLibrosPorIdioma(String nombre, String idioma) {
        List<Libro> libros = libroRepository.findByIdioma(idioma);

        if (libros.isEmpty()) {
            System.out.println("\nNo se encontraron libros en " + nombre + ".");
        } else {
            System.out.println("\nLibros en " + nombre + ":");
            System.out.println("==============================");
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getDescargas).reversed())
                    .forEach(System.out::println);
        }
    }
}
