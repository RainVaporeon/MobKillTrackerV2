package com.spiritlight.mobkilltracker;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
public class DebugCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "mkt-debug";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            AnnouncerSpirit.send("test, log, adv, end, toggle, recollect");
            return;
        }
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "test":
            AnnouncerSpirit.send("Starting test.");
            Main.test = true;
            AnnouncerSpirit.send("");
            break;
            case "log":
                Main.log = !Main.log;
                AnnouncerSpirit.send("OK: " + Main.log);
                break;
            case "adv":
                Main.advlog = !Main.advlog;
                AnnouncerSpirit.send("OK: " + Main.advlog);
                break;
            case "end":
                TotemEvent.terminate();
                AnnouncerSpirit.send("OK, terminated.");
                break;
            case "toggle":
                Main.enabled = !Main.enabled;
                AnnouncerSpirit.send("OK, enabled status is now " + Main.enabled);
                break;
            case "recollect":
                AnnouncerSpirit.send("Collecting item data from API...");
                CompletableFuture.runAsync(API::fetchItem).thenAccept(v -> AnnouncerSpirit.send("Fetched from API."));
                break;
            default:
                AnnouncerSpirit.send("test, log, adv");
                break;
        }
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("mktd");
    }
}
