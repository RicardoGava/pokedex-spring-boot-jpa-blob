package com.gava.pokedex.services.exceptions;

public class IdNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IdNotFoundException(Object id) {
        super("Id " + id + " not found.");
    }
}