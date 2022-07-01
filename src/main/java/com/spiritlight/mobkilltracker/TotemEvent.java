package com.spiritlight.mobkilltracker;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TotemEvent {
    protected static final AtomicBoolean instanceOccupied = new AtomicBoolean(false);
    protected final static DropStatistics drops = new DropStatistics();
    static int mobKills = 0;
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

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
                final AnnouncerSpirit messenger = new AnnouncerSpirit();
                messenger.send("A mob totem is already present, ignoring this one.");
                Main.test = false;
            }
        }
    }

    /**
     * Starts a new totem timer. If the provided scheduler is not available, create a new one instead.
     * @param duration The length of the timer.
     */
    protected static void start(int duration) {
        final AnnouncerSpirit messenger = new AnnouncerSpirit();
        if(!instanceOccupied.get()) {
            drops.clear();
            entityEvent.UUIDMap.clear();
            mobKills = 0;
            drops.setAllowUpdates(true);
            entityEvent.antiDupeE.set(true);
            entityEvent.antiDupeI.set(true);
            messenger.send("Detected mob totem, started recording...");
            instanceOccupied.set(true);
            try {
                if(scheduler.isTerminated()) {
                    scheduler = Executors.newSingleThreadScheduledExecutor();
                } else {
                    scheduler.schedule(summary, duration, TimeUnit.SECONDS);
                }
            } catch (RejectedExecutionException ex) {
                new AnnouncerSpirit().send("Error: The responsible thread is not available right now. Please try again later.");
                instanceOccupied.set(false);
            }
        } else {
            messenger.send("An instance already exists.");
        }

    }

    protected static void start() {
        start(30);
    }


    /**
     * Dumps the current summary and terminates thread
     */
    protected static void terminate() {
        summary.run();
        scheduler.shutdownNow();
    }

    private static final Runnable summary = () -> {
        final AnnouncerSpirit messenger = new AnnouncerSpirit();
        if(!instanceOccupied.get()) return;
        drops.setAllowUpdates(false);
        final int totalDrops = drops.getTotal(0);
        final int itemDrops = drops.getTotal(1);
        final int ingDrops = drops.getTotal(2);
        messenger.send(
                "\n" +
                        "§3§l Mob Totem Ended\n" +
                        "§rTotal Mobs Killed: §c" + mobKills + "\n" +
                        "§rTotal Items Dropped: §a" + totalDrops + "\n" +
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
                        "Total drops: Item " + itemDrops + ", Ingredients " + ingDrops +
                        (Main.logAdvanced ? "\n §c§lAdvanced details:\n" +
                                "§rItem Rate: " + mobKills / (itemDrops == 0 ? 1 : itemDrops) + " §7(Mobs/item)" + "\n" +
                                "§rIngredient Rate: " + mobKills / (ingDrops == 0 ? 1 : ingDrops) + " §7(Mobs/Ingredient)" : "")
        );
        entityEvent.UUIDMap.clear(); // Releasing resources
        drops.clear();
        instanceOccupied.set(false); // Call at last line
    };
}
