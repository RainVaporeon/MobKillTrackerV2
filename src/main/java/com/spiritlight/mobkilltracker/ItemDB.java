package com.spiritlight.mobkilltracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDB {
    public static final Map<String, Tier> itemDB = new HashMap<>();
    public static final List<String> itemMatcher = new ArrayList<>();

    public static Tier getTier(String name) {
        Tier ret = itemDB.get(name);
        return ret == null ? Tier.UNKNOWN : ret;
    }
}
