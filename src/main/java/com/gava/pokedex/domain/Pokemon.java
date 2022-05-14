package com.gava.pokedex.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gava.pokedex.domain.enums.Type;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;

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
    @JsonProperty("base-experience")
    private Integer baseExperience;
    @ElementCollection // 1
    @CollectionTable(name = "pokemon_types", joinColumns = @JoinColumn(name = "id"))
    @OrderColumn(name = "slot")
    private int[] types;
    @JsonIgnore
    private Blob img;
    @OneToOne(mappedBy = "pokemon", cascade = CascadeType.ALL)
    private PokemonStats stats;

    public Type[] getTypes() {
        Type[] types = new Type[this.types.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = Type.valueOf(this.types[i]);
        }
        return types;
    }
}
