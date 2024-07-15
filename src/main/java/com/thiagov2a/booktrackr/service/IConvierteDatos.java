package com.thiagov2a.booktrackr.service;

public interface IConvierteDatos {

    <T> T obtenerDatos(String json, Class<T> clase);

}
