package com.gava.pokedex.services;

import com.gava.pokedex.domain.Pokemon;
import com.gava.pokedex.repositories.PokemonRepository;
import com.gava.pokedex.services.exceptions.PokemonNotFoundException;
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
    public Pokemon findById(String nameOrId) {
        Optional<Pokemon> obj;
        if (isLong(nameOrId)) {
            obj = repository.findById(Long.parseLong(nameOrId));
        } else {
            obj = repository.findByName(nameOrId);
        }
        return obj.orElseThrow(() -> new PokemonNotFoundException(nameOrId));
    }

    public static boolean isLong(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
