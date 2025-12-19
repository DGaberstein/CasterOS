package com.casteros.core;

import java.util.Map;

public class SpellLogic {
    /**
     * Validates a spell definition from YAML.
     * @param spellKey The spell key
     * @param spellData The spell data map
     * @return null if valid, or error code string
     */
    public static String validateSpell(String spellKey, Map<String, Object> spellData) {
        if (spellData == null) return "ERR_SPELL_MISSING_DATA";
        if (!spellData.containsKey("type")) return "ERR_SPELL_MISSING_TYPE";
        // Optionally check for required fields per type
        return null;
    }
}
