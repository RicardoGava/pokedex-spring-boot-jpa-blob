package com.gava.pokedex.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;

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
    @JsonIgnore
    private Blob img;
    @OneToOne(mappedBy = "pokemon", cascade = CascadeType.ALL)
    private PokemonStats stats;

    public Pokemon(Long id, String name, Blob img) {
        this.id = id;
        this.name = name;
        this.img = img;
    }
}
