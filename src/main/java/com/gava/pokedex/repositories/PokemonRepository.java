package com.gava.pokedex.repositories;

import com.gava.pokedex.domain.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

    Optional<Pokemon>  findByName(String name);
    /*SELECT p.name FROM db_pokedex.pokemon_types t
join db_pokedex.pokemon p
on p.id = t.pokemon_id
where t.types = 14 and t.pokemon_id in(SELECT pokemon_id FROM db_pokedex.pokemon_types where types = 10)*/

    /*SELECT p.name FROM db_pokedex.pokemon_types t
join db_pokedex.pokemon p
on p.id = t.pokemon_id
where t.types = 11 and t.pokemon_id not in(SELECT pokemon_id FROM db_pokedex.pokemon_types where types != 11)*/
}
