package com.gava.pokedex.config;

import com.gava.pokedex.domain.Pokemon;
import com.gava.pokedex.domain.PokemonAbility;
import com.gava.pokedex.domain.PokemonSpecies;
import com.gava.pokedex.domain.PokemonStats;
import com.gava.pokedex.domain.enums.Type;
import com.gava.pokedex.repositories.PokemonAbilityRepository;
import com.gava.pokedex.repositories.PokemonRepository;
import io.netty.handler.timeout.ReadTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;

@Configuration
public class PokemonConfig implements CommandLineRunner {

    @Value("${pokedex-onboarding-limit-number}")
    private int limit;

    @Autowired
    PokemonRepository pokemonRepository;

    @Autowired
    PokemonAbilityRepository pokemonAbilityRepository;

    @Autowired
    WebClient webClient1;

    @Autowired
    WebClient webClient2;

    @Override
    public void run(String... args) throws Exception {

        if (pokemonRepository.findAll().isEmpty() == true) {
            List<Pokemon> pokemonsList = new ArrayList<>();

            for (int i = 1; i <= limit; i++) {
                Pokemon pokemon = new Pokemon();

                // Começa a captura do JSON do Pokemon
                Mono<String> pokemonMono = getJson(
                        webClient1,
                        "https://pokeapi.co/api/v2/pokemon/" + i,
                        MediaType.APPLICATION_JSON
                );

                // Começa a captura do JSON da espécie do Pokemon
                Mono<String> speciesMono = getJson(
                        webClient2,
                        "https://pokeapi.co/api/v2/pokemon-species/" + i,
                        MediaType.APPLICATION_JSON
                );

                // Bloqueia e finaliza a captura do JSON do Pokemon
                JSONObject pokemonJsonObj = new JSONObject(pokemonMono.share().block());

                // Adiciona as informações à classe Pokemon
                pokemon.setName(capitalize(pokemonJsonObj.getString("name")));
                pokemon.setHeight(pokemonJsonObj.getInt("height")/10d);
                pokemon.setWeight(pokemonJsonObj.getInt("weight")/10d);

                // Adiciona os Types à classe Pokemon
                JSONArray typesArray = pokemonJsonObj.getJSONArray("types");
                for (Object obj : typesArray) {
                    JSONObject jsonTypes = (JSONObject) obj;
                    Type type = Type.valueOf(jsonTypes.getJSONObject("type").getString("name").toUpperCase());
                    pokemon.addType(type);
                }

                // Começa a captura da imagem SVG do Pokemon como String
                Mono<String> svg = getJson(
                        webClient1,
                        pokemonJsonObj
                                .getJSONObject("sprites")
                                .getJSONObject("other")
                                .getJSONObject("dream_world")
                                .getString("front_default"),
                        MediaType.ALL
                );

                // Bloqueia e finaliza a captura da imagem SVG como String
                pokemon.setImg(new SerialBlob(svg.share().block().getBytes(StandardCharsets.UTF_8)));

                // Salva a classe Pokemon para criar um ID no repositório
                pokemonRepository.save(pokemon);

                // Bloqueia e finaliza a captura do JSON da espécie do Pokemon
                JSONObject speciesJsonObj = new JSONObject(speciesMono.share().block());

                // Adiciona as informações da espécie à classe Pokemon
                PokemonSpecies pokemonSpecies = new PokemonSpecies();
                pokemonSpecies.setBaseHappiness(speciesJsonObj.getInt("base_happiness"));
                pokemonSpecies.setBaseExperience(pokemonJsonObj.getInt("base_experience"));
                pokemonSpecies.setCaptureRate(speciesJsonObj.getInt("capture_rate"));
                pokemonSpecies.setColor(speciesJsonObj.getJSONObject("color").getString("name"));
                pokemonSpecies.setShape(speciesJsonObj.getJSONObject("shape").getString("name"));
                pokemonSpecies.setGenus(getObjects(speciesJsonObj.getJSONArray("genera"), 7).getString("genus"));
                try {
                    pokemonSpecies.setEvolvesFrom(speciesJsonObj.getJSONObject("evolves_from_species").getString("name"));
                } finally {}
                pokemonSpecies.setPokemon(pokemon);
                JSONArray flavorArray = speciesJsonObj.getJSONArray("flavor_text_entries");

                for (int j = flavorArray.length() - 1; j >= 0; j--) {
                    JSONObject jsonFlavor = getObjects(flavorArray, j);
                    if (jsonFlavor.getJSONObject("language").getString("name").equals("en")) {
                        pokemonSpecies.setFlavorText(jsonFlavor.getString("flavor_text"));
                        break;
                    }
                }

                // Adiciona habilidades à classe PokemonAbilities caso ainda não tenha sido adicionada
                // em seguida adiciona a habilidade à classe Pokemon
                JSONArray abilitiesArray = pokemonJsonObj.getJSONArray("abilities");
                for (Object obj : abilitiesArray) {
                    JSONObject jsonAbilities = (JSONObject) obj;
                    PokemonAbility pokemonAbility = pokemonAbilityRepository.findByName(jsonAbilities
                            .getJSONObject("ability").getString("name"));
                    if (pokemonAbility == null) {
                        pokemonAbility = new PokemonAbility();
                        pokemonAbility.setName(jsonAbilities.getJSONObject("ability").getString("name"));
                        pokemonAbility.setHidden(jsonAbilities.getBoolean("is_hidden"));
                        Mono<String> abilityMono = getJson(
                                webClient2,
                                jsonAbilities.getJSONObject("ability").getString("url"),
                                MediaType.APPLICATION_JSON
                        );
                        JSONObject abilityJsonObj = new JSONObject(abilityMono.share().block());
                        JSONArray effectArray = abilityJsonObj.getJSONArray("effect_entries");
                        for (int j = effectArray.length() - 1; j >= 0; j--) {
                            JSONObject jsonEffect = getObjects(effectArray, j);
                            if (jsonEffect.getJSONObject("language").getString("name").equals("en")) {
                                pokemonAbility.setShortEffect(getObjects(abilityJsonObj
                                        .getJSONArray("effect_entries"), j).getString("short_effect"));
                                pokemonAbility.setEffect(getObjects(abilityJsonObj
                                        .getJSONArray("effect_entries"), j).getString("effect"));
                                break;
                            }
                        }
                        pokemonAbilityRepository.save(pokemonAbility);
                    }
                    pokemon.addAbility(pokemonAbility);
                }

                // Adiciona os Stats à classe PokemonStats
                JSONArray statsArray = pokemonJsonObj.getJSONArray("stats");

                PokemonStats pokemonStats = new PokemonStats(
                        getObjects(statsArray, 0).getInt("base_stat"),
                        getObjects(statsArray, 1).getInt("base_stat"),
                        getObjects(statsArray, 2).getInt("base_stat"),
                        getObjects(statsArray, 3).getInt("base_stat"),
                        getObjects(statsArray, 4).getInt("base_stat"),
                        getObjects(statsArray, 5).getInt("base_stat"),
                        pokemon
                );

                // Adiciona as classes PokemonStats e PokemonSpecies à classe Pokemon
                pokemon.setStats(pokemonStats);
                pokemon.setSpecies(pokemonSpecies);

                // Salva novamente ao repositório o conteúdo completo
                pokemonRepository.save(pokemon);
            }
        }
    }

    private static JSONObject getObjects(JSONArray array, int index) {
        return (JSONObject) array.get(index);
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static Mono<String> getJson(WebClient webClient, String uri, MediaType mediaType) {
        return webClient
                .method(HttpMethod.GET)
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"));
    }
}