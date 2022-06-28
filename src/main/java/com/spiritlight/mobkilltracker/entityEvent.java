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
    static final Map<UUID, NBTTagCompound> UUIDMap = new ConcurrentHashMap<>();
    private final AtomicBoolean STATUS = new AtomicBoolean(false);
    private final List<UUID> ignoredMobs = new ArrayList<>();
    private final List<Entity> ignoredEntities = new ArrayList<>();

    @SubscribeEvent
    public void onEvent(EntityEvent event) {
        if(STATUS.get()) return;
        if (Minecraft.getMinecraft().world == null) return;
        if(TotemEvent.instanceOccupied) return;
        CompletableFuture.runAsync(() -> {
            STATUS.set(true);
            final List<Entity> entityList = new ArrayList<>(Minecraft.getMinecraft().world.getLoadedEntityList());
            for(Entity e : entityList) {
                if(e == null) continue;
                if(ignoredMobs.contains(e.getUniqueID())) continue;
                if(ignoredEntities.contains(e)) continue;
                if(!(e instanceof EntityArmorStand || e instanceof EntityItem)) {
                    ignoredMobs.add(e.getUniqueID());
                    continue;
                }
                if(!e.addedToChunk) continue;
                if(e.ticksExisted < 2) continue;
                if (e instanceof EntityItem) {
                    if (e.getName().equals("tile.item.air")) continue;
                    if (e.getName().equals("item.item.emerald")) continue;
                    if (e.getName().contains("NPC")) continue;
                }
                final UUID entityUUID;
                NBTTagCompound trimmedNBT;
                try {
                    trimmedNBT  = e.serializeNBT().copy();
                    entityUUID = e.getUniqueID();
                } catch (NullPointerException| ReportedException ex) {
                    System.out.println("Error occurred whilst processing entity " + e.getClass().getName() + ": " + ex.getMessage());
                    if(Main.log) {
                        AnnouncerSpirit.send(new TextComponentString("Caught exception processing " + e.getClass().getName() + ", ignoring").setStyle(
                                new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Stacktrace: \n" + ex.getMessage() + ":" + ex.getClass().getCanonicalName() + "\n" + Arrays.toString(ex.getStackTrace()).replace(",", "\n") + "\nEntity Data:\n" + e)))
                        ));
                    }
                    ignoredEntities.add(e);
                    continue; // don't really wanna bother why it's not working rn
                }
                if(trimmedNBT.hasNoTags()) continue;
                if(trimmedNBT.hasKey("NoGravity", 1)) continue; // Mob attacks
                trimmedNBT.removeTag("Age");
                trimmedNBT.removeTag("Motion");
                trimmedNBT.removeTag("Pos");
                trimmedNBT.removeTag("Fire");
                trimmedNBT.removeTag("FallDistance");
                trimmedNBT.removeTag("PickupDelay");
                trimmedNBT.removeTag("OnGround");
                if (UUIDMap.containsKey(e.getUniqueID()) && UUIDMap.get(e.getUniqueID()).equals(trimmedNBT)) continue;
                if(Main.advlog) {
                    AnnouncerSpirit.send("Logging entity " + e.getClass().getName());
                }
                if(e instanceof EntityItem) {
                    if(Main.advlog) {
                        AnnouncerSpirit.send(new TextComponentString("Processing item entity " + e.getClass().getName()).setStyle(
                                new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(e.toString())))
                        ));
                    }
                    final NBTTagCompound nbt = e.serializeNBT();
                    final String name = nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
                    if(nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").toString().contains("identifications")) continue;
                    if (Main.log) {
                        AnnouncerSpirit.send(new TextComponentString("Found item of " + e.getName()).setStyle(
                                new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new TextComponentString(format("Wynncraft Item Name: " + nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name") + "\n\n" + "Item name: " + (e.hasCustomName() ? e.getCustomNameTag() + "(" + e.getName() + ")" : e.getName()) + "\n" + "Item UUID: " + e.getUniqueID() + "\n\n" + nbt + "\n\nClick to track!")))
                                ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                        e.getPosition().getX() + " " + e.getPosition().getY() + " " + e.getPosition().getZ()))));
                    }
                    try {
                        TotemEvent.drops.addDrop(ItemDB.getTier(name));
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Unknown tier for " + name);
                    } catch (IllegalStateException exc) {
                        // no-op
                    }
                } else {
                    if(Main.advlog) {
                        AnnouncerSpirit.send("Processing entity " + e.getClass().getName());
                    }
                    final String name = (e.hasCustomName() ? e.getCustomNameTag() : e.getName());
                    if(name.toLowerCase(Locale.ROOT).contains("combat xp")) {
                        if(Main.log)
                            AnnouncerSpirit.send("Detected mob kill.");
                        TotemEvent.mobKills++;
                    }
                }
                UUIDMap.put(entityUUID, trimmedNBT);
            }
            STATUS.set(false);
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        }).whenComplete((c, throwable) -> STATUS.set(false)
        );
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
