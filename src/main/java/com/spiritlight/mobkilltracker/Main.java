package com.spiritlight.mobkilltracker;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashMap;
import java.util.Map;

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

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        API.fetchItem();
        ClientCommandHandler.instance.registerCommand(new DebugCommand());
        MinecraftForge.EVENT_BUS.register(new entityEvent());
        MinecraftForge.EVENT_BUS.register(new TotemEvent());
        MinecraftForge.EVENT_BUS.register(new DisconnectEvent());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
}
