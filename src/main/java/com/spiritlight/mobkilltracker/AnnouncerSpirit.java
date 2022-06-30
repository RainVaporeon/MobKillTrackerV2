package com.spiritlight.mobkilltracker;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Arrays;

public class AnnouncerSpirit {
    public void send(String message) {
        if (Minecraft.getMinecraft().player == null) return;
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(Main.PREFIX + message));
    }

    public void send(TextComponentString message) {
        if (Minecraft.getMinecraft().player == null) return;
        Minecraft.getMinecraft().player.sendMessage(message);
    }

    public void send(ITextComponent message) {
        if (Minecraft.getMinecraft().player == null) return;
        Minecraft.getMinecraft().player.sendMessage(message);
    }

    public void sendException(Exception e, String message, boolean printStackTrace) {
        Style style;
        TextComponentString t = new TextComponentString(message.replace("$err", e.getMessage()).replace("$errType", e.getClass().getCanonicalName()));
        style = t.getStyle();
        TextComponentString s = new TextComponentString(e.getClass().getCanonicalName() + ": " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()).replace(",", ",\n").replace("[", "").replace("]", ""));
        style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, s));
        send(t);
        if (printStackTrace)
            e.printStackTrace();
    }

    public void sendException(Exception e) {
        sendException(e, "$errType: $err caught whilst performing an action (Hover for details)", true);
    }
}
