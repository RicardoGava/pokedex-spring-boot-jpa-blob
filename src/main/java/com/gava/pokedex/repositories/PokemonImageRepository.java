package com.gava.pokedex.repositories;

import com.gava.pokedex.domain.PokemonImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonImageRepository extends JpaRepository<PokemonImage, Long> {
}
