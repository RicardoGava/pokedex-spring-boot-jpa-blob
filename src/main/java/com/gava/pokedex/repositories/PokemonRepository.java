package com.gava.pokedex.repositories;

import com.gava.pokedex.domain.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

    /*SELECT p.name FROM db_pokedex.pokemon_types t
join db_pokedex.pokemon p
on p.id = t.id
where t.types = 14 and t.id in(SELECT id FROM db_pokedex.pokemon_types where types = 10)*/

    /*SELECT p.name FROM db_pokedex.pokemon_types t
join db_pokedex.pokemon p
on p.id = t.id
where t.types = 11 and t.id not in(SELECT id FROM db_pokedex.pokemon_types where types != 11)*/
}
