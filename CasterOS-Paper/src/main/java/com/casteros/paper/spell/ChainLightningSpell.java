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
import java.util.*;

public class ChainLightningSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        World world = player.getWorld();
        Location start = player.getEyeLocation();
        double range = 16.0;
        int maxJumps = 4;
        double damage = 8.0; // 4 hearts
        double falloff = 0.7; // Each jump deals 70% of previous
        Set<LivingEntity> hit = new HashSet<>();
        LivingEntity current = findNearestTarget(player, start, range, hit);
        boolean synergy = false;
        if (current == null) {
            player.sendMessage(Component.text("No valid target for Chain Lightning!").color(NamedTextColor.GRAY));
            return;
        }
        hit.add(current);
        Location prev = start;
        for (int i = 0; i < maxJumps && current != null; i++) {
            // Visual: lightning and electric spark
            world.strikeLightningEffect(current.getLocation());
            world.spawnParticle(Particle.ELECTRIC_SPARK, current.getLocation().add(0,1,0), 30, 0.5, 0.5, 0.5, 0.05);
            world.playSound(current.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.7f, 1.2f);
            // Synergy: double damage if 'wet'
            double actualDamage = damage;
            if (StatusEffectManager.hasStatus(current, "wet")) {
                actualDamage *= 2;
                synergy = true;
                StatusEffectManager.clearStatus(current, "wet");
                world.spawnParticle(Particle.WATER_SPLASH, current.getLocation().add(0,1,0), 20, 0.5, 0.5, 0.5, 0.1);
            }
            // Damage and brief stun (slowness)
            current.damage(actualDamage, player);
            current.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOW, 20, 4));
            // Arc particle
            drawArc(world, prev, current.getLocation());
            // Find next
            prev = current.getLocation();
            damage *= falloff;
            current = findNearestTarget(player, prev, range, hit);
            if (current != null) hit.add(current);
        }
        if (synergy) {
            player.sendMessage(Component.text("Synergy! Wet target: Chain Lightning deals double damage!").color(NamedTextColor.AQUA));
        } else {
            player.sendMessage(Component.text("Chain Lightning arcs between foes!").color(NamedTextColor.YELLOW));
        }
    }

    private LivingEntity findNearestTarget(Player caster, Location from, double range, Set<LivingEntity> exclude) {
        LivingEntity nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Entity e : from.getWorld().getNearbyEntities(from, range, range, range)) {
            if (e instanceof LivingEntity le && le != caster && !exclude.contains(le) && !le.isDead() && le.getLocation().distance(from) < minDist) {
                minDist = le.getLocation().distance(from);
                nearest = le;
            }
        }
        return nearest;
    }

    private void drawArc(World world, Location from, Location to) {
        Vector dir = to.toVector().subtract(from.toVector());
        int steps = 10;
        for (int i = 1; i < steps; i++) {
            Location point = from.clone().add(dir.clone().multiply(i/(double)steps));
            world.spawnParticle(Particle.CRIT_MAGIC, point, 2, 0.05, 0.05, 0.05, 0.01);
        }
    }

    @Override
    public String getKey() {
        return "chainlightning";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"chain", "arc"};
    }
}
