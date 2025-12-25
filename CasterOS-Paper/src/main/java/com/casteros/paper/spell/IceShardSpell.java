package com.casteros.paper.spell;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Collections;
import java.util.Map;


import net.kyori.adventure.text.Component;

public class IceShardSpell implements Spell, Listener {
    public String getName() {
        return "Ice Shard";
    }

    @Override
    public String getKey() {
        return "iceshard";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"ice_shard", "icebolt", "ice"};
    }

    @Override
    public void cast(Player caster, Map<String, Object> context, Plugin plugin) {
        Snowball snowball = caster.launchProjectile(Snowball.class);
        snowball.setVelocity(caster.getLocation().getDirection().multiply(2.2));
        try {
            snowball.customName(Component.text("Ice Shard"));
            snowball.setCustomNameVisible(false);
        } catch (NoSuchMethodError ignored) {
            // Fallback for older API
            snowball.setCustomName("Ice Shard");
            snowball.setCustomNameVisible(false);
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                EntityDamageByEntityEvent.getHandlerList().unregister(IceShardSpell.this);
            }
        }.runTaskLater(plugin, 100L); // Unregister after 5 seconds
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball snowball && event.getEntity() instanceof Player target) {
            String name = null;
            try {
                Component c = snowball.customName();
                if (c != null) name = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(c);
            } catch (NoSuchMethodError ignored) {
                if (snowball.getCustomName() != null) name = snowball.getCustomName();
            }
            if (name != null && name.equals("Ice Shard")) {
                event.setDamage(6.0);
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2)); // 3s slow
            }
        }
    }
}
