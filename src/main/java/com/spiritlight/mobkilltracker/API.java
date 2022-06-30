package com.spiritlight.mobkilltracker;

import com.google.gson.*;

public class API {
    // Only fetch once
    public static void fetchItem() {
        System.out.println("Collecting to db...");
        ItemDB.itemDB.clear();
        ItemDB.itemMatcher.clear();
        try {
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(HTTP.get("https://api.wynncraft.com/public_api.php?action=itemDB&category=all"));
            JsonArray arr = json.getAsJsonObject().getAsJsonArray("items");
            for (JsonElement element : arr) {
                String rarity = element.getAsJsonObject().get("tier").getAsString();
                Tier tier;
                switch(rarity) {
                    case "Mythic":
                        tier = Tier.MYTHIC;
                        break;
                    case "Fabled":
                        tier = Tier.FABLED;
                        break;
                    case "Legendary":
                        tier = Tier.LEGENDARY;
                        break;
                    case "Rare":
                        tier = Tier.RARE;
                        break;
                    case "Set":
                        tier = Tier.SET;
                        break;
                    case "Unique":
                        tier = Tier.UNIQUE;
                        break;
                    case "Normal":
                        tier = Tier.NORMAL;
                        break;
                    default:
                        System.out.println("Found ambiguous item " + element);
                        tier = Tier.UNKNOWN;
                        break;
                }
                // Item Name : Tier
                ItemDB.itemDB.put(element.getAsJsonObject().get("name").getAsString(), tier);
                ItemDB.itemMatcher.add(element.getAsJsonObject().get("name").getAsString());
            }
            for(int i=0; i<4; i++) {
                json = parser.parse(HTTP.get("https://api.wynncraft.com/v2/ingredient/search/tier/"+i));
                for (JsonElement element : json.getAsJsonObject().getAsJsonArray("data")) {
                    Tier tier = (i == 0 ? Tier.INGREDIENT_0 : i == 1 ? Tier.INGREDIENT_1 : i == 2 ? Tier.INGREDIENT_2 : Tier.INGREDIENT_3);
                    ItemDB.itemDB.put(element.getAsJsonObject().get("name").getAsString(), tier);
                    ItemDB.itemMatcher.add(element.getAsJsonObject().get("name").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("API fetched.");
    }
}
