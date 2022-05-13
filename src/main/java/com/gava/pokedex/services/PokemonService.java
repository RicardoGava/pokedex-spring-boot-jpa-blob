package com.gava.pokedex.services;

import com.gava.pokedex.domain.Pokemon;
import com.gava.pokedex.repositories.PokemonRepository;
import com.gava.pokedex.services.exceptions.IdNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PokemonService {

    @Autowired
    private PokemonRepository repository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Pokemon> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Pokemon findById(Long id) {
        Optional<Pokemon> obj = repository.findById(id);
        return obj.orElseThrow(() -> new IdNotFoundException(id));
    }

}
