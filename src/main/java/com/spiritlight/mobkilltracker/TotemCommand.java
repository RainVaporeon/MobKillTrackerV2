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
        if(args.length==0) {
            AnnouncerSpirit.send("Invalid syntax. Available commands:\n" +
                    "/" + getName() + " start [duration] - Records mob area for x seconds, default 30.\n" +
                    "/" + getName() + " stop - Terminates current scan and dump the summary\n" +
                    "/" + getName() + " time [duration] - Sets default keybind duration.");
            return;
        }
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "start":
                if(args.length == 1) {
                    TotemEvent.start();
                } else try {
                    TotemEvent.start(Integer.parseInt(args[1]));
                } catch (NumberFormatException ex) {
                    AnnouncerSpirit.send("Failed to parse the duration input.");
                }
                break;
            case "stop":
                if(TotemEvent.instanceOccupied.get()) {
                    AnnouncerSpirit.send("Terminating this session...");
                    TotemEvent.terminate();
                } else {
                    AnnouncerSpirit.send("There are no ongoing session.");
                }
                break;
            case "time":
                if(args.length == 1) {
                    AnnouncerSpirit.send("Current hotkey time is " + Main.def_duration + " seconds.");
                    return;
                } else try {
                    int time = Integer.parseInt(args[1]);
                    if(time < 1) {
                        AnnouncerSpirit.send("Timer is too short, must be at least 1 second.");
                        return;
                    } else {
                        Main.def_duration = time;
                        ConfigSpirit.save();
                    }
                } catch (NumberFormatException ex) {
                    AnnouncerSpirit.send("Failed to parse the duration input.");
                }
            default:
                AnnouncerSpirit.send("Invalid syntax. Available commands:\n" +
                        "/" + getName() + " start [duration] - Records mob area for x seconds, default 30.\n" +
                        "/" + getName() + " stop - Terminates current scan and dump the summary\n");
        }
    }
}
