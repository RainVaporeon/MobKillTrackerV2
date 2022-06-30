package com.spiritlight.mobkilltracker;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TossEvent {
    @SubscribeEvent
    public void onToss(ItemTossEvent event) {
        if(!Main.enabled) return;
        if(!TotemEvent.instanceOccupied.get()) return;
        if(Minecraft.getMinecraft().world == null) return;
        final EntityItem item = event.getEntityItem();
        if(TotemEvent.drops.doAllowUpdates()) {
            final String name = item.serializeNBT().getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
            TotemEvent.drops.removeDrop(ItemDB.getTier(name));
        }
    }
}
