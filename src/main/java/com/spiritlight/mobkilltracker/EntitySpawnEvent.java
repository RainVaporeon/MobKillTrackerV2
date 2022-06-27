package com.spiritlight.mobkilltracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class EntitySpawnEvent {
    static final Map<UUID, NBTTagCompound> UUIDMap = new ConcurrentHashMap<>();
    private static final status s = new status(false);

    @SubscribeEvent
    public void onEvent(final EntityEvent event) {
        if(s.check()) return;
        if (Minecraft.getMinecraft().world == null)
            return;
        CompletableFuture.runAsync(() -> {
            s.on();
            final List<Entity> worldEntity = new ArrayList<>(Minecraft.getMinecraft().world.getLoadedEntityList());
            for(Entity e : worldEntity) {
                if(e instanceof EntityPlayerSP) continue;
                if(e == null) continue;
                if(e.serializeNBT().hasNoTags()) continue;
                if((e instanceof EntityItem && e.getName().contains("NPC"))) continue;
                scanEntity(e);
            }
            s.off();
        }).exceptionally(e -> {
            AnnouncerSpirit.sendException((Exception) e);
            s.off();
            return null;
        }).thenAccept(x -> s.off());
    }

    private void scanEntity(Entity e) {
        final UUID entityUUID = e.getUniqueID();
        NBTTagCompound trimmedNBT;
        trimmedNBT = e.serializeNBT();
        trimmedNBT.removeTag("Age");
        trimmedNBT.removeTag("Motion");
        trimmedNBT.removeTag("Pos");
        trimmedNBT.removeTag("Fire");
        trimmedNBT.removeTag("FallDistance");
        trimmedNBT.removeTag("PickupDelay");
        trimmedNBT.removeTag("OnGround");
        if (UUIDMap.containsKey(e.getUniqueID()) && UUIDMap.get(e.getUniqueID()).equals(trimmedNBT)) return;
        if(e instanceof EntityItem) {
            final NBTTagCompound nbt = e.serializeNBT();
            final String name = nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
            if(nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").toString().contains("identifications")) return;
            if (Main.log) {
                AnnouncerSpirit.send(new TextComponentString("Found item of " + e.getName()).setStyle(
                        new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new TextComponentString(format("Wynncraft Item Name: " + nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name") + "\n\n" + "Item name: " + (e.hasCustomName() ? e.getCustomNameTag() + "(" + e.getName() + ")" : e.getName()) + "\n" + "Item UUID: " + e.getUniqueID() + "\n\n" + nbt + "\n\nClick to track!")))
                        ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                e.getPosition().getX() + " " + e.getPosition().getY() + " " + e.getPosition().getZ()))));
            }
            TotemEvent.drops.addDrop(Main.itemDB.get(name));
        } else {
            final String name = (e.hasCustomName() ? e.getCustomNameTag() : e.getName());
            if(name.contains("Combat XP")) {
                if(Main.log)
                    AnnouncerSpirit.send("Detected mob kill.");
                TotemEvent.mobKills++;
            }
        }
        UUIDMap.put(entityUUID, trimmedNBT);
    }

    private String format(String s) {
        return s
                .replaceAll("\\{", TextFormatting.AQUA + "{" + TextFormatting.GOLD)
                .replaceAll("}",TextFormatting.AQUA + "}" + TextFormatting.GOLD)
                .replaceAll("\\[",TextFormatting.RESET + "[" + TextFormatting.GOLD)
                .replaceAll("]",TextFormatting.RESET + "]" + TextFormatting.GOLD)
                .replaceAll(",",TextFormatting.RESET + "," + TextFormatting.GOLD)
                .replaceAll(":", TextFormatting.RESET + ":" + TextFormatting.AQUA)
                .replaceAll("'", TextFormatting.YELLOW + "'" + TextFormatting.RESET)
                .replaceAll("\"", TextFormatting.GREEN + "\"" + TextFormatting.GOLD);
    }
}
