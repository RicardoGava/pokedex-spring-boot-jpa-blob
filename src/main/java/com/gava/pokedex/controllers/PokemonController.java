package com.gava.pokedex.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.gava.pokedex.services.PokemonService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping(value = "pokemon")
public class PokemonController {

    @Autowired
    private PokemonService service;

    @SneakyThrows
    @GetMapping(value = "{id}")
    public ResponseEntity<String> findByIdFiltered(@PathVariable Long id,
                                                   @RequestParam(required = false) Boolean species,
                                                   @RequestParam(required = false) Boolean abilities,
                                                   @RequestParam(required = false) Boolean stats,
                                                   @RequestParam(required = false) Boolean types
    ) {
        SimpleFilterProvider sfp = new SimpleFilterProvider();
        Set<String> hide = new HashSet<>();
        if (Boolean.FALSE.equals(species)) {
            hide.add("species");
        }
        if (Boolean.FALSE.equals(abilities)) {
            hide.add("abilities");
        }
        if (Boolean.FALSE.equals(stats)) {
            hide.add("stats");
        }
        if (Boolean.FALSE.equals(types)) {
            hide.add("types");
        }
        sfp.addFilter("PokemonFilter", SimpleBeanPropertyFilter.serializeAllExcept(hide));
        ObjectMapper om = new ObjectMapper();
        om.setFilterProvider(sfp);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(om.writeValueAsString(service.findById(id)));
    }

}
