package com.casteros.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CasterPlayer {
    private final UUID uuid;
    private boolean inMagicMode = false;
    private long lastCastTime = 0;
    private String lastSpell = null;
    private final Map<String, Long> spellCooldowns = new HashMap<>();

    public CasterPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isInMagicMode() {
        return inMagicMode;
    }

    public void setInMagicMode(boolean inMagicMode) {
        this.inMagicMode = inMagicMode;
    }

    public long getLastCastTime() {
        return lastCastTime;
    }

    public void setLastCastTime(long lastCastTime) {
        this.lastCastTime = lastCastTime;
    }

    public String getLastSpell() {
        return lastSpell;
    }

    public void setLastSpell(String lastSpell) {
        this.lastSpell = lastSpell;
    }

    public void setSpellCooldown(String spell, long until) {
        spellCooldowns.put(spell, until);
    }

    public boolean isSpellOnCooldown(String spell, long now) {
        return spellCooldowns.getOrDefault(spell, 0L) > now;
    }
}
