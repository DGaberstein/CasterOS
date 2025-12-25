package com.casteros.paper.spell;

// import org.bukkit.plugin.Plugin;
import java.util.HashMap;
import java.util.Map;

public class SpellRegistry {
    private final Map<String, Spell> spellRegistry = new HashMap<>();
    // private final Plugin plugin;

    public SpellRegistry() {
        // this.plugin = plugin;
    }

    public void registerSpell(Spell spell) {
        spellRegistry.put(spell.getKey(), spell);
        for (String alias : spell.getAliases()) {
            spellRegistry.put(alias, spell);
        }
    }

    public Spell getSpell(String key) {
        return spellRegistry.get(key);
    }

    public boolean hasSpell(String key) {
        return spellRegistry.containsKey(key);
    }

    public void registerAllSpells() {
        registerSpell(new FireballSpell());
        registerSpell(new ShieldSpell());
        registerSpell(new TeleportSpell());
        registerSpell(new SnowballSpell());
        registerSpell(new LightningSpell());
        registerSpell(new HealSpell());
        registerSpell(new ExplosionSpell());
        registerSpell(new LevitateSpell());
        registerSpell(new SummonWolfSpell());
        registerSpell(new SummonCatSpell());
        registerSpell(new SummonIronGolemSpell());
        registerSpell(new FirewallSpell());
        registerSpell(new ChainLightningSpell());
        registerSpell(new VoidPullSpell());
        registerSpell(new WaterSplashSpell());
        registerSpell(new IceShardSpell());
        registerSpell(new FrostNovaSpell());
        registerSpell(new FreezeSpell());
        registerSpell(new BlizzardSpell());
        // Register more spells here as you modularize them
    }
}
