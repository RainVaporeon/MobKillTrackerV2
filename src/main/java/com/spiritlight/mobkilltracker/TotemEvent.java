package com.spiritlight.mobkilltracker;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TotemEvent {
    protected static AtomicBoolean instanceOccupied = new AtomicBoolean(false);
    final static DropStatistics drops = new DropStatistics();
    static int mobKills = 0;
    final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @SubscribeEvent
    public void onMessage(ClientChatReceivedEvent chat) {
        final String message = chat.getMessage().getUnformattedText();
        if((message.contains("placed a mob totem") && !message.contains("[")) || Main.test) {
            if(!instanceOccupied.get()) {
                drops.clear();
                entityEvent.UUIDMap.clear();
                mobKills = 0;
                drops.setAllowUpdates(true);
                AnnouncerSpirit.send("Detected mob totem, started recording...");
                instanceOccupied.set(true);
                if(Main.test) {
                    Main.test = false;
                    scheduler.schedule(summary, 30, TimeUnit.SECONDS);
                } else {
                    scheduler.schedule(summary, 300, TimeUnit.SECONDS);
                }
            } else {
                AnnouncerSpirit.send("A mob totem is already present, ignoring this one.");
                Main.test = false;
            }
        }
    }

    private final Runnable summary = () -> {
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
                        "Total drops: Item " + drops.getTotal(1) + ", Ingredients " + drops.getTotal(2)
        );
        instanceOccupied.set(false);
    };
}
