package com.gava.pokedex.controllers;

import com.gava.pokedex.services.PokemonImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "pokemon/img")
public class PokemonImageController {

    @Autowired
    private PokemonImageService service;

    @GetMapping(value = "{id}")
    public ResponseEntity<String> findByIdParamHandler(
            @PathVariable Long id,
            @RequestParam(required = false) String fillColor,
            @RequestParam(required = false) String width,
            @RequestParam(required = false) String height
    ) {
        return ResponseEntity.ok().contentType(MediaType.valueOf("image/svg+xml"))
                .body(service.findByIdParamHandler(id, fillColor, width, height));
    }
}
