package com.casteros.paper.spell;

import org.bukkit.entity.LivingEntity;
import java.util.*;

public class StatusEffectManager {
    private static final Map<UUID, Map<String, Long>> statusMap = new HashMap<>();

    public static void setStatus(LivingEntity entity, String status, long durationTicks) {
        statusMap.computeIfAbsent(entity.getUniqueId(), k -> new HashMap<>())
                 .put(status, System.currentTimeMillis() + (durationTicks * 50));
    }

    public static boolean hasStatus(LivingEntity entity, String status) {
        Map<String, Long> statuses = statusMap.get(entity.getUniqueId());
        if (statuses == null) return false;
        Long expire = statuses.get(status);
        if (expire == null || expire < System.currentTimeMillis()) {
            if (expire != null) statuses.remove(status);
            return false;
        }
        return true;
    }

    public static void clearStatus(LivingEntity entity, String status) {
        Map<String, Long> statuses = statusMap.get(entity.getUniqueId());
        if (statuses != null) statuses.remove(status);
    }
}
