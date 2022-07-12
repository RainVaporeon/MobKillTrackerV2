package com.spiritlight.mobkilltracker;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class DropAnalyzer {
    private static JsonElement cached_element = null;
    private static final Random RANDOM = new Random();

    /**
     * Utility method to export the provided {@link DropStatistics} into a JSON.
     * @param dropList The list of {@link DropStatistics} to process.
     */
    public static void exportDrops(@Nonnull List<DropStatistics> dropList) {
        final AnnouncerSpirit messenger = new AnnouncerSpirit();
        messenger.send("Exporting " + dropList.size() + (dropList.size() == 1 ? " entry..." : " entries..."));
        JsonArray array = new JsonArray();
        for(DropStatistics drops : dropList) {
            JsonObject object = new JsonObject();
            object.addProperty("mythic", drops.getMythicDropped());
            object.addProperty("fabled", drops.getFabledDropped());
            object.addProperty("legendary", drops.getLegendaryDropped());
            object.addProperty("rare", drops.getRareDropped());
            object.addProperty("set", drops.getSetDropped());
            object.addProperty("unique", drops.getUniqueDropped());
            object.addProperty("normal", drops.getNormalDropped());
            object.addProperty("ingredient_t3", drops.getT3Ingredients());
            object.addProperty("ingredient_t2", drops.getT2Ingredients());
            object.addProperty("ingredient_t1", drops.getT1Ingredients());
            object.addProperty("ingredient_t0", drops.getT0Ingredients());
            object.addProperty("kills", drops.getKills());
            object.addProperty("note", drops.getNote());

            // object.addProperty("rarity_index", getRarityIndex(drops));
            array.add(object);
        }
        cached_element = array;
        messenger.send(new TextComponentString("Successfully exported, click here to copy. (Only copies latest)")
                .setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mkt __$copy.clipboard"))));
        messenger.send("Alternatively, check your logs for exported JSON. (This set of numbers may help you find it quickier: "
                + RANDOM.nextInt() + ")");
        System.out.println(array);
    }

    protected static void copyToClipboard() {
        if(cached_element == null) return;
        StringSelection stringSelection = new StringSelection(cached_element.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    protected static int getRarityIndex(DropStatistics drops) {
        return drops.getMythicDropped() * 512
                + drops.getFabledDropped() * 64
                + drops.getLegendaryDropped() * 16
                + drops.getRareDropped() * 4
                + drops.getSetDropped() * 6
                + drops.getUniqueDropped() * 2
                + drops.getNormalDropped();
    }
}
