package com.spiritlight.mobkilltracker;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
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
    private final AtomicBoolean STATUS = new AtomicBoolean(false);
    private final AtomicBoolean ITEMSTATUS = new AtomicBoolean(false);

    @SubscribeEvent
    public void onEntityEvent(final EntityEvent event) {
        if(!Main.enabled) return;
        if (STATUS.get()) return;
        if (Minecraft.getMinecraft().world == null) return;
        if (!TotemEvent.instanceOccupied.get()) return;
        CompletableFuture.runAsync(() -> {
            STATUS.set(true);
            final List<Entity> entityList = new ArrayList<>(Minecraft.getMinecraft().world.getLoadedEntityList());
            for (Entity e : entityList) {
                if (!(e instanceof EntityArmorStand)) {
                    continue;
                }
                final UUID entityUUID = e.getUniqueID();
                String mobOrItemName = (e.hasCustomName() ? e.getCustomNameTag() : e.getName());
                if (UUIDMap.containsKey(e.getUniqueID()) && UUIDMap.get(e.getUniqueID()).equals(mobOrItemName)) continue;
                if (Main.advlog) {
                    AnnouncerSpirit.send(new TextComponentString("Processing entity " + e.getClass().getName()).setStyle(
                            new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(e.toString())))
                    ));
                }
                final String name = (e.hasCustomName() ? e.getCustomNameTag() : e.getName());
                if (name.toLowerCase(Locale.ROOT).contains("combat xp")) {
                    if (Main.log)
                        AnnouncerSpirit.send("Detected mob kill.");
                    TotemEvent.mobKills++;
                }
                UUIDMap.put(entityUUID, mobOrItemName);
            }
            STATUS.set(false);
        }).exceptionally(e -> {
            if (!(e instanceof ReportedException)) e.printStackTrace();
            STATUS.set(false);
            return null;
        }).whenComplete((c, throwable) -> STATUS.set(false)
        );
    }

    @SubscribeEvent
    public void itemEvent(final EntityEvent event) {
        if(!Main.enabled) return;
        if (!TotemEvent.instanceOccupied.get())
            return;
        if (ITEMSTATUS.get())
            return;
        if (Minecraft.getMinecraft().world == null)
            return;
        try {
            CompletableFuture.runAsync(() -> {
                ITEMSTATUS.set(true);
                final List<Entity> worldEntity = new ArrayList<>(Minecraft.getMinecraft().world.getLoadedEntityList());
                for (Entity e : worldEntity) {
                    if (!(e instanceof EntityItem)) continue;
                    if(e.getName().contains("NPC")) continue;
                    if(e.serializeNBT().hasKey("NoGravity", 1)) continue;
                    NBTTagCompound trimmedNBT = e.serializeNBT();
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
                        AnnouncerSpirit.send(new TextComponentString("Found item of " + e.getName()).setStyle(
                                new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new TextComponentString(format("Wynncraft Item Name:" + wItemName + "\n\n" + "Item name: " + (e.hasCustomName() ? e.getCustomNameTag() + "(" + e.getName() + ")" : e.getName()) + "\n" + "Item UUID: " + e.getUniqueID() + "\n\n" + e.serializeNBT() + "\n\nClick to track!")))
                                ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                        e.getPosition().getX() + " " + e.getPosition().getY() + " " + e.getPosition().getZ()))));
                    }

                    TotemEvent.drops.addDrop(ItemDB.getTier(wItemName));
                    UUIDMap.put(e.getUniqueID(), trimmedNBT.toString());
                }
                ITEMSTATUS.set(false);
            }).exceptionally(e -> {
                ITEMSTATUS.set(false);
                if(Main.log) AnnouncerSpirit.sendException((Exception) e);
                e.printStackTrace();
                return null;
            }).thenAccept(x -> ITEMSTATUS.set(false));
        } catch (NullPointerException ignored) {}
    }

    private String format(String s) {
        return s
                .replace("{", TextFormatting.AQUA + "{" + TextFormatting.GOLD)
                .replace("}",TextFormatting.AQUA + "}" + TextFormatting.GOLD)
                .replace("[",TextFormatting.RESET + "[" + TextFormatting.GOLD)
                .replace("]",TextFormatting.RESET + "]" + TextFormatting.GOLD)
                .replace(",",TextFormatting.RESET + "," + TextFormatting.GOLD)
                .replace(":", TextFormatting.RESET + ":" + TextFormatting.AQUA)
                .replace("'", TextFormatting.YELLOW + "'" + TextFormatting.RESET)
                .replace("\"", TextFormatting.GREEN + "\"" + TextFormatting.GOLD);
    }
}
