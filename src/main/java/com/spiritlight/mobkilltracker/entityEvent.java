package com.spiritlight.mobkilltracker;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class entityEvent {
    static final Map<UUID, String> UUIDMap = new ConcurrentHashMap<>();
    private final AtomicBoolean entityOccupied = new AtomicBoolean(false);
    private final AtomicBoolean itemOccupied = new AtomicBoolean(false);
    protected static final AtomicBoolean firstScanE = new AtomicBoolean(true);
    protected static final AtomicBoolean firstScanI = new AtomicBoolean(true);

    @SubscribeEvent
    public void onEntityEvent(final EntityEvent event) {
        if (!Main.enabled) return;
        if (Minecraft.getMinecraft().world == null) return;
        if (!TotemEvent.instanceOccupied.get()) return;
        if(entityOccupied.get()) return;
        CompletableFuture.runAsync(() -> {
            entityOccupied.set(true);
            final AnnouncerSpirit messenger = new AnnouncerSpirit();
            final List<Entity> worldEntity = new ArrayList<>(Minecraft.getMinecraft().world.getLoadedEntityList());
            for(Entity e :worldEntity) {
                if (!(e instanceof EntityArmorStand)) continue;
                final UUID entityUUID = e.getUniqueID();
                String mobOrItemName = (e.hasCustomName() ? e.getCustomNameTag() : e.getName());
                if (UUIDMap.containsKey(e.getUniqueID()) && UUIDMap.get(e.getUniqueID()).equals(mobOrItemName))
                    return;
                if (Main.advlog) {
                    messenger.send(new TextComponentString("Processing entity " + e.getClass().getName()).setStyle(
                            new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(e.toString())))
                    ));
                }
                final String name = (e.hasCustomName() ? e.getCustomNameTag() : e.getName());
                if (name.toLowerCase(Locale.ROOT).contains("combat xp")) {
                    if (Main.log)
                        messenger.send("Detected mob kill.");
                    if(!firstScanE.get()) {
                        TotemEvent.mobKills++;
                    }
                }
                UUIDMap.put(entityUUID, mobOrItemName);
            }
            firstScanE.set(false);
        }).whenComplete((x, throwable) -> entityOccupied.set(false))
                .exceptionally(e -> {
                    if(Main.log)
                        new AnnouncerSpirit().sendException((Exception) e);
                    entityOccupied.set(false);
                    return null;
        });
    }

    @SubscribeEvent
    public void itemEvent(final EntityEvent event) {
        if (!Main.enabled) return;
        if (!TotemEvent.instanceOccupied.get())
            return;
        if (Minecraft.getMinecraft().world == null)
            return;
        if(itemOccupied.get()) return;
        CompletableFuture.runAsync(() -> {
            final AnnouncerSpirit messenger = new AnnouncerSpirit();
            final List<Entity> worldEntity = new ArrayList<>(Minecraft.getMinecraft().world.getLoadedEntityList());
            for(Entity e : worldEntity) {
                if (!(e instanceof EntityItem)) continue;
                if (e.getName().contains("NPC")) continue;
                if (e.getName().equals("item.tile.air")) continue;
                if (e.serializeNBT().hasKey("NoGravity", 1)) continue;
                NBTTagCompound trimmedNBT = e.serializeNBT();
                if (trimmedNBT.hasKey("Passengers") && trimmedNBT.toString().contains("Banner")) continue; // Cape thingy
                trimmedNBT.removeTag("Age");
                trimmedNBT.removeTag("Motion");
                trimmedNBT.removeTag("Pos");
                trimmedNBT.removeTag("Fire");
                trimmedNBT.removeTag("FallDistance");
                trimmedNBT.removeTag("PickupDelay");
                trimmedNBT.removeTag("OnGround");
                if (UUIDMap.containsKey(e.getUniqueID()) && UUIDMap.get(e.getUniqueID()).equals(trimmedNBT.toString()))
                    continue;
                String wItemName = e.serializeNBT().getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
                if (Main.log) {
                    messenger.send(new TextComponentString("Found item of " + e.getName()).setStyle(
                            new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new TextComponentString(format("Wynncraft Item Name:" + wItemName + "\n\n" + "Item name: " + (e.hasCustomName() ? e.getCustomNameTag() + "(" + e.getName() + ")" : e.getName()) + "\n" + "Item UUID: " + e.getUniqueID() + "\n\n" + e.serializeNBT() + "\n\nClick to track!")))
                            ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                    e.getPosition().getX() + " " + e.getPosition().getY() + " " + e.getPosition().getZ()))));
                }
                if(!firstScanI.get()) {
                    TotemEvent.drops.addDrop(ItemDB.getTier(wItemName));
                }
                UUIDMap.put(e.getUniqueID(), trimmedNBT.toString());
            }
            firstScanI.set(false);
        }).exceptionally(e -> {
            if(Main.log)
                new AnnouncerSpirit().sendException((Exception) e);
            e.printStackTrace();
            itemOccupied.set(false);
            return null;
        }).whenComplete((x, t) -> itemOccupied.set(false));
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
