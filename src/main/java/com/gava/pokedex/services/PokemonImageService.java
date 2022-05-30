package com.gava.pokedex.services;

import com.gava.pokedex.domain.PokemonImage;
import com.gava.pokedex.repositories.PokemonImageRepository;
import com.gava.pokedex.services.exceptions.PokemonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;

@Service
public class PokemonImageService {

    @Autowired
    private PokemonImageRepository repository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PokemonImage findById(Long id) {
        Optional<PokemonImage> obj = repository.findById(id);
        return obj.orElseThrow(() -> new PokemonNotFoundException(id));
    }

    public String findByIdParamHandler(Long id, String fillColor) {
        PokemonImage obj = findById(id);
        String img;
        try {
            img = new String(obj.getImg().getBytes(1, (int) obj.getImg().length()));
            if (fillColor != null && fillColor.matches("^[0-9a-fA-F]+$")
                    && (fillColor.length() == 3 || fillColor.length() == 6)) {
                img = replaceFill(img, fillColor);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            img = e.getMessage();
        }
        return img;
    }

    private static String replaceFill(String svg, String color) {
        StringBuilder sb = new StringBuilder();
        int lastReplacement = 0;
        color = "#" + color;
        while (true) {
            int nextPos = svg.indexOf("fill=\"", lastReplacement);
            if (nextPos != -1) {
                sb.append(svg, lastReplacement, nextPos + 6);
                sb.append(color);
                lastReplacement = svg.indexOf("\"", nextPos + 6);
            } else {
                sb.append(svg, lastReplacement, svg.length());
                break;
            }
        }
        return sb.toString();
    }

}
