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

    public String findByIdParamHandler(Long id, String fillColor, String width, String height) {
        PokemonImage obj = findById(id);
        String img;
        try {
            img = new String(obj.getImg().getBytes(1, (int) obj.getImg().length()));
            if (checkColor(fillColor)) {
                img = replaceFill(img, fillColor);
            }
            if (parseIntOrNull(width) != null || parseIntOrNull(height) != null) {
                img = resize(img, parseIntOrNull(width), parseIntOrNull(height));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            img = e.getMessage();
        }
        return img;
    }

    private static Boolean checkColor(String color) {
        return color != null && color.matches("^[0-9a-fA-F]+$") && (color.length() == 3 || color.length() == 6);
    }

    private static Integer parseIntOrNull(String strNum) {
        if (strNum == null) {
            return null;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return null;
        }
        return Integer.parseInt(strNum);
    }

    private static String replaceFill(String svg, String color) {
        StringBuilder sb = new StringBuilder();
        int lastReplacement = 0;
        color = "#" + color;
        while (true) {
            int nextPos = svg.indexOf("fill=\"", lastReplacement);
            if (nextPos != -1) {
                sb.append(svg, lastReplacement, nextPos + 6).append(color);
                lastReplacement = svg.indexOf("\"", nextPos + 6);
            } else {
                sb.append(svg, lastReplacement, svg.length());
                break;
            }
        }
        return sb.toString();
    }

    private static String resize(String svg, Integer width, Integer height) {
        StringBuilder sb = new StringBuilder();
        int imgWidth = Integer.parseInt(svg.substring(svg.indexOf("width=\"") + 7,
                svg.indexOf("px", svg.indexOf("width=\""))));
        int imgHeight = Integer.parseInt(svg.substring(svg.indexOf("height=\"") + 8,
                svg.indexOf("px", svg.indexOf("height=\""))));
        int newImgWidth;
        int newImgHeight;
        if (width != null) {
            newImgWidth = width;
            newImgHeight = width * imgHeight / imgWidth;
            if (height != null && newImgHeight > height) {
                newImgHeight = height;
                newImgWidth = height * imgWidth / imgHeight;
            }
        } else {
            newImgHeight = height;
            newImgWidth = height * imgWidth / imgHeight;
        }

        sb
                .append(svg, 0, svg.indexOf("width=\"") + 7)
                .append(newImgWidth)
                .append(svg, svg.indexOf("px", svg.indexOf("width=\"")), svg.indexOf("height=\"") + 8)
                .append(newImgHeight)
                .append(svg, svg.indexOf("px", svg.indexOf("height=\"")), svg.length());

        return sb.toString();
    }

}
