package com.casteros.paper;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
// Use PlayerChatEvent for modern Paper, fallback to AsyncPlayerChatEvent for older versions
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
 // Tracks if player is selecting keybind via action

public class CasterOSPaperPlugin extends JavaPlugin implements Listener {

    private final HashMap<UUID, Boolean> keybindSelection = new HashMap<>();
    // Stores each player's chosen keybind (default: swap hand)
    private final HashMap<UUID, String> playerKeybinds = new HashMap<>();
    // Tracks if player is in magic mode
    private final HashMap<UUID, Boolean> magicMode = new HashMap<>();
    // Stores spells loaded from config
    private Map<String, Map<String, Object>> spells = new HashMap<>();
    public void onEnable() {
        getLogger().info("CasterOS enabled!");
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this, this);
        // Load spells from spells.yml
        File spellsFile = new File(getDataFolder(), "spells.yml");
        if (!spellsFile.exists()) {
            saveResource("spells.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(spellsFile);
        for (String key : config.getKeys(false)) {
            Object value = config.get(key);
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> spell = (Map<String, Object>) value;
                spells.put(key, spell);
            }
        }
        getLogger().info("Loaded spells: " + spells.keySet());
    }

    @Override
    public void onDisable() {
        getLogger().info("CasterOS disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("casteros")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage("§dCasterOS is active! Welcome to the magic system.");
            } else {
                sender.sendMessage("CasterOS command can only be used by players.");
            }
            return true;
        }
        if (label.equalsIgnoreCase("casteroskeybind") || label.equalsIgnoreCase("casteros:keybind")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                keybindSelection.put(player.getUniqueId(), true);
                player.sendMessage("§eCasterOS Keybind Selection: Perform your desired action (swap hand, drop item, sneak, jump, interact) to set your magic keybind.");
            } else {
                sender.sendMessage("CasterOS keybind command can only be used by players.");
            }
            return true;
        }
        return false;
    }

    // Listen for key events to set keybind
    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (keybindSelection.getOrDefault(uuid, false)) {
            playerKeybinds.put(uuid, "Swap Hand Items");
            keybindSelection.put(uuid, false);
            event.getPlayer().sendMessage("§aCasterOS keybind set to: Swap Hand Items!");
            event.setCancelled(true);
            return;
        }
        if ("Swap Hand Items".equals(playerKeybinds.getOrDefault(uuid, "Swap Hand Items (default)"))) {
            if (!magicMode.getOrDefault(uuid, false)) {
                magicMode.put(uuid, true);
                event.getPlayer().sendMessage("§dCasterOS Magic Mode: Type your spell incantation. You cannot move or act until you cast a spell or exit.");
                event.getPlayer().sendActionBar(Component.text("[ CasterOS Magic Mode - Type a spell! ]").color(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
                event.getPlayer().getWorld().spawnParticle(org.bukkit.Particle.CRIT_MAGIC, event.getPlayer().getLocation(), 30, 1, 1, 1, 0.2);
            } else {
                magicMode.put(uuid, false);
                event.getPlayer().sendMessage("§cCasterOS: Magic mode exited. You can move and act again.");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (keybindSelection.getOrDefault(uuid, false)) {
            playerKeybinds.put(uuid, "Drop Item");
            keybindSelection.put(uuid, false);
            event.getPlayer().sendMessage("§aCasterOS keybind set to: Drop Item!");
            event.setCancelled(true);
            return;
        }
        if ("Drop Item".equals(playerKeybinds.getOrDefault(uuid, ""))) {
            if (!magicMode.getOrDefault(uuid, false)) {
                magicMode.put(uuid, true);
                event.getPlayer().sendMessage("§dCasterOS Magic Mode: Type your spell incantation. You cannot move or act until you cast a spell or exit.");
                event.getPlayer().sendActionBar(Component.text("[ CasterOS Magic Mode - Type a spell! ]").color(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
                event.getPlayer().getWorld().spawnParticle(org.bukkit.Particle.CRIT_MAGIC, event.getPlayer().getLocation(), 30, 1, 1, 1, 0.2);
            } else {
                magicMode.put(uuid, false);
                event.getPlayer().sendMessage("§cCasterOS: Magic mode exited. You can move and act again.");
            }
            event.setCancelled(true);
        }
    }

    // Sneak keybind
    @EventHandler
    public void onSneak(org.bukkit.event.player.PlayerToggleSneakEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (keybindSelection.getOrDefault(uuid, false)) {
            playerKeybinds.put(uuid, "Sneak");
            keybindSelection.put(uuid, false);
            event.getPlayer().sendMessage("§aCasterOS keybind set to: Sneak!");
            return;
        }
        if ("Sneak".equals(playerKeybinds.getOrDefault(uuid, "")) && event.isSneaking()) {
            if (!magicMode.getOrDefault(uuid, false)) {
                magicMode.put(uuid, true);
                event.getPlayer().sendMessage("§dCasterOS Magic Mode: Type your spell incantation. You cannot move or act until you cast a spell or exit.");
                event.getPlayer().sendActionBar(Component.text("[ CasterOS Magic Mode - Type a spell! ]").color(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
                event.getPlayer().getWorld().spawnParticle(org.bukkit.Particle.CRIT_MAGIC, event.getPlayer().getLocation(), 30, 1, 1, 1, 0.2);
            } else {
                magicMode.put(uuid, false);
                event.getPlayer().sendMessage("§cCasterOS: Magic mode exited. You can move and act again.");
            }
        }
    }

    // Jump keybind
    // NOTE: PlayerJumpEvent is not available in standard Bukkit/Paper. You may need a custom implementation or plugin for jump detection.

    // Interact keybind
    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (keybindSelection.getOrDefault(uuid, false)) {
            playerKeybinds.put(uuid, "Interact");
            keybindSelection.put(uuid, false);
            event.getPlayer().sendMessage("§aCasterOS keybind set to: Interact!");
            return;
        }
        if ("Interact".equals(playerKeybinds.getOrDefault(uuid, ""))) {
            if (!magicMode.getOrDefault(uuid, false)) {
                magicMode.put(uuid, true);
                event.getPlayer().sendMessage("§dCasterOS Magic Mode: Type your spell incantation. You cannot move or act until you cast a spell or exit.");
                event.getPlayer().sendActionBar(Component.text("[ CasterOS Magic Mode - Type a spell! ]").color(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
                event.getPlayer().getWorld().spawnParticle(org.bukkit.Particle.CRIT_MAGIC, event.getPlayer().getLocation(), 30, 1, 1, 1, 0.2);
            } else {
                magicMode.put(uuid, false);
                event.getPlayer().sendMessage("§cCasterOS: Magic mode exited. You can move and act again.");
            }
        }
    }

    // Use AsyncPlayerChatEvent for chat input (still the only supported way for plugins as of Paper 1.21)
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (magicMode.getOrDefault(uuid, false)) {
            event.setCancelled(true);
            String incantation = event.getMessage().trim().toLowerCase();
            String matchedKey = null;
            String errorCode = null;
            try {
                if (spells == null) {
                    errorCode = "ERR-SPELLS-NOT-LOADED";
                    player.sendMessage(Component.text("[CasterOS Error " + errorCode + "] Spells not loaded. Contact admin.").color(net.kyori.adventure.text.format.NamedTextColor.RED));
                    getLogger().severe("[" + errorCode + "] Spells map is null!");
                    magicMode.put(uuid, false);
                    return;
                }
                // Try direct match
                if (spells.containsKey(incantation)) {
                    matchedKey = incantation;
                } else {
                    // Try alias match (including aliases list in each spell)
                    for (String key : spells.keySet()) {
                        if (key.equalsIgnoreCase(incantation) || key.replace("_", "").equalsIgnoreCase(incantation.replace(" ", ""))) {
                            matchedKey = key;
                            break;
                        }
                        Map<String, Object> spell = spells.get(key);
                        if (spell == null) {
                            errorCode = "ERR-SPELL-NOT-FOUND";
                            getLogger().warning("[" + errorCode + "] Spell key '" + key + "' is null in spells.yml");
                            continue;
                        }
                        Object aliasesObj = spell.get("aliases");
                        if (aliasesObj instanceof java.util.List) {
                            java.util.List<?> aliases = (java.util.List<?>) aliasesObj;
                            for (Object aliasObj : aliases) {
                                if (aliasObj != null) {
                                    String alias = aliasObj.toString().trim().toLowerCase();
                                    if (alias.equals(incantation)) {
                                        matchedKey = key;
                                        break;
                                    }
                                }
                            }
                        }
                        if (matchedKey != null) break;
                    }
                }
                if (matchedKey != null) {
                    Map<String, Object> spell = (Map<String, Object>) spells.get(matchedKey);
                    if (spell == null) {
                        errorCode = "ERR-SPELL-NULL";
                        player.sendMessage(Component.text("[CasterOS Error " + errorCode + "] Spell data missing for '" + matchedKey + "'. Contact admin.").color(net.kyori.adventure.text.format.NamedTextColor.RED));
                        getLogger().severe("[" + errorCode + "] Spell data is null for key: " + matchedKey);
                        magicMode.put(uuid, false);
                        return;
                    }
                    String type = (String) spell.get("type");
                    if (type == null) {
                        errorCode = "ERR-SPELL-NO-TYPE";
                        player.sendMessage(Component.text("[CasterOS Error " + errorCode + "] Spell '" + matchedKey + "' missing type. Contact admin.").color(net.kyori.adventure.text.format.NamedTextColor.RED));
                        getLogger().severe("[" + errorCode + "] Spell '" + matchedKey + "' missing type field.");
                        magicMode.put(uuid, false);
                        return;
                    }
                    // Particle effect logic for spell casting
                    org.bukkit.Location loc = player.getLocation();
                    switch (type) {
                        case "fireball":
                            player.launchProjectile(org.bukkit.entity.Fireball.class, loc.getDirection().multiply(2));
                            player.getWorld().spawnParticle(org.bukkit.Particle.FLAME, loc, 30, 0.5, 0.5, 0.5, 0.01);
                            player.sendMessage(Component.text("Flare spell cast! Fireball launched.").color(net.kyori.adventure.text.format.NamedTextColor.GOLD));
                            break;
                        case "shield":
                            // Use SPELL particle as a visual shield effect for maximum compatibility
                            player.getWorld().spawnParticle(org.bukkit.Particle.SPELL, loc, 20, 0.5, 1, 0.5, 0.01);
                            player.sendMessage(Component.text("Shield spell cast! (effect not implemented)").color(net.kyori.adventure.text.format.NamedTextColor.AQUA));
                            break;
                        case "teleport":
                            player.teleport(loc.add(loc.getDirection().multiply(5)));
                            player.getWorld().spawnParticle(org.bukkit.Particle.END_ROD, player.getLocation(), 40, 0.5, 0.5, 0.5, 0.01);
                            player.sendMessage(Component.text("Blink spell cast! Teleported forward.").color(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
                            break;
                        case "snowball":
                            player.getWorld().spawnParticle(org.bukkit.Particle.SNOWBALL, loc, 25, 0.5, 0.5, 0.5, 0.01);
                            player.sendMessage(Component.text("Frostbolt spell cast! Snowball launched.").color(net.kyori.adventure.text.format.NamedTextColor.AQUA));
                            break;
                        case "lightning":
                            player.getWorld().spawnParticle(org.bukkit.Particle.ELECTRIC_SPARK, loc, 50, 0.5, 0.5, 0.5, 0.01);
                            player.sendMessage(Component.text("Lightning spell cast!").color(net.kyori.adventure.text.format.NamedTextColor.YELLOW));
                            break;
                        case "heal":
                            player.getWorld().spawnParticle(org.bukkit.Particle.HEART, loc, 15, 0.5, 1, 0.5, 0.01);
                            player.sendMessage(Component.text("Heal spell cast!").color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
                            break;
                        case "explosion":
                            player.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_NORMAL, loc, 35, 0.5, 0.5, 0.5, 0.01);
                            player.sendMessage(Component.text("Explosion spell cast!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
                            break;
                        case "levitate":
                            player.getWorld().spawnParticle(org.bukkit.Particle.CLOUD, loc, 20, 0.5, 1, 0.5, 0.01);
                            player.sendMessage(Component.text("Levitate spell cast!").color(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
                            break;
                        case "arcane":
                            player.getWorld().spawnParticle(org.bukkit.Particle.SPELL, loc, 30, 0.5, 0.5, 0.5, 0.01);
                            player.sendMessage(Component.text("Arcane spell cast!").color(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
                            break;
                        default:
                            player.getWorld().spawnParticle(org.bukkit.Particle.CRIT_MAGIC, loc, 10, 0.5, 0.5, 0.5, 0.01);
                            errorCode = "ERR-SPELL-TYPE-NOT-IMPL";
                            player.sendMessage(Component.text("[CasterOS Error " + errorCode + "] Spell type not implemented: " + type).color(net.kyori.adventure.text.format.NamedTextColor.RED));
                            getLogger().warning("[" + errorCode + "] Spell type not implemented: " + type);
                            break;
                    }
                    magicMode.put(uuid, false);
                    player.sendMessage("§aCasterOS: Spell cast! Magic mode exited. You can move and act again.");
                } else {
                    errorCode = "ERR-INCANTATION-NOT-FOUND";
                    player.sendMessage(Component.text("[CasterOS Error " + errorCode + "] Unknown incantation: " + incantation).color(net.kyori.adventure.text.format.NamedTextColor.RED));
                    if (spells != null && !spells.isEmpty()) {
                        StringBuilder sb = new StringBuilder("§7Available spells: ");
                        for (String key : spells.keySet()) {
                            sb.append(key).append(", ");
                        }
                        player.sendMessage(sb.substring(0, sb.length() - 2));
                    }
                    // Remain in magic mode until a valid spell is cast or user exits with keybind
                }
            } catch (Exception ex) {
                errorCode = "ERR-UNEXPECTED";
                player.sendMessage(Component.text("[CasterOS Error " + errorCode + "] Unexpected error. Contact admin.").color(net.kyori.adventure.text.format.NamedTextColor.RED));
                getLogger().severe("[" + errorCode + "] Unexpected error in spell casting: " + ex.getMessage());
                ex.printStackTrace();
                magicMode.put(uuid, false);
            }
        }
    }

    @EventHandler
    public void onPlayerMoveWhileCasting(PlayerMoveEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (magicMode.getOrDefault(uuid, false)) {
            event.setCancelled(true);
            event.getPlayer().sendActionBar(Component.text("§cYou cannot move in magic mode!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onAttackWhileCasting(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            UUID uuid = player.getUniqueId();
            if (magicMode.getOrDefault(uuid, false)) {
                event.setCancelled(true);
                player.sendActionBar(Component.text("§cYou cannot attack in magic mode!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (magicMode.getOrDefault(player.getUniqueId(), false)) {
                event.setCancelled(true);
                player.sendActionBar(Component.text("§cYou cannot open inventory in magic mode!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            }
        }
    }

    @EventHandler
    public void onDropItemWhileCasting(PlayerDropItemEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (magicMode.getOrDefault(uuid, false)) {
            event.setCancelled(true);
            event.getPlayer().sendActionBar(Component.text("§cYou cannot drop items in magic mode!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
        }
    }
}
