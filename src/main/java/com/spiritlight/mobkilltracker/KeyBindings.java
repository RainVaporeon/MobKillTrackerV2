package com.spiritlight.mobkilltracker;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyBindings {
    static final KeyBinding[] keyBindings = new KeyBinding[1];
    private static boolean registered = false;

    static void register() {
        if(registered) return;
        for(KeyBinding k : keyBindings) {
            ClientRegistry.registerKeyBinding(k);
        }
        registered = true;
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent e) {
        if(keyBindings[0].isPressed()) {

        }
    }

    static {
        keyBindings[0] = new KeyBinding("Toggle totem status", Keyboard.KEY_M, "MKT v2");
    }
}
