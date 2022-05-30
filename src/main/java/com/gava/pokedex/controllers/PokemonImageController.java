package com.gava.pokedex.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.gava.pokedex.domain.PokemonImage;
import com.gava.pokedex.services.PokemonImageService;
import com.gava.pokedex.services.PokemonService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping(value = "pokemon/img")
public class PokemonImageController {

    @Autowired
    private PokemonImageService service;

    @GetMapping(value = "{id}")
    public ResponseEntity<String> findByIdParamHandler(
            @PathVariable Long id,
            @RequestParam(required = false) String fillColor
    ) {
        return ResponseEntity.ok().contentType(MediaType.valueOf("image/svg+xml"))
                .body(service.findByIdParamHandler(id, fillColor));
    }


}
