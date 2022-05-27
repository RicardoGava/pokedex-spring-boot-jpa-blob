package com.gava.pokedex.services;

import com.gava.pokedex.domain.Pokemon;
import com.gava.pokedex.domain.PokemonImage;
import com.gava.pokedex.repositories.PokemonImageRepository;
import com.gava.pokedex.repositories.PokemonRepository;
import com.gava.pokedex.services.exceptions.IdNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PokemonImageService {

    @Autowired
    private PokemonImageRepository repository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PokemonImage findById(Long id) {
        Optional<PokemonImage> obj = repository.findById(id);
        return obj.orElseThrow(() -> new IdNotFoundException(id));
    }

}
