package com.casteros.core;

import java.util.*;

public class IncantationParser {
    public static class SpellMatch {
        public final String spellKey;
        public final String matchedAlias;
        public SpellMatch(String spellKey, String matchedAlias) {
            this.spellKey = spellKey;
            this.matchedAlias = matchedAlias;
        }
    }

    /**
     * Attempts to match an incantation to a spell using aliases.
     * @param incantation The user input (normalized, lowercased)
     * @param spellMap Map of spellKey -> spellData (must contain 'aliases' as List<String> or String)
     * @return SpellMatch if found, null otherwise
     */
    public static SpellMatch matchIncantation(String incantation, Map<String, Map<String, Object>> spellMap) {
        String norm = normalize(incantation);
        for (Map.Entry<String, Map<String, Object>> entry : spellMap.entrySet()) {
            String spellKey = entry.getKey();
            Map<String, Object> data = entry.getValue();
            // Check main key
            if (norm.equals(normalize(spellKey))) {
                return new SpellMatch(spellKey, spellKey);
            }
            // Check aliases
            Object aliasesObj = data.get("aliases");
            if (aliasesObj instanceof List) {
                for (Object alias : (List<?>) aliasesObj) {
                    if (alias != null && norm.equals(normalize(alias.toString()))) {
                        return new SpellMatch(spellKey, alias.toString());
                    }
                }
            } else if (aliasesObj instanceof String) {
                if (norm.equals(normalize((String) aliasesObj))) {
                    return new SpellMatch(spellKey, (String) aliasesObj);
                }
            }
        }
        return null;
    }

    public static String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
