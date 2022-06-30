package com.spiritlight.mobkilltracker;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TotemEvent {
    protected static AtomicBoolean instanceOccupied = new AtomicBoolean(false);
    protected final static DropStatistics drops = new DropStatistics();
    static int mobKills = 0;
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @SubscribeEvent
    public void onMessage(ClientChatReceivedEvent chat) {
        if(!Main.enabled) return;
        final String message = chat.getMessage().getUnformattedText();
        if((message.contains("placed a mob totem") && !message.contains("[")) || Main.test) {
            if(!instanceOccupied.get()) {
                if(Main.test) {
                    Main.test = false;
                    start(30);
                } else {
                    start(300);
                }
            } else {
                AnnouncerSpirit.send("A mob totem is already present, ignoring this one.");
                Main.test = false;
            }
        }
    }

    protected static void start(int duration) {
        if(!instanceOccupied.get()) {
            drops.clear();
            entityEvent.UUIDMap.clear();
            mobKills = 0;
            drops.setAllowUpdates(true);
            AnnouncerSpirit.send("Detected mob totem, started recording...");
            instanceOccupied.set(true);
            scheduler.schedule(summary, duration, TimeUnit.SECONDS);
        } else {
            AnnouncerSpirit.send("An instance already exists.");
        }
    }

    protected static void start() {
        start(30);
    }

    protected static void terminate() {
        summary.run();
        scheduler.shutdown();
    }

    private static final Runnable summary = () -> {
        if(!instanceOccupied.get()) return;
        drops.setAllowUpdates(false);
        AnnouncerSpirit.send(
                "\n" +
                        "§3§l Mob Totem Ended\n" +
                        "§rTotal Mobs Killed: §c" + mobKills + "\n" +
                        "§rTotal Items Dropped: §a" + drops.getTotal(0) + "\n" +
                        "\n" +
                        "§6§l Item Summary: \n" +
                        "§rIngredient Drops: §b[✫✫✫] §rx" + drops.getT3Ingredients() + " §d[✫✫§8✫§d] §rx" + drops.getT2Ingredients() + " §e[✫§8✫✫§e] §rx" + drops.getT1Ingredients() + " §7[§8✫✫✫§7] §rx" + drops.getT0Ingredients() + "\n" +
                        "§5§lMythic §rDrops: " + drops.getMythicDropped() + "\n" +
                        "§cFabled §rDrops: " + drops.getFabledDropped() + "\n" +
                        "§bLegendary §rDrops: " + drops.getLegendaryDropped() + "\n" +
                        "§dRare §rDrops: " + drops.getRareDropped() + "\n" +
                        "§aSet §rDrops: " + drops.getSetDropped() + "\n" +
                        "§eUnique §rDrops: " + drops.getUniqueDropped() + "\n" +
                        "§rNormal §rDrops: " + drops.getNormalDropped() + "\n" +
                        "Total drops: Item " + drops.getTotal(1) + ", Ingredients " + drops.getTotal(2) +
                        (Main.logAdvanced ? "\n §c§lAdvanced details:\n" +
                                "§rItem Rate: " + mobKills / drops.getTotal(1) + " §7(Mobs/item)" + "\n" +
                                "§rIngredient Rate: " + mobKills / drops.getTotal(2) + " §7(Mobs/Ingredient)" : "")
        );
        entityEvent.UUIDMap.clear(); // Releasing resources
        drops.clear();
        instanceOccupied.set(false); // Call at last line
    };
}
