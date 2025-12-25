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
    // Custom model data value for the magic wand (must match resource pack)
    public static final int MAGIC_WAND_MODEL_DATA = 1234567;
    private SpellRegistry spellRegistry;
    // Map normalized incantation/alias -> spell type (from config)
    private final Map<String, String> incantationToType = new HashMap<>();

    // === Lifecycle ===
    @Override
    public void onEnable() {
        // Register event listeners
        getServer().getPluginManager().registerEvents(this, this);
        // Initialize spell registry
        spellRegistry = new SpellRegistry();
        spellRegistry.registerAllSpells();
        // Load spell config
        reloadSpellConfig();
        getLogger().info("CasterOS enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CasterOS disabled!");
    }

    // === Command Handling ===
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("wand")) {
            if (sender instanceof Player player) {
                player.getInventory().addItem(createMagicWand());
                player.sendMessage(Component.text("You received a Magic Wand!").color(net.kyori.adventure.text.format.NamedTextColor.AQUA));
            } else {
                sender.sendMessage("Players only.");
            }
            return true;
        }
        if (label.equalsIgnoreCase("casteros")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player player) {
                    if (player.isOp()) {
                        reloadSpellConfig();
                        player.sendMessage("§aCasterOS spell config reloaded!");
                    } else {
                        player.sendMessage("§cYou must be OP to reload the spell config.");
                    }
                } else {
                    reloadSpellConfig();
                    sender.sendMessage("§aCasterOS spell config reloaded!");
                }
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
        return false;
    }

    // === Event Handlers ===
    @EventHandler
    public void onItemHeldChange(org.bukkit.event.player.PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (isMagicWand(newItem)) {
            player.sendMessage("§6§l[Spellcasting] §eSpellcasting active! Type a spell name in chat.");
        }
    }

    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (isMagicWand(item)) {
            player.sendMessage("§6§l[Spellcasting] §eSpellcasting active! Type a spell name in chat.");
        }
    }

    @EventHandler
    public void onSwapItems(org.bukkit.event.player.PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        if (isMagicWand(main)) {
            player.sendMessage("§6§l[Spellcasting] §eSpellcasting active! Type a spell name in chat.");
        }
    }

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

    @EventHandler(ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {
        String message = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(event.message());
        handleSpellChat(event.getPlayer(), message, event);
    }

    // === Utility Methods ===
    public static ItemStack createMagicWand() {
        ItemStack wand = new ItemStack(Material.STICK);
        var meta = wand.getItemMeta();
        meta.displayName(Component.text("Magic Wand").color(net.kyori.adventure.text.format.NamedTextColor.AQUA));
        meta.setCustomModelData(MAGIC_WAND_MODEL_DATA);
        wand.setItemMeta(meta);
        return wand;
    }

    private boolean isMagicWand(ItemStack item) {
        if (item == null || item.getType() != Material.STICK) return false;
        if (!item.hasItemMeta()) return false;
        var meta = item.getItemMeta();
        if (meta.hasCustomModelData() && meta.getCustomModelData() == MAGIC_WAND_MODEL_DATA) {
            return true;
        }
        if (meta.hasDisplayName()) {
            // Use displayName() instead of deprecated getDisplayName()
            Component nameComponent = meta.displayName();
            String name = nameComponent != null ? net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(nameComponent) : null;
            return name != null && name.toLowerCase().contains("wand");
        }
        return false;
    }

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

    private void handleSpellChat(Player player, String message, org.bukkit.event.Cancellable event) {
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
}
