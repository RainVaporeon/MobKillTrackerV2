package com.spiritlight.mobkilltracker;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.*;

@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
public class TotemCommand extends CommandBase {
    private static final DecimalFormat dformat = new DecimalFormat("0.00");

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
                    "/" + getName() + " toggle - Toggles mod function\n" +
                    "/" + getName() + " cleaner - Toggles other cleaner detections\n" +
                    "/" + getName() + " trace - Traces session data\n" +
                    "/" + getName() + " export - Exports data\n" +
                    "Note: May be inaccurate and should be off if you are cleaning alone.");
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
            case "cleaner":
                Main.cleaner = !Main.cleaner;
                messenger.send("OK, now " + (Main.cleaner ? "will" : "will no longer") + " check other player tosses.");
                break;
            case "last":
                messenger.send("Dumping last session data...");
                summary(Main.cachedDrops);
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
            case "trace":
                if(args.length == 1) {
                    messenger.send("There are currently " + Main.sessionDrops.size() + (Main.sessionDrops.size() == 1 ? " stat" : " stats") + " available.");
                    messenger.send("Do /" + getName() + " trace list to see all of them in brief context.");
                    messenger.send("Or do /" + getName() + " trace <index> to see the specific of that stat.");
                    return;
                }
                if(args[1].toLowerCase(Locale.ROOT).equals("list")) {
                    messenger.send("- - - Current Session Caches - - -");
                    for(int i=0; i < Main.sessionDrops.size(); i++) {
                        final DropStatistics tmp = Main.sessionDrops.get(i);
                        messenger.send("Cache #" + i+1 + ": §r" + tmp.getKills() + "§a kills; §r" + tmp.getTotal(0) + "§a drops §7(§r" + tmp.getTotal(1) + "§7 items, §r" + tmp.getTotal(2) + "§7 ingredients)");
                        if(tmp.hasNote()) {
                            messenger.send("§7Notes of this data: " + tmp.getNote());
                        }
                    }
                    return;
                }
                int idx;
                try {
                    idx = Integer.parseInt(args[1])-1;
                } catch (NumberFormatException ex) {
                    messenger.send("Invalid index.");
                    return;
                }
                if(idx > Main.sessionDrops.size() || idx < 0) {
                    messenger.send("Index illegal. Max index allowed: " + Main.sessionDrops.size());
                    return;
                }
                summary(Main.sessionDrops.get(idx));
                break;
            case "note":
                if(args.length == 1) {
                    messenger.send("/" + getName() + " note all [note] - Sets all drop data with the note.");
                    messenger.send("/" + getName() + " note # [note] - Sets specified drop data with this note.");
                    messenger.send("If note was left empty, the notes are cleared.");
                    return;
                }
                final String note;
                if(args.length >= 3) {
                    note = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                } else note = "";
                if ("all".equals(args[1].toLowerCase(Locale.ROOT))) {
                    messenger.send("Attaching note...");
                    for (DropStatistics drops : Main.sessionDrops) {
                        drops.setNote(note);
                    }
                    messenger.send("Finished!");
                } else {
                    try {
                        idx = Integer.parseInt(args[1])-1;
                        Main.sessionDrops.get(idx).setNote(note);
                        messenger.send("Successfully attached note " + note + " to data #" + idx+1);
                    } catch (NumberFormatException|IndexOutOfBoundsException ex) {
                        messenger.send("Invalid operation.");
                        return;
                    }
                }
                break;
            case "export":
                final int ENTRY_SIZE = Main.sessionDrops.size();
                if(args.length == 1) {
                    messenger.send("/" + getName() + " export all: Exports all session data as JSON.");
                    messenger.send("/" + getName() + " export range <min> [max]: Exports session data in range as JSON.");
                    messenger.send("/" + getName() + " export last: Exports last session data as JSON.");
                    messenger.send("/" + getName() + " export #: Exports the #th index as JSON.");
                    messenger.send("View stats via /" + getName() + " trace. There are " + ENTRY_SIZE + (ENTRY_SIZE == 1 ? " entry" : " entries") + " available.");
                    return;
                } else {
                    if(Main.sessionDrops.size() == 0) {
                        messenger.send("There are no available entry yet. Try recording some?");
                        return;
                    }
                    switch(args[1].toLowerCase(Locale.ROOT)) {
                        case "all":
                            DropAnalyzer.exportDrops(new ArrayList<>(Main.sessionDrops));
                            break;
                        case "last":
                            DropAnalyzer.exportDrops(Collections.singletonList(Main.cachedDrops));
                            break;
                        case "range":
                            if(args.length == 2) {
                                messenger.send("Invalid range.");
                            } else if(args.length == 3) {
                                try {
                                    int x = Integer.parseInt(args[2])-1;
                                    final List<DropStatistics> drops = new ArrayList<>();
                                    for(int i=x; i<Main.sessionDrops.size(); i++) {
                                        drops.add(Main.sessionDrops.get(i));
                                    }
                                    DropAnalyzer.exportDrops(drops);
                                } catch (NumberFormatException ex) {
                                    messenger.send("Invalid range specified.");
                                    return;
                                } catch (IndexOutOfBoundsException e) {
                                    messenger.send("Index out of bounds. Max allowed size: " + ENTRY_SIZE);
                                    return;
                                }
                            } else try {
                                final List<DropStatistics> drops = new ArrayList<>();
                                int x = Integer.parseInt(args[2])-1;
                                int y = Integer.parseInt(args[3]);
                                for(int i=x; i<y; i++) {
                                    drops.add(Main.sessionDrops.get(i));
                                }
                                DropAnalyzer.exportDrops(drops);
                            } catch (NumberFormatException ex) {
                                messenger.send("Invalid range specified.");
                                return;
                            } catch (IndexOutOfBoundsException e) {
                                messenger.send("Index out of bounds. Max allowed size: " + ENTRY_SIZE);
                                return;
                            }
                            break;
                        default:
                            try {
                                int x = Integer.parseInt(args[1])-1;
                                DropAnalyzer.exportDrops(Collections.singletonList(Main.sessionDrops.get(x)));
                            } catch (NumberFormatException ex) {
                                messenger.send("Invalid range specified.");
                                return;
                            } catch (IndexOutOfBoundsException e) {
                                messenger.send("Index out of bounds. Max allowed size: " + ENTRY_SIZE);
                                return;
                            }
                            break;
                    }
                }
                break;
            case "__$copy.clipboard":
                messenger.send("Copied to clipboard!");
                DropAnalyzer.copyToClipboard();
                break;
            default:
                messenger.send("Invalid syntax. Try /mkt");
        }
    }

    private void summary(DropStatistics drops) {
        final AnnouncerSpirit messenger = new AnnouncerSpirit();
        final int mobKills = drops.getKills();
        final int totalDrops = drops.getTotal(0);
        final int itemDrops = drops.getTotal(1);
        final int ingDrops = drops.getTotal(2);
        final double ingRate = (ingDrops == 0 ? 0 : (double) mobKills / ingDrops);
        final double itemRate = (itemDrops == 0 ? 0 : (double) mobKills / itemDrops);
        messenger.send(
                "\n" +
                        "§3§lTotem Summary\n" +
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
                                "§rItem Rate: " + dformat.format(itemRate) + " §7(Mobs/item)" + "\n" +
                                "§rIngredient Rate: " + dformat.format(ingRate) + " §7(Mobs/Ingredient)" : ""));
    }
}
