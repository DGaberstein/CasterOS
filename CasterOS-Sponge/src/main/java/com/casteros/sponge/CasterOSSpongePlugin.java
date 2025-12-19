package com.casteros.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.message.PlayerChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.io.InputStream;
import com.casteros.core.CasterPlayer;
import com.casteros.core.IncantationParser;
import com.casteros.core.SpellLogic;
import org.yaml.snakeyaml.Yaml;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Plugin metadata is now in META-INF/plugins/casteros.plugin.yml for SpongeAPI 8+
public class CasterOSSpongePlugin {
    private Map<String, Map<String, Object>> spellMap = new HashMap<>();
    private final Map<UUID, CasterPlayer> playerData = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> magicMode = new ConcurrentHashMap<>();
    private boolean spellsLoaded = false;
    private String lastConfigError = null;


    // Call this method from your plugin entrypoint (see plugin.yml)
    public void onInitialize() {
        loadSpells();
        // Register listeners with plugin container
        Sponge.pluginManager().fromInstance(this).ifPresent(container ->
            Sponge.eventManager().registerListeners(container, this)
        );
    }

    private void loadSpells() {
        spellsLoaded = false;
        lastConfigError = null;
        try {
            Path configPath = Sponge.pluginManager().fromInstance(this)
                .map(container -> Sponge.configManager().pluginConfig(container).directory().resolve("spells.yml"))
                .orElseThrow(() -> new IllegalStateException("Plugin container not found for config!"));
            if (!Files.exists(configPath)) {
                // Copy default from jar
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("spells.yml")) {
                    if (in != null) {
                        Files.createDirectories(configPath.getParent());
                        Files.copy(in, configPath);
                    }
                }
            }
            Yaml yaml = new Yaml();
            Object data = yaml.load(Files.newInputStream(configPath));
            if (!(data instanceof Map)) {
                lastConfigError = "ERR_YAML_INVALID_FORMAT";
                System.err.println("[CasterOS] spells.yml invalid format! Error: ERR_YAML_INVALID_FORMAT");
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> loaded = (Map<String, Map<String, Object>>) data;
            spellMap = loaded;
            for (Map.Entry<String, Map<String, Object>> entry : spellMap.entrySet()) {
                String err = SpellLogic.validateSpell(entry.getKey(), entry.getValue());
                if (err != null) {
                    lastConfigError = err;
                    System.err.println("[CasterOS] Spell '" + entry.getKey() + "' invalid: " + err);
                }
            }
            spellsLoaded = true;
        } catch (Exception e) {
            lastConfigError = "ERR_YAML_EXCEPTION";
            System.err.println("[CasterOS] Exception loading spells.yml: " + e.getMessage() + " Error: ERR_YAML_EXCEPTION");
        }
    }

    private CasterPlayer getCasterPlayer(ServerPlayer player) {
        return playerData.computeIfAbsent(player.uniqueId(), CasterPlayer::new);
    }

    @Listener
    public void onPlayerChat(PlayerChatEvent event) {
        ServerPlayer player = event.cause().first(ServerPlayer.class).orElse(null);
        if (player == null) return;
        CasterPlayer cp = getCasterPlayer(player);
        if (!magicMode.getOrDefault(player.uniqueId(), false)) return;
        event.setCancelled(true);
        if (!spellsLoaded) {
            player.sendMessage(Component.text("[CasterOS] Spells not loaded: " + (lastConfigError != null ? lastConfigError : "Unknown error"), NamedTextColor.RED));
            System.err.println("[CasterOS] Player attempted to cast while spells not loaded. Error: " + lastConfigError);
            return;
        }
        String incantation = PlainTextComponentSerializer.plainText().serialize(event.message());
        IncantationParser.SpellMatch match = IncantationParser.matchIncantation(incantation, spellMap);
        if (match == null) {
            player.sendMessage(Component.text("[CasterOS] Unknown spell or alias. Error: ERR_SPELL_NOT_FOUND", NamedTextColor.RED));
            System.out.println("[CasterOS] Player '" + player.name() + "' failed to cast: '" + incantation + "' (ERR_SPELL_NOT_FOUND)");
            return;
        }
        Map<String, Object> spellData = spellMap.get(match.spellKey);
        String err = SpellLogic.validateSpell(match.spellKey, spellData);
        if (err != null) {
            player.sendMessage(Component.text("[CasterOS] Spell invalid: " + err, NamedTextColor.RED));
            System.err.println("[CasterOS] Player '" + player.name() + "' tried invalid spell: '" + match.spellKey + "' (" + err + ")");
            return;
        }
        // ...spell effect logic (to be implemented per Sponge API)...
        player.sendMessage(Component.text("[CasterOS] Cast spell: " + match.spellKey + " (alias: " + match.matchedAlias + ")", NamedTextColor.GREEN));
        System.out.println("[CasterOS] Player '" + player.name() + "' cast spell: '" + match.spellKey + "' (alias: " + match.matchedAlias + ")");
        cp.setLastSpell(match.spellKey);
        cp.setLastCastTime(System.currentTimeMillis());
        magicMode.put(player.uniqueId(), false);
    }

    @Listener
    public void onPlayerMove(MoveEntityEvent event) {
        if (!(event.entity() instanceof ServerPlayer)) return;
        ServerPlayer player = (ServerPlayer) event.entity();
        if (magicMode.getOrDefault(player.uniqueId(), false)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You cannot move in magic mode!", NamedTextColor.RED));
        }
    }

    @Listener
    public void onPlayerSneak(ChangeDataHolderEvent.ValueChange event) {
        if (!(event.targetHolder() instanceof ServerPlayer)) return;
        ServerPlayer player = (ServerPlayer) event.targetHolder();

        Optional<Boolean> after = player.get(Keys.IS_SNEAKING);
        if (!after.isPresent()) return;
        boolean isSneaking = after.get();

        if (!magicMode.getOrDefault(player.uniqueId(), false) && isSneaking) {
            magicMode.put(player.uniqueId(), true);
            player.sendMessage(Component.text("[CasterOS] Magic Mode: Type your spell incantation. You cannot move or act until you cast a spell or exit.", NamedTextColor.LIGHT_PURPLE));
        } else if (magicMode.getOrDefault(player.uniqueId(), false) && !isSneaking) {
            magicMode.put(player.uniqueId(), false);
            player.sendMessage(Component.text("[CasterOS] Magic mode exited. You can move and act again.", NamedTextColor.RED));
        }
    }
}
