package com.spiritlight.mobkilltracker;

import net.minecraft.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ItemDB {
    public static final Map<String, Tier> itemDB = new HashMap<>();
    public static final List<String> itemMatcher = new ArrayList<>();
    private static final Pattern CHAR_FILTER = Pattern.compile("[^A-Za-z0-9 ]");

    public static Tier getTier(String name) {
        Tier ret = itemDB.get(name);
        return ret == null ? Tier.UNKNOWN : ret;
    }

    //~ Extra note needed: This is pretty expensive to call.
    public static Tier getTierAnyMatch(String anyName) {
        final String ITEM_NAME = CHAR_FILTER.matcher(StringUtils.stripControlCodes(anyName)).replaceAll("");
        for(String s : itemMatcher) {
            if(ITEM_NAME.contains(s)) return getTier(s);
        }
        return Tier.UNKNOWN;
    }
}
