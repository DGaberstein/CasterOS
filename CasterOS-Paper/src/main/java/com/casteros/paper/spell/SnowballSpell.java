package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class SnowballSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        Location eye = player.getEyeLocation();
        // Launch a snowball projectile in the direction the player is looking
        player.launchProjectile(Snowball.class, eye.getDirection().multiply(1.5));
        // Visual: spawn snowball and snowflake particles at launch
        player.getWorld().spawnParticle(Particle.SNOWBALL, eye, 20, 0.3, 0.3, 0.3, 0.01);
        player.getWorld().spawnParticle(Particle.SNOWFLAKE, eye, 10, 0.2, 0.2, 0.2, 0.01);
        // Play snowball throw sound
        player.getWorld().playSound(eye, org.bukkit.Sound.ENTITY_SNOW_GOLEM_SHOOT, 1.2f, 1.0f);
        player.sendMessage(Component.text("Frostbolt spell cast! Snowball launched.").color(NamedTextColor.AQUA));
    }

    @Override
    public String getKey() {
        return "snowball";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
