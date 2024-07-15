package com.thiagov2a.booktrackr.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Datos(@JsonAlias("count") Integer total,
                    @JsonAlias("results") List<DatosLibro> libros) {
}
