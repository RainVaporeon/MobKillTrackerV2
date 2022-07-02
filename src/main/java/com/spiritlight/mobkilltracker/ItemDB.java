package com.spiritlight.mobkilltracker;

import java.util.HashMap;
import java.util.Map;

public class ItemDB {
    public static final Map<String, Tier> itemDB = new HashMap<>();

    /**
     * Gets the {@link Tier} of attached name.<br><br>
     * If the {@code name} field is null or not in {@link ItemDB#itemDB}, return {@link Tier#UNKNOWN}.
     *
     * @param name The name to get tier from {@link ItemDB#itemDB}
     * @return The {@link Tier} of this item (From {@link ItemDB#itemDB}
     */
    public static Tier getTier(String name) {
        Tier ret = itemDB.get(name);
        return ret == null ? Tier.UNKNOWN : ret;
    }
}
