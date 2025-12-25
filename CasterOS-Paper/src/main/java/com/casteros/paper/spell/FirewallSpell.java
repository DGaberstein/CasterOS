package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class FirewallSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        Location start = player.getLocation().add(0, 1, 0);
        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        World world = player.getWorld();
        int length = 7;
        int height = 3;
        double spacing = 1.0;
        // Synergy: Combustion (if player has 'wind' status, wall is larger)
        if (StatusEffectManager.hasStatus(player, "wind")) {
            length = 13;
            height = 5;
            StatusEffectManager.clearStatus(player, "wind");
            player.sendMessage(Component.text("Synergy! Combustion: Firewall is larger!").color(NamedTextColor.GOLD));
        }
        // Center the wall in front of the player
        Vector perp = new Vector(-direction.getZ(), 0, direction.getX());
        Location wallCenter = start.clone().add(direction.clone().multiply(3));
        // Create the wall
        for (int i = -length/2; i <= length/2; i++) {
            for (int j = 0; j < height; j++) {
                Location blockLoc = wallCenter.clone().add(perp.clone().multiply(i * spacing)).add(0, j, 0);
                // Visual: flame and smoke
                world.spawnParticle(Particle.FLAME, blockLoc, 8, 0.15, 0.1, 0.15, 0.01);
                world.spawnParticle(Particle.SMOKE_LARGE, blockLoc, 2, 0.1, 0.05, 0.1, 0.01);
                // Area effect: set fire to nearby blocks (if air)
                Location below = blockLoc.clone().add(0, -1, 0);
                if (below.getBlock().getType().isAir()) {
                    below.getBlock().setType(org.bukkit.Material.FIRE);
                }
            }
        }
        // Play fire sound
        world.playSound(wallCenter, Sound.ITEM_FIRECHARGE_USE, 1.5f, 1.0f);
        player.sendMessage(Component.text("Firewall spell cast! A blazing wall erupts.").color(NamedTextColor.GOLD));
    }

    @Override
    public String getKey() {
        return "firewall";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
