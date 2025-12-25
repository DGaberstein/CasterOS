package com.casteros.paper.spell;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class LevitateSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        // Apply levitation effect for 5 seconds
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 5, 1));
        // Visual: spawn cloud particles around player
        player.getWorld().spawnParticle(org.bukkit.Particle.CLOUD, player.getLocation().add(0, 1, 0), 20, 0.7, 0.7, 0.7, 0.05);
        // Play levitation sound
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PHANTOM_FLAP, 1.2f, 1.5f);
        player.sendMessage(Component.text("Levitate spell cast! You begin to float.").color(NamedTextColor.LIGHT_PURPLE));
    }

    @Override
    public String getKey() {
        return "levitate";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
