package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class SummonIronGolemSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        Location loc = player.getLocation();
        // Visual: spawn summoning particles
        player.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, loc.add(0, 1, 0), 30, 0.7, 0.7, 0.7, 0.1);
        player.getWorld().spawnParticle(org.bukkit.Particle.CLOUD, loc.add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.01);
        // Play golem sound
        player.getWorld().playSound(loc, org.bukkit.Sound.ENTITY_IRON_GOLEM_STEP, 1.2f, 1.0f);
        // Summon iron golem
        IronGolem golem = player.getWorld().spawn(loc, IronGolem.class);
        golem.setPlayerCreated(true);
        String golemName = player.getName() + "'s Golem";
        golem.setCustomName(golemName);
        golem.setCustomNameVisible(true);
        player.sendMessage(Component.text("Summon Golem spell cast! A mighty golem appears.").color(NamedTextColor.DARK_GRAY));
    }

    @Override
    public String getKey() {
        return "summongolem";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
