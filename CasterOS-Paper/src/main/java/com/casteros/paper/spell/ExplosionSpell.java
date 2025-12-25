package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class ExplosionSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        // Create explosion a few blocks in front of the player
        Location eye = player.getEyeLocation();
        Location target = eye.add(eye.getDirection().multiply(4));
        // Visual: spawn explosion and smoke particles
        player.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_HUGE, target, 1);
        player.getWorld().spawnParticle(org.bukkit.Particle.SMOKE_LARGE, target, 30, 1, 1, 1, 0.05);
        // Play explosion sound
        player.getWorld().playSound(target, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1.0f);
        // Create explosion (no block damage, no fire, does not hurt caster)
        player.getWorld().createExplosion(target, 2.5F, false, false, player);
        player.sendMessage(Component.text("Explosion spell cast! Boom!").color(NamedTextColor.RED));
    }

    @Override
    public String getKey() {
        return "explosion";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
