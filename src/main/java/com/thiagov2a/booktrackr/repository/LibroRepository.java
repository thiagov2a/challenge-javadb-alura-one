package com.thiagov2a.booktrackr.repository;

import com.thiagov2a.booktrackr.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    public Optional<Libro> findByTitulo(String titulo);

}
