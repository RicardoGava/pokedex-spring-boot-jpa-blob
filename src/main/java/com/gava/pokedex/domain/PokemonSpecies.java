package com.gava.pokedex.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gava.pokedex.domain.enums.Type;
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
@JsonPropertyOrder({"flavor-text", "genus", "shape", "color", "capture-rate", "base-happiness", "base-experience",
        "is-baby", "is-legendary", "is-mythical", "evolves-from", "evolves-to"})
public class PokemonSpecies implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("flavor-text")
    private String flavorText;
    private String genus;
    private String shape;
    private String color;
    @JsonProperty("capture-rate")
    private Integer captureRate;
    @JsonProperty("base-happiness")
    private Integer baseHappiness;
    @JsonProperty("base-experience")
    private Integer baseExperience;
    @JsonProperty("is-baby")
    private Boolean isBaby;
    @JsonProperty("is-legendary")
    private Boolean isLegendary;
    @JsonProperty("is-mythical")
    private Boolean isMythical;
    @JsonProperty("evolves-from")
    private String evolvesFrom;
    @JsonProperty("evolves-to")
    @ElementCollection
    private Set<String> evolvesTo = new HashSet<>();
    @JsonIgnore
    @OneToOne
    @MapsId
    private Pokemon pokemon;

    public void addEvolution(String evolutionName) {
        this.evolvesTo.add(evolutionName);
    }

}
