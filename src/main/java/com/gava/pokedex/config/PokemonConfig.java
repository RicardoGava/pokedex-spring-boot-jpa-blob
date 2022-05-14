package com.gava.pokedex.config;

import com.gava.pokedex.domain.Pokemon;
import com.gava.pokedex.domain.PokemonStats;
import com.gava.pokedex.domain.enums.Type;
import com.gava.pokedex.repositories.PokemonRepository;
import io.netty.handler.timeout.ReadTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.sql.rowset.serial.SerialBlob;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class PokemonConfig implements CommandLineRunner {

    @Autowired
    PokemonRepository pokemonRepository;

    @Autowired
    WebClient webClient;

    @Override
    public void run(String... args) throws Exception {

        if (pokemonRepository.findAll().isEmpty() == true) {
            List<Pokemon> pokemonsList = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                Pokemon pokemon = new Pokemon();

                // Recebe o JSON completo
                Mono<String> mono = webClient
                        .method(HttpMethod.GET)
                        .uri("https://pokeapi.co/api/v2/pokemon/" + i)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(10))
                        .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"));

                JSONObject jsonObj = new JSONObject(mono.share().block());

                // Adiciona as informações à classe Pokemon
                pokemon.setName(capitalize(jsonObj.getString("name")));
                pokemon.setHeight(jsonObj.getInt("height"));
                pokemon.setWeight(jsonObj.getInt("weight"));
                pokemon.setBaseExperience(jsonObj.getInt("base_experience"));

                // Adiciona os Types à classe Pokemon
                JSONArray typesArray = jsonObj.getJSONArray("types");
                int[] types = new int[typesArray.length()];
                for (int j = 0; j < typesArray.length(); j++) {
                    JSONObject jsonTypes = (JSONObject) typesArray.get(j);
                    Type type = Type.valueOf(jsonTypes.getJSONObject("type").getString("name").toUpperCase());
                    types[j] = type.getCode();
                }
                pokemon.setTypes(types);

                // Captura imagem SVG como String
                Mono<String> svg = webClient
                        .method(HttpMethod.GET)
                        .uri(jsonObj
                                .getJSONObject("sprites")
                                .getJSONObject("other")
                                .getJSONObject("dream_world")
                                .getString("front_default")
                        )
                        .accept(MediaType.ALL)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(10))
                        .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"));

                pokemon.setImg(new SerialBlob(svg.share().block().getBytes(StandardCharsets.UTF_8)));

                pokemonRepository.save(pokemon);

                // Adiciona os Stats à classe Pokemon
                JSONArray statsArray = jsonObj.getJSONArray("stats");

                PokemonStats stats = new PokemonStats(
                        getBaseStat(statsArray, 0),
                        getBaseStat(statsArray, 1),
                        getBaseStat(statsArray, 2),
                        getBaseStat(statsArray, 3),
                        getBaseStat(statsArray, 4),
                        getBaseStat(statsArray, 5),
                        pokemon
                );

                pokemon.setStats(stats);

                pokemonRepository.save(pokemon);
            }
        }
    }

    private static int getBaseStat(JSONArray statsArray, int index) {
        JSONObject baseStat = (JSONObject) statsArray.get(index);
        return baseStat.getInt("base_stat");
    }

    private static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
