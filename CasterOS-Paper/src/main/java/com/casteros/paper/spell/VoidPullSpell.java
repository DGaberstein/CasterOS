package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VoidPullSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        World world = player.getWorld();
        Location center = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(5)).add(0, 1, 0);
        double radius = 6.0;
        int durationTicks = 80; // 4 seconds
        int explosionPower = 3;
        Set<LivingEntity> affected = new HashSet<>();
        // Visual: black hole
        world.spawnParticle(Particle.PORTAL, center, 100, 1.5, 1.5, 1.5, 0.2);
        world.playSound(center, Sound.ENTITY_ENDERMAN_SCREAM, 1.5f, 0.5f);
        // Schedule pull effect
        new org.bukkit.scheduler.BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks++ > durationTicks) {
                    // Final explosion
                    world.createExplosion(center, explosionPower, false, false, player);
                    world.spawnParticle(Particle.EXPLOSION_LARGE, center, 1, 0, 0, 0, 0);
                    world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.7f);
                    cancel();
                    return;
                }
                // Pull entities
                for (Entity e : world.getNearbyEntities(center, radius, radius, radius)) {
                    if (e instanceof LivingEntity le && le != player) {
                        Vector pull = center.toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.5);
                        le.setVelocity(le.getVelocity().add(pull));
                        affected.add(le);
                        // Black hole particles
                        world.spawnParticle(Particle.SMOKE_LARGE, le.getLocation().add(0,1,0), 2, 0.2, 0.2, 0.2, 0.01);
                    }
                }
                // Black hole swirl
                world.spawnParticle(Particle.PORTAL, center, 30, 1.2, 1.2, 1.2, 0.1);
            }
        }.runTaskTimer(plugin, 0, 4);
        player.sendMessage(Component.text("Void Pull spell cast! Entities are drawn in... BOOM!").color(NamedTextColor.DARK_PURPLE));
    }

    @Override
    public String getKey() {
        return "voidpull";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"blackhole", "pull"};
    }
}
