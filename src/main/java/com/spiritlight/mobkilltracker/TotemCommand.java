package com.spiritlight.mobkilltracker;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
public class TotemCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "mkt";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        final AnnouncerSpirit messenger = new AnnouncerSpirit();
        if (args.length == 0) {
            messenger.send("Invalid syntax. Available commands:\n" +
                    "/" + getName() + " start [duration] - Records mob area for x seconds, default 30.\n" +
                    "/" + getName() + " stop - Terminates current scan and dump the summary\n" +
                    "/" + getName() + " last - Dumps last totem data\n" +
                    "/" + getName() + " time [duration] - Sets default keybind duration.\n" +
                    "/" + getName() + " advanced - Toggles advanced recording mode\n" +
                    "/" + getName() + " toggle - Toggles mod function");
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "toggle":
                Main.enabled = !Main.enabled;
                messenger.send("OK, mod active is now: " + Main.enabled);
                break;
            case "start":
                if (args.length == 1) {
                    TotemEvent.start();
                } else try {
                    TotemEvent.start(Integer.parseInt(args[1]));
                } catch (NumberFormatException ex) {
                    messenger.send("Failed to parse the duration input.");
                }
                break;
            case "lasttotem":
            case "last":
                messenger.send("Dumping last session data...");
                summary.run();
                break;
            case "stop":
                if (TotemEvent.instanceOccupied.get()) {
                    messenger.send("Terminating this session...");
                    TotemEvent.terminate();
                } else {
                    messenger.send("There are no ongoing session.");
                }
                break;
            case "advanced":
                Main.logAdvanced = !Main.logAdvanced;
                messenger.send("Dumping detailed summary: " + Main.logAdvanced);
                messenger.send("Extra details will " + (Main.logAdvanced ? "now" : "no longer") + " be logged in summary.");
                break;
            case "time":
                if (args.length == 1) {
                    messenger.send("Current hotkey time is " + Main.def_duration + " seconds.");
                    return;
                } else try {
                    int time = Integer.parseInt(args[1]);
                    if (time < 1) {
                        messenger.send("Timer is too short, must be at least 1 second.");
                        return;
                    } else {
                        Main.def_duration = time;
                        messenger.send("Set default hotkey time to " + time + " seconds.");
                        ConfigSpirit.save();
                    }
                } catch (NumberFormatException ex) {
                    messenger.send("Failed to parse the duration input.");
                }
                break;
            default:
                messenger.send("Invalid syntax. Try /mkt");
        }
    }

    private static final Runnable summary = () -> {
        final AnnouncerSpirit messenger = new AnnouncerSpirit();
        final DropStatistics drops = Main.cachedDrops;
        final int mobKills = Main.cachedKills;
        final int totalDrops = drops.getTotal(0);
        final int itemDrops = drops.getTotal(1);
        final int ingDrops = drops.getTotal(2);
        final int ingRate = (mobKills == 0 || ingDrops == 0 ? 0 : mobKills / ingDrops);
        final int itemRate = (mobKills == 0 || itemDrops == 0 ? 0 : mobKills / itemDrops);
        messenger.send(
                "\n" +
                        "§3§lTotem Summary §7(Last session)\n" +
                        "§rTotal Mobs Killed: §c" + mobKills + "\n" +
                        "§rTotal Items Dropped: §a" + totalDrops + "\n" +
                        "§rIngredient Drops: §b[✫✫✫] §rx" + drops.getT3Ingredients() + " §d[✫✫§8✫§d] §rx" + drops.getT2Ingredients() + " §e[✫§8✫✫§e] §rx" + drops.getT1Ingredients() + " §7[§8✫✫✫§7] §rx" + drops.getT0Ingredients() + "\n" +
                        "§5§lMythic §rDrops: " + drops.getMythicDropped() + "\n" +
                        "§cFabled §rDrops: " + drops.getFabledDropped() + "\n" +
                        "§bLegendary §rDrops: " + drops.getLegendaryDropped() + "\n" +
                        "§dRare §rDrops: " + drops.getRareDropped() + "\n" +
                        "§aSet §rDrops: " + drops.getSetDropped() + "\n" +
                        "§eUnique §rDrops: " + drops.getUniqueDropped() + "\n" +
                        "§rNormal §rDrops: " + drops.getNormalDropped() + "\n" +
                        "Total drops: Item " + itemDrops + ", Ingredients " + ingDrops +
                        (Main.logAdvanced ? "\n §c§lAdvanced details:\n" +
                                "§rItem Rate: " + itemRate + " §7(Mobs/item)" + "\n" +
                                "§rIngredient Rate: " + ingRate + " §7(Mobs/Ingredient)" : "")
        );
    };
}
