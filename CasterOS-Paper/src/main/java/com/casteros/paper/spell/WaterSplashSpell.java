package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class WaterSplashSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        Location loc = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(4)).add(0, 1, 0);
        double radius = 3.0;
        int duration = 100; // 5 seconds
        int count = 0;
        for (org.bukkit.entity.Entity e : player.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (e instanceof LivingEntity le && le != player) {
                StatusEffectManager.setStatus(le, "wet", duration);
                le.getWorld().spawnParticle(Particle.WATER_SPLASH, le.getLocation().add(0,1,0), 30, 0.5, 0.5, 0.5, 0.1);
                count++;
            }
        }
        player.getWorld().playSound(loc, Sound.ENTITY_PLAYER_SPLASH, 1.2f, 1.0f);
        player.sendMessage(Component.text("Water Splash spell cast! " + count + " targets are now wet.").color(NamedTextColor.AQUA));
    }

    @Override
    public String getKey() {
        return "watersplash";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"wet", "splash"};
    }
}
