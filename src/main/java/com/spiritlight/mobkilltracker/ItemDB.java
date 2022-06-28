package com.spiritlight.mobkilltracker;

import java.util.HashMap;
import java.util.Map;

public class ItemDB {
    public static final Map<String, Tier> itemDB = new HashMap<>();

    public static Tier getTier(String name) {
        Tier ret = itemDB.get(name);
        return ret == null ? Tier.UNKNOWN : ret;
    }
}
