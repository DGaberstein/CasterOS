package com.casteros.paper;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import java.util.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import com.casteros.paper.spell.SpellRegistry;
import com.casteros.paper.spell.Spell;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class CasterOSPaperPlugin extends JavaPlugin implements Listener {
    // Track players in spell mode (by UUID)
    private final Set<UUID> spellModePlayers = new HashSet<>();
    private SpellRegistry spellRegistry;
    // Map normalized incantation/alias -> spell type (from config)
    private final Map<String, String> incantationToType = new HashMap<>();

    // Utility: is the item a magic wand? (named stick)
    private boolean isMagicWand(ItemStack item) {
        if (item == null || item.getType() != Material.STICK) return false;
        if (!item.hasItemMeta()) return false;
        var meta = item.getItemMeta();
        // Try to use Adventure Component API if available
        try {
            java.lang.reflect.Method m = meta.getClass().getMethod("displayName");
            Object comp = m.invoke(meta);
            if (comp != null) {
                String plain = comp.toString().toLowerCase();
                return plain.contains("wand");
            }
        } catch (Exception ignore) {}
        // Fallback to deprecated getDisplayName()
        if (meta.hasDisplayName()) {
            @SuppressWarnings("deprecation")
            String name = meta.getDisplayName();
            return name != null && name.toLowerCase().contains("wand");
        }
        return false;
    }

    // Block opening inventory when holding a magic wand
    @EventHandler
    public void onInventoryOpen(org.bukkit.event.inventory.InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (isMagicWand(player.getInventory().getItemInMainHand())) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot open your inventory while holding your magic wand!");
            }
        }
    }

    @Override
    public void onEnable() {
        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
        // Register spells
        spellRegistry = new SpellRegistry();
        spellRegistry.registerAllSpells();

        // Load spells.yml from plugin data folder, copy default if missing
        try {
            File configFile = new File(getDataFolder(), "spells.yml");
            if (!configFile.exists()) {
                getDataFolder().mkdirs();
                try (java.io.InputStream in = getResource("spells.yml")) {
                    if (in != null) {
                        java.nio.file.Files.copy(in, configFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        getLogger().info("Default spells.yml copied to data folder.");
                    }
                }
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            for (String key : config.getKeys(false)) {
                String normalized = key.toLowerCase().replace("_", "").replace(" ", "");
                String type = config.getString(key + ".type");
                if (type != null) {
                    incantationToType.put(normalized, type.toLowerCase());
                    // Add aliases
                    List<String> aliases = config.getStringList(key + ".aliases");
                    if (aliases != null) {
                        for (String alias : aliases) {
                            String normAlias = alias.toLowerCase().replace("_", "").replace(" ", "");
                            incantationToType.put(normAlias, type.toLowerCase());
                        }
                    }
                }
            }
            getLogger().info("Loaded spell incantations and aliases from spells.yml");
        } catch (Exception e) {
            getLogger().warning("Failed to load spells.yml for incantations: " + e.getMessage());
        }
        getLogger().info("CasterOS enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CasterOS disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("casteros")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadSpellConfig();
                sender.sendMessage("§aCasterOS spell config reloaded!");
                return true;
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage("§dCasterOS is active! Right-click with a stick to cast a spell.");
            } else {
                sender.sendMessage("CasterOS command can only be used by players.");
            }
            return true;
        }
        if (label.equalsIgnoreCase("casteroskeybind") || label.equalsIgnoreCase("casteros:keybind")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage("§eKeybind selection is disabled. Use a stick to cast spells.");
            } else {
                sender.sendMessage("CasterOS keybind command can only be used by players.");
            }
            return true;
        }
        return false;
    }

    // Reload spells.yml and rebuild incantation/alias map
    public void reloadSpellConfig() {
        incantationToType.clear();
        try {
            File configFile = new File(getDataFolder(), "spells.yml");
            if (!configFile.exists()) {
                getDataFolder().mkdirs();
                try (java.io.InputStream in = getResource("spells.yml")) {
                    if (in != null) {
                        java.nio.file.Files.copy(in, configFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        getLogger().info("Default spells.yml copied to data folder.");
                    }
                }
            }
            org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(configFile);
            for (String key : config.getKeys(false)) {
                String normalized = key.toLowerCase().replace("_", "").replace(" ", "");
                String type = config.getString(key + ".type");
                if (type != null) {
                    incantationToType.put(normalized, type.toLowerCase());
                    // Add aliases
                    java.util.List<String> aliases = config.getStringList(key + ".aliases");
                    if (aliases != null) {
                        for (String alias : aliases) {
                            String normAlias = alias.toLowerCase().replace("_", "").replace(" ", "");
                            incantationToType.put(normAlias, type.toLowerCase());
                        }
                    }
                }
            }
            getLogger().info("[CasterOS] Spell incantations and aliases reloaded from spells.yml");
        } catch (Exception e) {
            getLogger().warning("Failed to reload spells.yml for incantations: " + e.getMessage());
        }
    }



    // Enable/disable spell mode when switching hotbar slot
    @EventHandler
    public void onItemHeldChange(org.bukkit.event.player.PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (isMagicWand(newItem)) {
            if (spellModePlayers.add(player.getUniqueId())) {
                player.sendMessage("§6§l[Spellcasting] §eSpell mode enabled! Type a spell name in chat.");
            }
        } else {
            if (spellModePlayers.remove(player.getUniqueId())) {
                player.sendMessage("§7Spell mode disabled.");
            }
        }
    }


    // Enable spell mode on join if holding wand
    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (isMagicWand(item)) {
            spellModePlayers.add(player.getUniqueId());
            player.sendMessage("§6§l[Spellcasting] §eSpell mode enabled! Type a spell name in chat.");
        }
    }


    // Update spell mode on offhand/mainhand swap
    @EventHandler
    public void onSwapItems(org.bukkit.event.player.PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        if (isMagicWand(main)) {
            if (spellModePlayers.add(player.getUniqueId())) {
                player.sendMessage("§6§l[Spellcasting] §eSpell mode enabled! Type a spell name in chat.");
            }
        } else {
            if (spellModePlayers.remove(player.getUniqueId())) {
                player.sendMessage("§7Spell mode disabled.");
            }
        }
    }



    // Intercept chat for spell casting (dynamic, config-driven) - Paper 1.19+ AsyncChatEvent
    @EventHandler(ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {
        // Extract plain text message from Adventure Component (cross-version safe)
        String message = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(event.message());
        handleSpellChat(event.getPlayer(), message, event);
    }

    // Shared chat handler for both events
    private void handleSpellChat(Player player, String message, org.bukkit.event.Cancellable event) {
        if (!spellModePlayers.contains(player.getUniqueId())) {
            getLogger().info("[DEBUG] Player " + player.getName() + " is not in spell mode.");
            return;
        }
        ItemStack wand = player.getInventory().getItemInMainHand();
        if (!isMagicWand(wand)) {
            getLogger().info("[DEBUG] Player " + player.getName() + " is not holding a wand.");
            return;
        }
        event.setCancelled(true);
        String incantation = message.trim().toLowerCase().replace("_", "").replace(" ", "");
        getLogger().info("[DEBUG] Player " + player.getName() + " cast attempt: '" + incantation + "'");
        String type = incantationToType.get(incantation);
        getLogger().info("[DEBUG] Lookup type for incantation: '" + incantation + "' => '" + type + "'");
        if (type != null) {
            Spell spell = spellRegistry.getSpell(type);
            getLogger().info("[DEBUG] SpellRegistry lookup for type: '" + type + "' => " + (spell != null ? spell.getClass().getSimpleName() : "null"));
            if (spell != null) {
                // Run spell casting on main thread
                Bukkit.getScheduler().runTask(this, () -> {
                    try {
                        spell.cast(player, Collections.emptyMap(), this);
                        getLogger().info("[DEBUG] Spell '" + type + "' cast successfully for player " + player.getName());
                    } catch (Exception ex) {
                        getLogger().warning("[DEBUG] Exception during spell cast: " + ex.getMessage());
                        ex.printStackTrace();
                        player.sendMessage(Component.text("Error casting spell: " + ex.getMessage()).color(net.kyori.adventure.text.format.NamedTextColor.RED));
                    }
                });
                return;
            } else {
                getLogger().warning("[DEBUG] No Spell implementation found for type: '" + type + "'");
            }
        } else {
            getLogger().info("[DEBUG] No spell type found for incantation: '" + incantation + "'");
        }
        player.sendMessage(Component.text("Unknown spell: '" + message.trim() + "'.").color(net.kyori.adventure.text.format.NamedTextColor.RED));
    }

    // All magic mode and interruption handlers removed (not needed for stick-only casting)
}
