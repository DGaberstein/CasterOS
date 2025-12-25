package com.casteros.paper.spell;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;
import org.bukkit.Bukkit;
import java.util.Map;


public class FreezeSpell implements Spell {
    public String getName() {
        return "Freeze";
    }

    @Override
    public String getKey() {
        return "freeze";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"icefreeze", "frostbite"};
    }

    @Override
    public void cast(Player caster, Map<String, Object> context, Plugin plugin) {
        // Find nearest player (not self) within 10 blocks
        Player target = null;
        double minDist = 10.0;
        for (Player p : caster.getWorld().getPlayers()) {
            if (p == caster) continue;
            double dist = p.getLocation().distance(caster.getLocation());
            if (dist < minDist) {
                minDist = dist;
                target = p;
            }
        }
        if (target != null) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 255)); // Max slow for 4s
            target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 80, 128)); // No jump
            target.getWorld().spawnParticle(Particle.SNOWFLAKE, target.getLocation().add(0,1,0), 30, 0.5, 0.5, 0.5, 0.1);
            target.sendMessage("§bYou have been frozen!");
            caster.sendMessage("§bFreeze spell cast on " + target.getName() + "!");
        } else {
            caster.sendMessage("§7No target found for Freeze spell.");
        }
    }
}
