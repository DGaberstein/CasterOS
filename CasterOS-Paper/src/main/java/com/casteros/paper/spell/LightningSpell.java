package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class LightningSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        // Strike lightning a few blocks in front of the player
        Location eye = player.getEyeLocation();
        Location target = eye.add(eye.getDirection().multiply(6));
        player.getWorld().strikeLightningEffect(target);
        // Visual: electric spark particles
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target, 50, 0.7, 0.7, 0.7, 0.02);
        // Play thunder sound
        player.getWorld().playSound(target, org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 1.0f);

        // Synergy: double damage if target is 'wet'
        double baseDamage = 8.0;
        boolean synergy = false;
        for (org.bukkit.entity.Entity e : player.getWorld().getNearbyEntities(target, 2, 2, 2)) {
            if (e instanceof org.bukkit.entity.LivingEntity le && le != player) {
                double damage = baseDamage;
                if (StatusEffectManager.hasStatus(le, "wet")) {
                    damage *= 2;
                    synergy = true;
                    StatusEffectManager.clearStatus(le, "wet");
                    le.getWorld().spawnParticle(Particle.WATER_SPLASH, le.getLocation().add(0,1,0), 20, 0.5, 0.5, 0.5, 0.1);
                }
                le.damage(damage, player);
            }
        }
        if (synergy) {
            player.sendMessage(Component.text("Synergy! Wet target: Lightning deals double damage!").color(NamedTextColor.AQUA));
        } else {
            player.sendMessage(Component.text("Lightning spell cast! Lightning strikes!").color(NamedTextColor.YELLOW));
        }
    }

    @Override
    public String getKey() {
        return "lightning";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
