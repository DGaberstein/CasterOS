package com.casteros.paper.spell;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import java.util.Map;


public class FrostNovaSpell implements Spell {
    public String getName() {
        return "Frost Nova";
    }

    @Override
    public String getKey() {
        return "frostnova";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"frost_nova", "frost", "nova"};
    }

    @Override
    public void cast(Player caster, Map<String, Object> context, Plugin plugin) {
        Location center = caster.getLocation();
        double radius = 6.0;
        int slowTicks = 80; // 4 seconds
        int slowLevel = 2;
        double damage = 4.0;
        // Visual effect
        center.getWorld().spawnParticle(Particle.SNOW_SHOVEL, center, 80, radius, 1, radius, 0.2);
        // Affect nearby entities
        for (LivingEntity entity : center.getWorld().getLivingEntities()) {
            if (entity == caster) continue;
            if (entity.getLocation().distance(center) <= radius && entity instanceof Player) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slowTicks, slowLevel));
                entity.damage(damage, caster);
                entity.getWorld().spawnParticle(Particle.SNOWBALL, entity.getLocation().add(0,1,0), 10, 0.3, 0.3, 0.3, 0.1);
            }
        }
        caster.sendMessage("§bFrost Nova unleashed!");
    }
}
