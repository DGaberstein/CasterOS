package com.casteros.paper.spell;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.Map;

public interface Spell {
    /**
     * Cast the spell for the given player, using spell config.
     * @param player The player casting the spell
     * @param spellConfig The spell config map from spells.yml
     * @param plugin The plugin instance
     */
    void cast(Player player, Map<String, Object> spellConfig, Plugin plugin);

    /**
     * @return The main incantation key for this spell (normalized)
     */
    String getKey();

    /**
     * @return All aliases for this spell (normalized)
     */
    String[] getAliases();
}
