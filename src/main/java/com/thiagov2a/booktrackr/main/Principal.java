package com.thiagov2a.booktrackr.main;

import com.thiagov2a.booktrackr.model.Autor;
import com.thiagov2a.booktrackr.model.Datos;
import com.thiagov2a.booktrackr.model.DatosLibro;
import com.thiagov2a.booktrackr.model.Libro;
import com.thiagov2a.booktrackr.repository.LibroRepository;
import com.thiagov2a.booktrackr.service.ConsumoAPI;
import com.thiagov2a.booktrackr.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";
    private static final String URL_BUSQUEDA = "?search=";
    private static final int LIMITE_LIBROS = 10; // Límite de libros a mostrar
    private static final int LIMITE_MAXIMO = 50; // Límite de libros para volver al menú

    private final Scanner input;
    private final ConsumoAPI consumoAPI;
    private final ConvierteDatos convierteDatos;
    private final LibroRepository libroRepository;

    public Principal(LibroRepository libroRepository) {
        this.input = new Scanner(System.in);
        this.consumoAPI = new ConsumoAPI();
        this.convierteDatos = new ConvierteDatos();
        this.libroRepository = libroRepository;
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
        System.out.println("\nIngrese el título del libro que desea buscar:");
        System.out.print("Título: ");
        var nombreLibro = obtenerNombre();

        Libro libro = buscarLibro(nombreLibro);

        if (libro != null) {
            Optional<Libro> libroOptional = libroRepository.findByTitulo(libro.getTitulo());

            if (libroOptional.isEmpty()) {
                System.out.println("\nLibro encontrado:");
                System.out.println("==============================");
                System.out.println(libro);
                System.out.println("==============================");

                libroRepository.save(libro);
                System.out.println("El libro ha sido guardado correctamente en la base de datos.");
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
            System.out.printf("%d. %s\n", i + 1, librosLimitados.get(i).getTitulo());
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
                    .sorted(Comparator.comparing(Libro::getTitulo).reversed())
                    .forEach(System.out::println);
        }
    }

    private void listarAutores() {
        List<Autor> autores = libroRepository.findAll()
                .stream()
                .map(Libro::getAutor)
                .distinct()
                .toList();

        if (autores.isEmpty()) {
            System.out.println("\nNo hay autores registrados en la base de datos.");
        } else {
            System.out.println("\nAutores registrados:");
            System.out.println("==============================");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre).reversed())
                    .forEach(System.out::println);
        }
    }

    private void listarAutoresVivosEnUnDeterminadoAnio() {
        System.out.println("\nFuncionalidad no implementada aún.");
    }

    private void listarAutoresNacidosEnUnDeterminadoAnio() {
        System.out.println("\nFuncionalidad no implementada aún.");
    }

    private void listarAutoresPorAnioDeSuMuerte() {
        System.out.println("\nFuncionalidad no implementada aún.");
    }

    private void listarLibrosEnUnDeterminadoIdioma() {
        System.out.println("\nFuncionalidad no implementada aún.");
    }
}
