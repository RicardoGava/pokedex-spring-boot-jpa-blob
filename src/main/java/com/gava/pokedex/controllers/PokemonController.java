package com.gava.pokedex.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.gava.pokedex.domain.Pokemon;
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
@RequestMapping(value = "pokemon")
public class PokemonController {

    @Autowired
    private PokemonService service;

    /*@GetMapping
    public ResponseEntity<List<Pokemon>> findAll() {
        List<Pokemon> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }*/

    @SneakyThrows
    @ResponseBody
    @GetMapping(value = "{id}")
    public ResponseEntity<String> findByIdFiltered(@PathVariable Long id,
                                                   @RequestParam(required = false) Boolean species,
                                                   @RequestParam(required = false) Boolean abilities,
                                                   @RequestParam(required = false) Boolean stats,
                                                   @RequestParam(required = false) Boolean types
    ) {
        SimpleFilterProvider sfp = new SimpleFilterProvider();
        Set<String> hide = new HashSet<>();
        if (species != null && !species) {
            hide.add("species");
        }
        if (abilities != null && !abilities) {
            hide.add("abilities");
        }
        if (stats != null && !stats) {
            hide.add("stats");
        }
        if (types != null && !types) {
            hide.add("types");
        }
        sfp.addFilter("PokemonFilter", SimpleBeanPropertyFilter.serializeAllExcept(hide));
        ObjectMapper om = new ObjectMapper();
        om.setFilterProvider(sfp);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(om.writeValueAsString(service.findById(id)));
    }

    @GetMapping(value = "img/{id}")
    public ResponseEntity<String> findImgById(@PathVariable Long id) throws SQLException {
        Pokemon obj = service.findById(id);
        String img = new String(obj.getImg().getBytes(1, (int) obj.getImg().length()));
        return ResponseEntity.ok().contentType(MediaType.valueOf("image/svg+xml")).body(img);
    }
}
