package com.spiritlight.mobkilltracker;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Arrays;

public class AnnouncerSpirit {
    public static void send(String message) {
        try {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(Main.PREFIX + message));
        } catch (NullPointerException ignored) {
            System.out.println("Caught NullPointerException whilst attempting to send a message, assuming player does not yet exist.");
        }
    }

    public static void send(TextComponentString message) {
        try {
            Minecraft.getMinecraft().player.sendMessage(message);
        } catch (NullPointerException ignored) {
            System.out.println("Caught NullPointerException whilst attempting to send a message, assuming player does not yet exist.");
        }
    }

    public static void send(ITextComponent message) {
        try {
            Minecraft.getMinecraft().player.sendMessage(message);
        } catch (NullPointerException ignored) {
            System.out.println("Caught NullPointerException whilst attempting to send a message, assuming player does not yet exist.");
        }
    }

    public static void sendException(Exception e, String message, boolean printStackTrace) {
        Style style;
        TextComponentString t = new TextComponentString(message.replace("$err", e.getMessage()).replace("$errType", e.getClass().getCanonicalName()));
        style = t.getStyle();
        TextComponentString s = new TextComponentString(e.getClass().getCanonicalName() + ": " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()).replace(",", ",\n").replace("[", "").replace("]", ""));
        style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, s));
        send(t);
        if(printStackTrace)
            e.printStackTrace();
    }

    public static void sendException(Exception e) {
        sendException(e, "$errType: $err caught whilst performing an action (Hover for details)", true);
    }
}
