package com.gava.pokedex.services.exceptions;

public class PokemonNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PokemonNotFoundException(Object id) {
        super("Pokemon " + id + " not found.");
    }
}