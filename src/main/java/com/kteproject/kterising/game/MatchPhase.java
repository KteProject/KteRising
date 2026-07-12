package com.kteproject.kterising.game;

public enum MatchPhase {
    LOBBY,
    GRACE,
    LAVA,
    DEATHMATCH,
    ENDED;

    public boolean isActiveMatch() {
        return this == GRACE || this == LAVA || this == DEATHMATCH;
    }

    public boolean isLavaRising() {
        return this == LAVA || this == DEATHMATCH;
    }
}
