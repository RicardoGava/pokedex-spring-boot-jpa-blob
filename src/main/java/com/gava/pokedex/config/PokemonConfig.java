package com.gava.pokedex.config;

import com.gava.pokedex.domain.*;
import com.gava.pokedex.domain.enums.Type;
import com.gava.pokedex.repositories.PokemonAbilityRepository;
import com.gava.pokedex.repositories.PokemonImageRepository;
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
import java.util.*;

@Configuration
public class PokemonConfig implements CommandLineRunner {

    @Value("${pokedex-onboarding-limit-number}")
    private int limit;

    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private PokemonAbilityRepository pokemonAbilityRepository;

    @Autowired
    private PokemonImageRepository pokemonImageRepository;

    @Autowired
    private WebClient webClient;

    @Override
    public void run(String... args) throws Exception {

        if (pokemonRepository.findAll().isEmpty()) {
            List<Pokemon> pokemonsList = new ArrayList<>();

            for (int i = 1; i <= limit; i++) {
                Pokemon pokemon = new Pokemon();

                // Captura do JSON do Pokemon
                Mono<String> pokemonMono = getJson(
                        webClient,
                        "https://pokeapi.co/api/v2/pokemon/" + i,
                        MediaType.APPLICATION_JSON
                );
                JSONObject pokemonJsonObj = new JSONObject(pokemonMono.share().block());

                // Adiciona as informações à classe Pokemon
                pokemon.setName(capitalize(pokemonJsonObj.getString("name")));
                pokemon.setHeight(pokemonJsonObj.getInt("height") / 10d);
                pokemon.setWeight(pokemonJsonObj.getInt("weight") / 10d);

                // Adiciona os Types à classe Pokemon
                JSONArray typesArray = pokemonJsonObj.getJSONArray("types");
                int[] types = new int[typesArray.length()];
                for (int j = 0; j < typesArray.length(); j++) {
                    JSONObject jsonTypes = (JSONObject) typesArray.get(j);
                    Type type = Type.valueOf(jsonTypes.getJSONObject("type").getString("name").toUpperCase());
                    types[j] = type.getCode();
                }
                pokemon.setTypes(types);

                // Captura da imagem SVG do Pokemon como String
                Mono<String> svg = getJson(
                        webClient,
                        pokemonJsonObj
                                .getJSONObject("sprites")
                                .getJSONObject("other")
                                .getJSONObject("dream_world")
                                .getString("front_default"),
                        MediaType.ALL
                );
                PokemonImage pokemonImage = new PokemonImage(new SerialBlob(Objects.requireNonNull(svg.share().block())
                        .getBytes(StandardCharsets.UTF_8)));
                pokemonImageRepository.save(pokemonImage);

                // Salva a classe Pokemon para criar um ID no repositório
                pokemonRepository.save(pokemon);

                // Captura do JSON da espécie do Pokemon
                Mono<String> speciesMono = getJson(
                        webClient,
                        "https://pokeapi.co/api/v2/pokemon-species/" + i,
                        MediaType.APPLICATION_JSON
                );
                JSONObject speciesJsonObj = new JSONObject(speciesMono.share().block());

                // Adiciona as informações da espécie à classe Pokemon
                PokemonSpecies pokemonSpecies = new PokemonSpecies();
                pokemonSpecies.setColor(speciesJsonObj.getJSONObject("color").getString("name"));
                pokemonSpecies.setShape(speciesJsonObj.getJSONObject("shape").getString("name"));
                pokemonSpecies.setBaseHappiness(speciesJsonObj.getInt("base_happiness"));
                pokemonSpecies.setBaseExperience(pokemonJsonObj.getInt("base_experience"));
                pokemonSpecies.setCaptureRate(speciesJsonObj.getInt("capture_rate"));
                pokemonSpecies.setIsBaby(speciesJsonObj.getBoolean("is_baby"));
                pokemonSpecies.setIsLegendary(speciesJsonObj.getBoolean("is_legendary"));
                pokemonSpecies.setIsMythical(speciesJsonObj.getBoolean("is_mythical"));
                pokemonSpecies.setGenus(getObjects(speciesJsonObj
                        .getJSONArray("genera"), 7).getString("genus"));

                if (!speciesJsonObj.get("evolves_from_species").equals(null)) {
                    pokemonSpecies.setEvolvesFrom(capitalize(speciesJsonObj
                            .getJSONObject("evolves_from_species").getString("name")));
                }

                // Captura a chave de evolução do Pokemon
                Mono<String> pokemonEvolutionChain = getJson(
                        webClient,
                        speciesJsonObj.getJSONObject("evolution_chain").getString("url"),
                        MediaType.APPLICATION_JSON
                );
                JSONObject evolutionChainJsonObj = new JSONObject(pokemonEvolutionChain.share().block());

                // Procura o nível da chave em que o Pokemon está e retorna o array com as possíveis evoluções
                JSONArray evolutionsArray = searchEvolution(evolutionChainJsonObj.getJSONObject("chain"),
                        pokemonJsonObj.getString("name"));

                // Passa os nomes contidos no array com possíveis evoluções para a classe PokemonSpecies
                pokemonSpecies.setEvolvesTo(getEvolutionsNames(evolutionsArray));

                JSONArray flavorArray = speciesJsonObj.getJSONArray("flavor_text_entries");
                for (int j = flavorArray.length() - 1; j >= 0; j--) {
                    JSONObject jsonFlavor = getObjects(flavorArray, j);
                    if (jsonFlavor.getJSONObject("language").getString("name").equals("en")) {
                        pokemonSpecies.setFlavorText(jsonFlavor.getString("flavor_text"));
                        break;
                    }
                }
                pokemonSpecies.setPokemon(pokemon);

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
                                webClient,
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

    private static JSONArray searchEvolution(JSONObject pokemonEvolutionChain, String pokemonName) {
        if (pokemonEvolutionChain.get("evolves_to").equals(null)) {
            return null;
        } else if (pokemonEvolutionChain.getJSONObject("species").getString("name").equals(pokemonName)) {
            return pokemonEvolutionChain.getJSONArray("evolves_to");
        } else {
            for (Object obj : pokemonEvolutionChain.getJSONArray("evolves_to")) {
                return searchEvolution((JSONObject) obj, pokemonName);
            }
        }
        return null;
    }

    private static Set<String> getEvolutionsNames(JSONArray evolvesToArray) {
        Set<String> evolutions = new HashSet<>();
        if (evolvesToArray != null) {
            for (Object obj : evolvesToArray) {
                JSONObject jsonEvolution = (JSONObject) obj;
                evolutions.add(capitalize(jsonEvolution.getJSONObject("species").getString("name")));
            }
        }
        return evolutions;
    }
}