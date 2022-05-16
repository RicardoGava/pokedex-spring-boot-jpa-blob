package com.gava.pokedex.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gava.pokedex.domain.enums.Type;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private Integer height;
    private Integer weight;
    private String color;
    private String genus;
    @JsonProperty("base-experience")
    private Integer baseExperience;
    @Setter(AccessLevel.NONE)
    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "id"))
    private Set<Type> types = new LinkedHashSet<>();
    @JsonProperty("flavor-text")
    private String flavorText;
    @JsonIgnore
    private Blob img;
    @OneToOne(mappedBy = "pokemon", cascade = CascadeType.ALL)
    private PokemonStats stats;

    public void addType(Type type) {
        types.add(type);
    }
}
