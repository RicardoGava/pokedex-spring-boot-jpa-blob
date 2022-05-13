package com.gava.pokedex.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONPropertyName;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"hp", "attack", "defense", "special-attack", "special-defense", "speed"})
public class PokemonStats implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer hp;
    private Integer attack;
    private Integer defense;
    @JsonProperty("special-attack")
    private Integer specialAttack;
    @JsonProperty("special-defense")
    private Integer specialDefense;
    private Integer speed;
    @JsonIgnore
    @OneToOne
    @MapsId
    private Pokemon pokemon;

    public PokemonStats(Integer hp, Integer attack, Integer defense, Integer specialAttack, Integer specialDefense, Integer speed, Pokemon pokemon) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
        this.pokemon = pokemon;
    }
}
