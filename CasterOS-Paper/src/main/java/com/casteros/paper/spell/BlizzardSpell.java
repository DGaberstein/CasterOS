package com.casteros.paper.spell;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Snowball;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Map;
import java.util.Random;


public class BlizzardSpell implements Spell {
    public String getName() {
        return "Blizzard";
    }

    @Override
    public String getKey() {
        return "blizzard";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"iceblizzard", "snowstorm", "storm"};
    }

    @Override
    public void cast(Player caster, Map<String, Object> context, Plugin plugin) {
        Location center = caster.getLocation();
        World world = caster.getWorld();
        int durationTicks = 60; // 3 seconds
        int snowballsPerTick = 6;
        double radius = 5.0;
        Random random = new Random();
        caster.sendMessage("§bBlizzard unleashed!");
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks++ > durationTicks) {
                    cancel();
                    return;
                }
                // Visual snow particles
                world.spawnParticle(Particle.SNOWFLAKE, center, 30, radius, 1, radius, 0.1);
                // Launch snowballs
                for (int i = 0; i < snowballsPerTick; i++) {
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double dist = random.nextDouble() * radius;
                    double x = center.getX() + Math.cos(angle) * dist;
                    double z = center.getZ() + Math.sin(angle) * dist;
                    Location spawnLoc = new Location(world, x, center.getY() + 4, z);
                    Snowball snowball = world.spawn(spawnLoc, Snowball.class);
                    snowball.setVelocity(new org.bukkit.util.Vector(0, -1.2, 0));
                    snowball.setCustomName("Blizzard");
                    snowball.setCustomNameVisible(false);
                }
                // Apply slow to entities in area
                for (LivingEntity entity : world.getLivingEntities()) {
                    if (entity == caster) continue;
                    if (entity.getLocation().distance(center) <= radius) {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
