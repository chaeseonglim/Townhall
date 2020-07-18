package com.lifejourney.townhall;

public class GameFactor {

    enum Type {
        FARM_DEVELOPMENT_SPEED,
        MARKET_DEVELOPMENT_SPEED,
        DOWNTOWN_DEVELOPMENT_SPEED,
        FORTRESS_DEVELOPMENT_SPEED,
        GOLD_BOOST,
        POP_BOOST,
        DEFENSE_BOOST,
    }

    public GameFactor(Type type, int life, int value) {

        this.type = type;
        this.life = life;
        this.value = value;
    }

    /**
     *
     * @return
     */
    public int getValue() {
        return value;
    }

    /**
     *
     * @return
     */
    public boolean checkExpired() {
        return (--value == 0);
    }

    private Type type;
    private int life;
    private int value;
}
