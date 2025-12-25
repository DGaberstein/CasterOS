package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Fireball;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class FireballSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        Location loc = player.getEyeLocation();
        // Synergy: Combustion (if player has 'wind' status, fireball is faster)
        double speed = 1.5;
        if (StatusEffectManager.hasStatus(player, "wind")) {
            speed = 3.0;
            StatusEffectManager.clearStatus(player, "wind");
            player.sendMessage(Component.text("Synergy! Combustion: Fireball travels twice as fast!").color(NamedTextColor.GOLD));
        }
        // Launch a fireball projectile in the direction the player is looking
        Fireball fireball = player.launchProjectile(Fireball.class, loc.getDirection().multiply(speed));
        fireball.setIsIncendiary(true);
        fireball.setYield(2.5f); // explosion power
        // Visual: spawn flame and lava particles at launch
        player.getWorld().spawnParticle(Particle.FLAME, loc, 30, 0.3, 0.3, 0.3, 0.01);
        player.getWorld().spawnParticle(Particle.LAVA, loc, 10, 0.2, 0.2, 0.2, 0.01);
        // Play fireball launch sound
        player.getWorld().playSound(loc, org.bukkit.Sound.ENTITY_BLAZE_SHOOT, 1.2f, 1.0f);
        player.sendMessage(Component.text("Flare spell cast! Fireball launched.").color(NamedTextColor.GOLD));
    }

    @Override
    public String getKey() {
        return "fireball";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
