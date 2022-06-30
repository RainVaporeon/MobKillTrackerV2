package com.spiritlight.mobkilltracker;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.IOException;


@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    public static final String MODID = "mkt";
    public static final String NAME = "MobKillTracker v2";
    public static final String VERSION = "1.0";
    // Lists are all Name:Tier

    static boolean test = false;
    static boolean log = false;
    static boolean advlog = false;
    static boolean enabled = true;
    static boolean logAdvanced = false;
    static int def_duration = 30;
    static final String PREFIX = "§2[§aMKT §ev2§2] §a";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        try {
            ConfigSpirit.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        API.fetchItem();
        ClientCommandHandler.instance.registerCommand(new DebugCommand());
        ClientCommandHandler.instance.registerCommand(new TotemCommand());
        MinecraftForge.EVENT_BUS.register(new entityEvent());
        MinecraftForge.EVENT_BUS.register(new TotemEvent());
        MinecraftForge.EVENT_BUS.register(new DisconnectEvent());
        MinecraftForge.EVENT_BUS.register(new TossEvent());
        MinecraftForge.EVENT_BUS.register(new KeyBindings());
        KeyBindings.register();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
}
