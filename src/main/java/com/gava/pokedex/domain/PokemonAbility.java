package com.gava.pokedex.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"name", "hidden", "short-effect","effect"})
public class PokemonAbility implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean hidden;
    @JsonProperty("short-effect")
    private String shortEffect;
    @Lob
    private String effect;
    @JsonIgnore
    @ManyToMany(mappedBy = "abilities")
    @Setter(AccessLevel.NONE)
    private Set<Pokemon> pokemons = new HashSet<>();

}
