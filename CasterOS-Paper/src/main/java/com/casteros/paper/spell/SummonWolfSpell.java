package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class SummonWolfSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        Location loc = player.getLocation();
        // Visual: spawn summoning particles
        player.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, loc.add(0, 1, 0), 30, 0.7, 0.7, 0.7, 0.1);
        player.getWorld().spawnParticle(org.bukkit.Particle.CAMPFIRE_COSY_SMOKE, loc.add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.01);
        // Play summoning sound
        player.getWorld().playSound(loc, org.bukkit.Sound.ENTITY_WOLF_HOWL, 1.2f, 1.0f);
        // Summon wolf
        Wolf wolf = player.getWorld().spawn(loc, Wolf.class);
        wolf.setOwner(player);
        // Use legacy setCustomName(String) for maximum compatibility, suppressing deprecation warning
        @SuppressWarnings("deprecation")
        String wolfName = "Spellbound Wolf";
        wolf.setCustomName(wolfName);
        wolf.setCustomNameVisible(true);
        player.sendMessage(Component.text("Summon Wolf spell cast! A loyal wolf appears.").color(NamedTextColor.GRAY));
    }

    @Override
    public String getKey() {
        return "summonwolf";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
