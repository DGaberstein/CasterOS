package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class ShieldSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        Location loc = player.getLocation();
        // Visual: spawn spell and barrier particles
        player.getWorld().spawnParticle(Particle.SPELL, loc.add(0, 1, 0), 20, 0.7, 1, 0.7, 0.02);
        // Use CRIT_MAGIC as a fallback for BARRIER for compatibility
        player.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 12, 0.5, 1, 0.5, 0.05);
        // Give absorption (extra hearts) and resistance
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 10, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 1));
        // Play shield sound
        player.getWorld().playSound(loc, org.bukkit.Sound.ITEM_SHIELD_BLOCK, 1.2f, 1.0f);
        player.sendMessage(Component.text("Shield spell cast! You are protected.").color(NamedTextColor.AQUA));
    }

    @Override
    public String getKey() {
        return "shield";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
