package com.spiritlight.mobkilltracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TossEvent {
    protected static final List<EntityItem> processQueue = new CopyOnWriteArrayList<>();
    private final Map<UUID, String> UUIDMap = new ConcurrentHashMap<>();
    private final AtomicBoolean processToss = new AtomicBoolean(false);
    private final double MAGIC_TOSS_Y = 1.31999999284744;

    // wtf is this
    @SubscribeEvent
    public void onToss(EntityEvent event) {
        if (!Main.enabled) return;
        if (!TotemEvent.instanceOccupied.get()) return;
        if (Minecraft.getMinecraft().world == null) return;
        if (!(event.getEntity() instanceof EntityItem)) return;
        if (!TotemEvent.drops.doAllowUpdates()) return;
        final Entity e = event.getEntity();
        if (!(e instanceof EntityItem)) return;
        if(e.getName().equals("item.item.emerald")) return;
        if (e.getName().contains("NPC")) return;
        if (e.serializeNBT().hasKey("NoGravity", 1)) return;
        NBTTagCompound nbt = e.serializeNBT();
        if (nbt.hasKey("Passengers") && nbt.toString().contains("Banner")) return; // Cape thingy
        final String name = e.serializeNBT().getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
        if (UUIDMap.containsKey(e.getUniqueID()) && UUIDMap.get(e.getUniqueID()).equals(name)) return;
        if(Main.cleaner) {
            final List<EntityPlayer> playerList = new ArrayList<>(Minecraft.getMinecraft().world.playerEntities);
            boolean isFound = playerList.stream().filter(player -> (!player.isDead && player.getHealth() > 0))
                    .filter(player -> !player.isInvisible())
                    .filter(player -> !(player instanceof FakePlayer))
                    .anyMatch(player -> player.posY == e.posY - MAGIC_TOSS_Y);
            if(!isFound) return;
        } else {
            final EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
            if (!(e.posY - MAGIC_TOSS_Y == playerSP.posY)) return;
        }
        processQueue.add((EntityItem) e);
        if(processToss.get()) return;
        CompletableFuture.runAsync(this::process).whenComplete((x, throwable) -> processToss.set(false));
    }

    private void process() {
        processToss.set(true);
        final AnnouncerSpirit messenger = new AnnouncerSpirit();
        for(EntityItem e : processQueue) {
            if(e.ticksExisted < 1) continue;
            final String name = e.serializeNBT().getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
            if (UUIDMap.containsKey(e.getUniqueID()) && UUIDMap.get(e.getUniqueID()).equals(name)) continue;
            int wItemQuantity = e.serializeNBT().getCompoundTag("Item").getCompoundTag("tag").getInteger("Count");
            TotemEvent.drops.removeDrop(ItemDB.getTier(name), wItemQuantity);
            if (Main.log) {
                messenger.send(new TextComponentString("Found tossed item of " + e.getName()).setStyle(
                        new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new TextComponentString(format("Wynncraft Item Name:" + name + "\n\n" + "Item name: " + (e.hasCustomName() ? e.getCustomNameTag() + "(" + e.getName() + ")" : e.getName()) + "\n" + "Item UUID: " + e.getUniqueID() + "\n\n" + e.serializeNBT() + "\n\nClick to track!")))
                        ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                e.getPosition().getX() + " " + e.getPosition().getY() + " " + e.getPosition().getZ()))));
            }
            UUIDMap.put(e.getUniqueID(), name);
            processQueue.remove(e);
        }
        processToss.set(false);
    }

    private String format(String s) {
        return s
                .replace("{", TextFormatting.AQUA + "{" + TextFormatting.GOLD)
                .replace("}", TextFormatting.AQUA + "}" + TextFormatting.GOLD)
                .replace("[", TextFormatting.RESET + "[" + TextFormatting.GOLD)
                .replace("]", TextFormatting.RESET + "]" + TextFormatting.GOLD)
                .replace(",", TextFormatting.RESET + "," + TextFormatting.GOLD)
                .replace(":", TextFormatting.RESET + ":" + TextFormatting.AQUA)
                .replace("'", TextFormatting.YELLOW + "'" + TextFormatting.RESET)
                .replace("\"", TextFormatting.GREEN + "\"" + TextFormatting.GOLD);
    }
}
