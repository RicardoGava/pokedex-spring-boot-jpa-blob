package com.gava.pokedex.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gava.pokedex.domain.enums.Type;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    @Column(precision = 5, scale = 1)
    private Double height;
    @Column(precision = 5, scale = 1)
    private Double weight;
    @Setter(AccessLevel.NONE)
    @ElementCollection
    private Set<Type> types = new LinkedHashSet<>();
    @JsonIgnore
    private Blob img;
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

    public void addType(Type type) {
        this.types.add(type);
    }

    public void addAbility(PokemonAbility pokemonAbility) {
        this.abilities.add(pokemonAbility);
    }

}
