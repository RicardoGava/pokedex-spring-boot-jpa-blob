package com.gava.pokedex.domain.enums;

public enum Type {
    NONE(0),
    BUG(1),
    DARK(2),
    DRAGON(3),
    ELECTRIC(4),
    FAIRY(5),
    FIGHTING(6),
    FIRE(7),
    FLYING(8),
    GHOST(9),
    GRASS(10),
    GROUND(11),
    ICE(12),
    NORMAL(13),
    POISON(14),
    PSYCHIC(15),
    ROCK(16),
    STEEL(17),
    WATER(18);

    private final int code;

    Type(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Type valueOf(int code) {
        for (Type value : Type.values()) {
            if (value.getCode() == code)
                return value;
        }
        throw new IllegalArgumentException("Invalid Type code");
    }

    private Type[] weak;
    private Type[] strong;
    private Type[] noEffect;

    static {
        // None to avoid null reference errors
        NONE.weak = new Type[]{};
        NONE.strong = new Type[]{};
        NONE.noEffect = new Type[]{};
        // Bug
        BUG.weak = new Type[]{FLYING, ROCK, FIRE};
        BUG.strong = new Type[]{FIGHTING, GROUND, GRASS};
        BUG.noEffect = new Type[]{};
        // Dark
        DARK.weak = new Type[]{FIGHTING, BUG, FAIRY};
        DARK.strong = new Type[]{GHOST, DARK};
        DARK.noEffect = new Type[]{PSYCHIC};
        // Dragon
        DRAGON.weak = new Type[]{ICE, DRAGON, FAIRY};
        DRAGON.strong = new Type[]{FIRE, WATER, GRASS, ELECTRIC};
        DRAGON.noEffect = new Type[]{};
        // Electric
        ELECTRIC.weak = new Type[]{GROUND};
        ELECTRIC.strong = new Type[]{FLYING, STEEL, ELECTRIC};
        ELECTRIC.noEffect = new Type[]{};
        // Fairy
        FAIRY.weak = new Type[]{POISON, STEEL};
        FAIRY.strong = new Type[]{FIGHTING, BUG, DARK};
        FAIRY.noEffect = new Type[]{DRAGON};
        // Fighting
        FIGHTING.weak = new Type[]{FLYING, PSYCHIC, FAIRY};
        FIGHTING.strong = new Type[]{ROCK, BUG, DARK};
        FIGHTING.noEffect = new Type[]{};
        // Fire
        FIRE.weak = new Type[]{GROUND, ROCK, WATER};
        FIRE.strong = new Type[]{BUG, STEEL, FIRE, GRASS, ICE, FAIRY};
        FIRE.noEffect = new Type[]{};
        // Flying
        FLYING.weak = new Type[]{ROCK, ELECTRIC, ICE};
        FLYING.strong = new Type[]{FIGHTING, BUG, GRASS};
        FLYING.noEffect = new Type[]{GROUND};
        // Ghost
        GHOST.weak = new Type[]{GHOST, DARK};
        GHOST.strong = new Type[]{POISON, BUG};
        GHOST.noEffect = new Type[]{NORMAL, FIGHTING};
        // Grass
        GRASS.weak = new Type[]{FLYING, POISON, BUG, FIRE, ICE};
        GRASS.strong = new Type[]{GROUND, WATER, GRASS, ELECTRIC};
        GRASS.noEffect = new Type[]{};
        // Ground
        GROUND.weak = new Type[]{WATER, GRASS, ICE};
        GROUND.strong = new Type[]{POISON, ROCK};
        GROUND.noEffect = new Type[]{ELECTRIC};
        // Ice
        ICE.weak = new Type[]{FIGHTING, ROCK, STEEL, FIRE};
        ICE.strong = new Type[]{ICE};
        ICE.noEffect = new Type[]{};
        // Normal
        NORMAL.weak = new Type[]{FIGHTING};
        NORMAL.strong = new Type[]{};
        NORMAL.noEffect = new Type[]{GHOST};
        // Poison
        POISON.weak = new Type[]{GROUND, PSYCHIC};
        POISON.strong = new Type[]{FIGHTING, POISON, GRASS, FAIRY};
        POISON.noEffect = new Type[]{};
        // Psychic
        PSYCHIC.weak = new Type[]{BUG, GHOST, DARK};
        PSYCHIC.strong = new Type[]{FIGHTING, PSYCHIC};
        PSYCHIC.noEffect = new Type[]{};
        // Rock
        ROCK.weak = new Type[]{FIGHTING, GROUND, STEEL, WATER, GRASS};
        ROCK.strong = new Type[]{NORMAL, FLYING, POISON, FIRE};
        ROCK.noEffect = new Type[]{};
        // Steel
        STEEL.weak = new Type[]{FIGHTING, GROUND, FIRE};
        STEEL.strong = new Type[]{NORMAL, FLYING, ROCK, BUG, STEEL, GRASS, PSYCHIC, ICE, DRAGON, FAIRY};
        STEEL.noEffect = new Type[]{POISON};
        // Water
        WATER.weak = new Type[]{GRASS, ELECTRIC};
        WATER.strong = new Type[]{STEEL, FIRE, WATER, ICE};
        WATER.noEffect = new Type[]{};
    }
}