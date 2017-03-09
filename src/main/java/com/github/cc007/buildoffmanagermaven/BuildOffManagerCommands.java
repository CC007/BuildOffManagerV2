/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven;

import com.github.cc007.buildoffmanagermaven.model.BuildOff;
import com.github.cc007.buildoffmanagermaven.model.BuildOffState;
import com.github.cc007.buildoffmanagermaven.model.Contestant;
import com.github.cc007.buildoffmanagermaven.model.Plot;
import com.github.cc007.buildoffmanagermaven.utils.LocationHelper;
import com.github.cc007.buildoffmanagermaven.utils.PersistencyHelper;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class BuildOffManagerCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        if (!"bo".equals(cmd.getName())) {
            return false;
        }

        if (!sender.hasPermission("buildoffmanager.player")) {
            message(sender, "You don't have permission to do this command!", ChatColor.RED);
            return true;
        }

        if (args.length == 0) {
            return onHelpCommand(sender, cmd, cmdAlias, args);
        }

        String subCmd = args[0];
        switch (subCmd.toLowerCase()) {
            case "help":
                return onHelpCommand(sender, cmd, cmdAlias, args);
            case "join":
                return onJoinCommand(sender, cmd, cmdAlias, args);
            case "leave":
                return onLeaveCommand(sender, cmd, cmdAlias, args);
            case "theme":
                return onThemeCommand(sender, cmd, cmdAlias, args);
            case "tp":
            case "tpplot":
                return onTpPlotCommand(sender, cmd, cmdAlias, args);
            case "time":
            case "endtime":
                return onTimeCommand(sender, cmd, cmdAlias, args);
        }

        if (!sender.hasPermission("buildoffmanager.staff")) {
            message(sender, "You don't have permission to do this command!", ChatColor.RED);
            return true;
        }

        switch (subCmd.toLowerCase()) {
            case "init":
            case "initialize":
                return onInitCommand(sender, cmd, cmdAlias, args);
            case "open":
                return onOpenCommand(sender, cmd, cmdAlias, args);
            case "start":
                return onStartCommand(sender, cmd, cmdAlias, args);
            case "end":
            case "stop":
            case "close":
                return onStopCommand(sender, cmd, cmdAlias, args);
            case "reset":
                return onResetCommand(sender, cmd, cmdAlias, args);
            case "forceleave":
            case "resetplot":
                return onResetPlotCommand(sender, cmd, cmdAlias, args);
            case "bugplayers":
            case "listplayers":
            case "who":
            case "list":
                return onListPlayersCommand(sender, cmd, cmdAlias, args);
            case "settheme":
                return onSetThemeCommand(sender, cmd, cmdAlias, args);
            case "expandsize":
                return onExpandSizeCommand(sender, cmd, cmdAlias, args);
            case "reload":
                return onReloadCommand(sender, cmd, cmdAlias, args);
            case "extendtime":
                return onExtendTimeCommand(sender, cmd, cmdAlias, args);
            case "mail":
            case "sendmail":
                return onMailCommand(sender, cmd, cmdAlias, args);
            case "cleanlegacy":
                return onCleanLegacyCommand(sender, cmd, cmdAlias, args);
        }
        message(sender, "Unknown command! Do /bo help.", ChatColor.RED);
        return false;
    }

    public boolean onHelpCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        String help = ChatColor.YELLOW + " ---- " + ChatColor.GOLD + "Build Off Help" + ChatColor.YELLOW + " ---- \n"
                + ChatColor.GOLD + "/bo join" + ChatColor.RESET + ": Join the Build Off.\n"
                + ChatColor.GOLD + "/bo leave" + ChatColor.RESET + ": Leave the Build Off.\n"
                + ChatColor.GOLD + "/bo theme" + ChatColor.RESET + ": Displays the Theme of the Build Off.\n"
                + ChatColor.GOLD + "/bo tp" + ChatColor.RESET + ": Teleport to your plot.\n"
                + ChatColor.GOLD + "/bo tp <number>" + ChatColor.RESET + ": Teleport to plot <number>.\n"
                + ChatColor.GOLD + "/bo tp <name>" + ChatColor.RESET + ": Teleport to the plot of <name>.\n"
                + ChatColor.GOLD + "/bo time" + ChatColor.RESET + ": Displays the time until the end of the Build Off.\n"
                + ChatColor.GOLD + "/bo help" + ChatColor.RESET + ": Displays this help message.";
        if (sender.hasPermission("buildoffmanager.staff")) {
            help += "\n" + ChatColor.YELLOW + " ---- " + ChatColor.GOLD + "BO Admin Commands" + ChatColor.YELLOW + " ---- \n"
                    + ChatColor.GOLD + "/bo init" + ChatColor.RESET + ": Initialize the BO area from your current location.\n"
                    + ChatColor.GOLD + "/bo init <worldName> <x> <y> <z>" + ChatColor.RESET + ": Initialize the BO area from a given location.\n"
                    + ChatColor.GOLD + "/bo open" + ChatColor.RESET + ": Open the BO to allow all players to enroll.\n"
                    + ChatColor.GOLD + "/bo start" + ChatColor.RESET + ": Start the BO to allow player to build.\n"
                    + ChatColor.GOLD + "/bo stop" + ChatColor.RESET + ": End the BO to prevent players from building.\n"
                    + ChatColor.GOLD + "/bo reset" + ChatColor.RESET + ": Reset the entire BO area, to prepare for the next BO.\n"
                    + ChatColor.GOLD + "/bo resetplot <nr>" + ChatColor.RESET + ": Reset plot number <nr>.\n"
                    + ChatColor.GOLD + "/bo listplayers" + ChatColor.RESET + ": List enrolled and not enrolled players.\n"
                    + ChatColor.GOLD + "/bo settheme <theme>" + ChatColor.RESET + ": Sets the theme for the next BO.\n"
                    + ChatColor.GOLD + "/bo expandsize <x>" + ChatColor.RESET + ": Expands the BO area with <x> plots.\n"
                    + ChatColor.GOLD + "/bo extendtime <time>" + ChatColor.RESET + ": Adds <time> tot he current BO.\n"
                    + ChatColor.GOLD + "/bo mail" + ChatColor.RESET + ": Mails all contestants if they want their plot saved.\n"
                    + ChatColor.GOLD + "/bo reload" + ChatColor.RESET + ": Reloads the config of this plugin.\n"
                    + ChatColor.GOLD + "/bo cleanlegacy" + ChatColor.RESET + ": Remove WorldGuard regions from older versions. Note that you should perform this command before doing /bo init. Otherwise the new regions will also be cleared.";
        }
        sender.sendMessage(help);
        return true;
    }

    public boolean onOpenCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (bo != null && bo.openBO()) {
            Bukkit.getServer().broadcastMessage(BuildOffManager.pluginChatPrefix(true) + ChatColor.GREEN + "The Build Off has been opened. Do " + ChatColor.BLUE + "/bo join" + ChatColor.GREEN + " to enroll yourself!");
            PersistencyHelper.saveBuildOff();
        } else {
            message(sender, "The Build Off can only be opened if it has been initialized or reset", ChatColor.RED);
        }
        return true;
    }

    public boolean onStartCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (bo != null && bo.startBO()) {
            Bukkit.getServer().broadcastMessage(BuildOffManager.pluginChatPrefix(true) + ChatColor.GREEN + "The Build Off has started! You will have 24 hours to complete your build. The theme is: " + ChatColor.BLUE + ChatColor.BOLD + bo.getThemeSign().getTheme());
            PersistencyHelper.saveBuildOff();
        } else {
            message(sender, "The Build Off can only be started if it has been reset", ChatColor.RED);
        }
        return true;
    }

    public boolean onStopCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (bo != null && bo.closeBO()) {
            if (BuildOffManager.getPlugin().getConfig().getBoolean("stream.enabled")) {
                BuildOffManager.getPlugin().getServer().broadcastMessage(BuildOffManager.pluginChatPrefix(true) + ChatColor.GREEN + "The Build Off has ended! Judging will commence soon. You can watch the judging live at: " + ChatColor.BLUE + BuildOffManager.getPlugin().getConfig().getString("stream.link"));
            } else {
                BuildOffManager.getPlugin().getServer().broadcastMessage(BuildOffManager.pluginChatPrefix(true) + ChatColor.GREEN + "The Build Off has ended! Judging will commence soon.");
            }
            PersistencyHelper.saveBuildOff();
        } else {
            message(sender, "The Build Off can only be stopped if it has been started", ChatColor.RED);
        }
        return true;
    }

    public boolean onResetCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (bo != null && bo.resetBO()) {
            message(sender, "All plots have been reset and are ready for the next Build Off.", ChatColor.GREEN);
            PersistencyHelper.saveBuildOff();
        } else {
            message(sender, "The Build Off can only be reset if it has been closed", ChatColor.RED);
        }
        return true;
    }

    public boolean onResetPlotCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (args.length < 2) {
            message(sender, "You didn't specify which plot to reset!", ChatColor.RED);
            return false;
        }
        if (bo == null) {
            message(sender, "No Build Off has been initialized!", ChatColor.RED);
            return true;
        }
        if (args[1].matches("[0-9]+")) {
            bo.resetPlot(Integer.parseInt(args[1]), sender);
        } else {
            bo.resetPlot(args[1], sender);
        }
        PersistencyHelper.saveBuildOff();
        return true;
    }

    public boolean onJoinCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (!(sender instanceof Player)) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "Johnsole is not allowed to join the BO");
            return false;
        }
        if (bo == null) {
            message(sender, "You can't join the Build Off at this time.", ChatColor.RED);
            return true;
        }
        Player player = (Player) sender;
        int plotNr = bo.joinPlot(player);
        switch (plotNr) {
            case -2:
                message(sender, "You are already enrolled to the Build Off", ChatColor.GOLD);
                return true;
            case -1:
                message(sender, "All Build Off plots are taken. You can bug an admin to expand the Build Off.", ChatColor.RED);
                return true;
            case 0:
                message(sender, "You cannot join the Build Off at this time.", ChatColor.RED);
                return true;
            default:
                message(sender, "You successfully joined the Build Off. Your plot number is " + plotNr, ChatColor.GREEN);
                PersistencyHelper.saveBuildOff();

        }
        return true;
    }

    public boolean onLeaveCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (!(sender instanceof Player)) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "Johnsole is not allowed to leave the BO");
            return false;
        }
        if (bo == null) {
            message(sender, "You did not join the Build Off yet and are therefore unable to leave it.", ChatColor.RED);
            return true;
        }
        Player player = (Player) sender;
        Boolean result = BuildOffManager.getPlugin().getActiveBuildOff().leavePlot(player);
        if (result == null) {
            message(sender, "You are not allowed to leave the Build Off once it has been started.", ChatColor.RED);
            return true;
        }
        if (!result) {
            message(sender, "You did not join the Build Off yet and are therefore unable to leave it.", ChatColor.RED);
            return true;
        }

        message(sender, "You successfully left the Build Off.", ChatColor.GREEN);
        PersistencyHelper.saveBuildOff();
        return true;
    }

    private boolean onInitCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        Location location;
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "Johnsole is not allowed to init the BO without specifying a world and location");
                return false;
            }
            Player player = (Player) sender;
            location = player.getLocation();
        } else if (args.length == 5) {
            int x, y, z;
            if (((Bukkit.getWorld(args[1]) == null || !args[2].matches("-?[0-9]+")) || !args[3].matches("-?[0-9]+")) || !args[4].matches("-?[0-9]+")) {
                message(sender, "You didn't use the command properly. Use /bo init <worldName> <x> <y> <z>", ChatColor.RED);
                return true;
            } else {
                World world = Bukkit.getWorld(args[1]);
                x = Integer.parseInt(args[2]);
                y = Integer.parseInt(args[3]);
                z = Integer.parseInt(args[4]);
                location = new Location(world, x, y, z);
            }
        } else {
            message(sender, "You didn't use the command properly. Use /bo init or use /bo init <worldName> <x> <y> <z>", ChatColor.RED);
            return true;
        }
        FileConfiguration config = BuildOffManager.getPlugin().getConfig();
        int plotCount = config.getInt("plotCount");
        int plotsPerRow = config.getInt("plotsPerRow");
        int plotSize = config.getInt("plotSize");
        int pathWidth = config.getInt("pathWidth");

        String directionName = config.getString("direction");
        boolean mirrored = config.getBoolean("mirrored");
        byte direction = nameToDirection(directionName, mirrored);

        Location themeSignLocation = LocationHelper.getLocation(location, config.getInt("themeSign.relativeX"), config.getInt("themeSign.relativeZ"), config.getInt("themeSign.relativeY"), direction);
        String themeSignDirectionName = config.getString("themeSign.direction");
        byte themeSignDirection = nameToSignDirection(themeSignDirectionName, false);

        Location overviewBoardLocation = LocationHelper.getLocation(location, config.getInt("overviewBoard.relativeX"), config.getInt("overviewBoard.relativeZ"), config.getInt("overviewBoard.relativeY"), direction);
        String overviewBoardDirectionName = config.getString("overviewBoard.direction");
        boolean overviewBoardMirrored = config.getBoolean("overviewBoard.mirrored");
        byte overviewBoardDirection = nameToSignDirection(overviewBoardDirectionName, overviewBoardMirrored);

        BuildOffManager.getPlugin().setActiveBuildOff(
                new BuildOff(plotCount,
                        location, direction,
                        themeSignLocation, themeSignDirection,
                        overviewBoardLocation, overviewBoardDirection,
                        plotsPerRow, plotSize, pathWidth));
        BuildOffManager.getPlugin().getActiveBuildOff().initPlots();
        PersistencyHelper.saveBuildOff();

        return true;

    }

    private boolean onListPlayersCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (bo == null) {
            message(sender, "No Build Off has been initialized!", ChatColor.RED);
            return true;
        }
        List<String> onlineContestants = new LinkedList<>();
        List<String> offlineContestants = new LinkedList<>();
        List<String> onlineNonContestants = new LinkedList<>();
        List<String> resetContestants = new LinkedList<>();

        for (Plot plot : bo.getPlots().values()) {
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(plot.getContestant().getName()))) {
                onlineContestants.add(plot.getContestant().getName());
            } else {
                offlineContestants.add(plot.getContestant().getName());
            }
        }
        BuildOffManager.getPlugin().getActiveBuildOff().getResetContestants().stream().map(Contestant::getName).forEach((name) -> resetContestants.add(name));
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlineContestants.contains(onlinePlayer.getName()) && !resetContestants.contains(onlinePlayer.getName())) {
                onlineNonContestants.add(onlinePlayer.getName());
            }
        }
        message(sender, "Player list:", ChatColor.GOLD);
        sender.sendMessage("Online contestants:" + ChatColor.GREEN + String.join(", ", onlineContestants));
        sender.sendMessage("Offline contestants:" + ChatColor.YELLOW + String.join(", ", offlineContestants));
        sender.sendMessage("Players that left the bo: " + ChatColor.GRAY + String.join(", ", resetContestants));
        sender.sendMessage("Other online players: " + ChatColor.RED + String.join(", ", onlineNonContestants));
        return true;
    }

    private boolean onTpPlotCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (!(sender instanceof Player)) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "Johnsole cannot teleport");
            return true;
        }
        if (bo == null) {
            message(sender, "You didn't join the Build Off yet, so you can't be teleported to your plot.", ChatColor.RED);
            return true;
        }
        Player player = (Player) sender;
        Plot tpPlot;
        if (args.length < 2) {
            tpPlot = bo.getPlot(sender.getName());
            if (tpPlot == null) {
                message(sender, "You didn't join the Build Off yet, so you can't be teleported to your plot.", ChatColor.RED);
                return true;
            }
        } else if (args[1].matches("[0-9]+")) {
            tpPlot = bo.getPlot(Integer.parseInt(args[1]) - 1);
            if (tpPlot == null) {
                message(sender, "Choose a plot number between 1 and " + bo.getPlots().size() + ".", ChatColor.RED);
                return true;
            }
        } else {
            tpPlot = bo.getPlot(args[1]);
            if (tpPlot == null) {
                message(sender, args[1] + " didn't join the Build Off yet, so you can't be teleported to their plot.", ChatColor.RED);
                return true;
            }
        }
        Location tpLocation = LocationHelper.getLocation(tpPlot.getPlotLocation(), -3, -3, 0, tpPlot.getDirection());
        tpLocation.setYaw(45.0f);
        tpLocation.setPitch(0.0f);
        player.teleport(tpLocation);
        return true;
    }

    private boolean onThemeCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (bo == null || (bo.getState() != BuildOffState.RUNNING && bo.getState() != BuildOffState.CLOSED)) {
            message(sender, "The theme is still a secret at this time. Wait for the Build Off to start.", ChatColor.RED);
            return true;
        }
        if (bo.getState() == BuildOffState.RUNNING) {
            message(sender, "The theme is: " + ChatColor.BLUE + ChatColor.BOLD + BuildOffManager.getPlugin().getActiveBuildOff().getThemeSign().getTheme(), ChatColor.WHITE);
        } else {
            message(sender, "The theme was: " + ChatColor.BLUE + ChatColor.BOLD + BuildOffManager.getPlugin().getActiveBuildOff().getThemeSign().getTheme(), ChatColor.WHITE);
        }
        return true;
    }

    private boolean onSetThemeCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (bo == null) {
            message(sender, "No Build Off has been initialized!", ChatColor.RED);
            return true;
        }
        String[] themeArray = Arrays.copyOfRange(args, 1, args.length);
        bo.getThemeSign().setTheme(String.join(" ", themeArray));
        PersistencyHelper.saveBuildOff();
        return true;
    }

    private boolean onExpandSizeCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        PersistencyHelper.saveBuildOff();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean onReloadCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOffManager.getPlugin().reloadConfig();
        if (sender instanceof Player) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(true) + ChatColor.GREEN + "The config has been reloaded");
        } else {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "The config has been reloaded");
        }
        return true;
    }

    private boolean onExtendTimeCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        PersistencyHelper.saveBuildOff();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean onTimeCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (bo == null || bo.getState() != BuildOffState.RUNNING) {
            if (bo != null && bo.getState() == BuildOffState.CLOSED) {
                message(sender, "The Build Off has already ended.", ChatColor.RED);
            } else {
                message(sender, "The Build Off hasn't started yet.", ChatColor.RED);
            }
            return true;
        }
        String totalTime = "";
        if (bo.hoursToBOEnd() > 0) {
            totalTime = bo.hoursToBOEnd() + "h ";
        }
        if (!"".equals(totalTime) || bo.minutesToBOEnd() > 0) {
            totalTime += (bo.minutesToBOEnd() % 60) + "m ";
        }
        if (!"".equals(totalTime) || bo.secondsToBOEnd() > 0) {
            totalTime += (bo.secondsToBOEnd() % 60) + "s";
        }
        message(sender, "You have " + totalTime + " left to complete your build.", ChatColor.GOLD);
        return true;
    }

    private boolean onMailCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean onCleanLegacyCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        BuildOffManager.getPlugin().legacyClear();
        message(sender, "The legacy regions have been cleared. Note that this will also clear the regions for this plugin, so you are required to do /bo init after this command.", ChatColor.GOLD);
        return true;
    }

    public static void message(CommandSender sender, String message, ChatColor color) {
        if (sender instanceof Player) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(true) + color + message);
        } else {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + message);
        }
    }

    private byte nameToDirection(String directionName, boolean mirrored) {
        byte direction = 0;
        switch (directionName.toLowerCase()) {
            case "southwest":
                direction += 4;
                break;
            case "northwest":
                direction += 5;
                break;
            case "northeast":
                direction += 6;
                break;
            case "southeast":
                direction += 7;
                break;
        }
        if (mirrored) {
            direction -= 4;
        }
        return direction;
    }

    private byte nameToSignDirection(String directionName, boolean mirrored) {
        byte direction = 0;
        switch (directionName.toLowerCase()) {
            case "south":
                direction += 4;
                break;
            case "west":
                direction += 5;
                break;
            case "north":
                direction += 6;
                break;
            case "east":
                direction += 7;
                break;
        }
        if (mirrored) {
            direction -= 4;
        }
        return direction;
    }

}
