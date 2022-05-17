package com.gava.pokedex.repositories;

import com.gava.pokedex.domain.PokemonAbility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonAbilityRepository extends JpaRepository<PokemonAbility, Long> {
    PokemonAbility findByName(String name);
}
