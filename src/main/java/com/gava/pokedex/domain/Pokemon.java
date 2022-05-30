package com.gava.pokedex.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.gava.pokedex.domain.enums.Type;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonFilter("PokemonFilter")
public class Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(precision = 5, scale = 1)
    private Double height;
    @Column(precision = 5, scale = 1)
    private Double weight;
    @ElementCollection
    @OrderColumn(name = "slot")
    @OrderBy("slot")
    private int[] types;
    @OneToOne(mappedBy = "pokemon", cascade = CascadeType.ALL)
    private PokemonSpecies species;
    @OneToOne(mappedBy = "pokemon", cascade = CascadeType.ALL)
    private PokemonStats stats;
    @ManyToMany
    @JoinTable(name = "pokemon_has_ability",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "ability_id")
    )
    @Setter(AccessLevel.NONE)
    private Set<PokemonAbility> abilities = new LinkedHashSet<>();

    public Type[] getTypes() {
        Type[] types = new Type[this.types.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = Type.valueOf(this.types[i]);
        }
        return types;
    }

    public void addAbility(PokemonAbility pokemonAbility) {
        this.abilities.add(pokemonAbility);
    }

}
