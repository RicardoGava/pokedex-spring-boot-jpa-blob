package com.gava.pokedex.controllers;

import com.gava.pokedex.domain.Pokemon;
import com.gava.pokedex.services.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@CrossOrigin
@RequestMapping(value = "pokemon")
public class PokemonController {

    @Autowired
    private PokemonService service;

    /*@GetMapping
    public ResponseEntity<List<Pokemon>> findAll() {
        List<Pokemon> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }*/

    @GetMapping(value = "{id}")
    public ResponseEntity<Pokemon> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping(value = "img/{id}")
    public ResponseEntity<String> findImgById(@PathVariable Long id) throws SQLException {
        Pokemon obj = service.findById(id);
        String img = new String(obj.getImg().getBytes(1, (int) obj.getImg().length()));
        return ResponseEntity.ok().contentType(MediaType.valueOf("image/svg+xml")).body(img);
    }
}
