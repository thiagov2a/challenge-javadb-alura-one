package com.thiagov2a.booktrackr.repository;

import com.thiagov2a.booktrackr.model.Autor;
import com.thiagov2a.booktrackr.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    public Optional<Libro> findByTitulo(String titulo);

    List<Libro> findByIdioma(String idioma);
}
