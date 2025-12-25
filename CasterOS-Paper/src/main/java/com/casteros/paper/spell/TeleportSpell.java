package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class TeleportSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        Location loc = player.getLocation();
        player.teleport(loc.add(loc.getDirection().multiply(5)));
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 40, 0.5, 0.5, 0.5, 0.01);
        player.sendMessage(Component.text("Blink spell cast! Teleported forward.").color(NamedTextColor.LIGHT_PURPLE));
    }

    @Override
    public String getKey() {
        return "teleport";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
