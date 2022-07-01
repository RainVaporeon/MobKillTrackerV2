package com.spiritlight.mobkilltracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class TossEvent {
    private final Map<UUID, String> UUIDMap = new ConcurrentHashMap<>();
    private final AtomicBoolean processToss = new AtomicBoolean(false);

    // wtf is this
    @SubscribeEvent
    public void onToss(EntityEvent event) {
        if (!Main.enabled) return;
        if (!TotemEvent.instanceOccupied.get()) return;
        if (Minecraft.getMinecraft().world == null) return;
        if (!(event.getEntity() instanceof EntityItem)) return;
        if (!TotemEvent.drops.doAllowUpdates()) return;
        if (processToss.get()) return;

        CompletableFuture.runAsync(() -> {
            processToss.set(true);
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
                final EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
                if (!(e.posY - 1.31999999284744 == playerSP.posY)) continue;
                final AnnouncerSpirit messenger = new AnnouncerSpirit();
                final String name = e.serializeNBT().getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
                if (Main.log) {
                    messenger.send(new TextComponentString("Found tossed item of " + e.getName()).setStyle(
                            new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new TextComponentString(format("Wynncraft Item Name:" + name + "\n\n" + "Item name: " + (e.hasCustomName() ? e.getCustomNameTag() + "(" + e.getName() + ")" : e.getName()) + "\n" + "Item UUID: " + e.getUniqueID() + "\n\n" + e.serializeNBT() + "\n\nClick to track!")))
                            ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                    e.getPosition().getX() + " " + e.getPosition().getY() + " " + e.getPosition().getZ()))));
                }
                TotemEvent.drops.removeDrop(ItemDB.getTier(name), 2); // Init inclusion. + ext. drop
                UUIDMap.put(e.getUniqueID(), trimmedNBT.toString());
            }
        }).whenComplete((x, throwable) -> processToss.set(false))
                .exceptionally(e -> {
                    e.printStackTrace();
                    if(Main.log) {
                        new AnnouncerSpirit().sendException((Exception) e);
                    }
                    processToss.set(false);
                    return null;
                });
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
