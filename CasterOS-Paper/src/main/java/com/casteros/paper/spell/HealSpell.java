package com.casteros.paper.spell;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class HealSpell implements Spell {
    @Override
    public void cast(Player player, Map<String, Object> spellConfig, Plugin plugin) {
        // Restore health (4 hearts)
        double maxHealth = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
        player.setHealth(Math.min(maxHealth, player.getHealth() + 8));
        // Apply regeneration effect for 5 seconds
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1));
        // Visual: spawn heart particles around player
        player.getWorld().spawnParticle(org.bukkit.Particle.HEART, player.getLocation().add(0, 1, 0), 12, 0.7, 0.7, 0.7, 0.1);
        // Play healing sound
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.2f, 1.2f);
        player.sendMessage(Component.text("Heal spell cast! You feel rejuvenated.").color(NamedTextColor.GREEN));
    }

    @Override
    public String getKey() {
        return "heal";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
