package com.casteros.paper.spell;

import org.bukkit.Location;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class SummonCatSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        Location loc = player.getLocation();
        // Visual: spawn summoning particles
        player.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, loc.add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().spawnParticle(org.bukkit.Particle.SPELL, loc.add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.01);
        // Play cat sound
        player.getWorld().playSound(loc, org.bukkit.Sound.ENTITY_CAT_AMBIENT, 1.2f, 1.0f);
        // Summon cat
        Cat cat = player.getWorld().spawn(loc, Cat.class);
        cat.setOwner(player);
        String catName = player.getName() + "'s Cat";
        cat.setCustomName(catName);
        cat.setCustomNameVisible(true);
        player.sendMessage(Component.text("Summon Cat spell cast! A magical cat appears.").color(NamedTextColor.GOLD));
    }

    @Override
    public String getKey() {
        return "summoncat";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
