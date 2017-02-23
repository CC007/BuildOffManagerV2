/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven;

import com.github.cc007.buildoffmanagermaven.model.Contestant;
import com.github.cc007.buildoffmanagermaven.model.Plot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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

        //Todo perm check
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
        sender.sendMessage(ChatColor.RED + "Unknown command! Do /bo help.");
        return false;
    }

    public boolean onHelpCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        //TODO help page
        return true;
    }

    public boolean onOpenCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        if (BuildOffManager.getPlugin().getActiveBuildOff().openBO()) {
            Bukkit.getServer().broadcastMessage(BuildOffManager.pluginChatPrefix(true) + ChatColor.GREEN + "The Build Off has been opened. Do " + ChatColor.BLUE + "/bo join" + ChatColor.GREEN + " to enroll yourself!");
        } else {
            message(sender, "The Build Off can only be opened if it has been reset", ChatColor.RED);
        }
        return true;
    }

    public boolean onStartCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        if (BuildOffManager.getPlugin().getActiveBuildOff().startBO()) {
            Bukkit.getServer().broadcastMessage(BuildOffManager.pluginChatPrefix(true) + ChatColor.GREEN + "The Build Off has started! You will have 24 hours to complete your build. The theme is: " + ChatColor.BLUE + ChatColor.BOLD + BuildOffManager.getPlugin().getActiveBuildOff().getThemeSign().getTheme());
        } else {
            message(sender, "The Build Off can only be started if it has been reset", ChatColor.RED);
        }
        return true;
    }

    public boolean onStopCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        if (BuildOffManager.getPlugin().getActiveBuildOff().closeBO()) {
            if (BuildOffManager.getPlugin().getConfig().getBoolean("stream.enabled")) {
                BuildOffManager.getPlugin().getServer().broadcastMessage(BuildOffManager.pluginChatPrefix(true) + "The Build Off has ended! Judging will commence soon. You can watch the judging live at: " + ChatColor.BLUE + BuildOffManager.getPlugin().getConfig().getString("stream.link"));
            } else {
                BuildOffManager.getPlugin().getServer().broadcastMessage(BuildOffManager.pluginChatPrefix(true) + "The Build Off has ended! Judging will commence soon.");
            }
        } else {
            message(sender, "The Build Off can only be stopped if it has been started", ChatColor.RED);
        }
        return true;
    }

    public boolean onResetCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        if (BuildOffManager.getPlugin().getActiveBuildOff().closeBO()) {
            message(sender, "All plots have been reset and are ready for the next Build Off.", ChatColor.GREEN);
        } else {
            message(sender, "The Build Off can only be reset if it has been closed", ChatColor.RED);
        }
        return true;
    }

    public boolean onResetPlotCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        if (args.length < 2) {
            message(sender, "You didn't specify which plot to reset!", ChatColor.RED);
            return false;
        }

        if (args[1].matches("[0-9]+")) {
            BuildOffManager.getPlugin().getActiveBuildOff().resetPlot(Integer.parseInt(args[1]), sender);
        } else {
            BuildOffManager.getPlugin().getActiveBuildOff().resetPlot(args[1], sender);
        }

        return true;
    }

    public boolean onJoinCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "Johnsole is not allowed to join the BO");
            return false;
        }
        Player player = (Player) sender;
        int plotNr = BuildOffManager.getPlugin().getActiveBuildOff().joinPlot(player);
        switch (plotNr) {
            case -2:
                message(sender, "You are already enrolled to the Build Off", ChatColor.GOLD);
                return false;
            case -1:
                message(sender, "All Build Off plots are taken. You can bug an admin to expand the Build Off.", ChatColor.RED);
                return false;
            default:
                message(sender, "You successfully joined the Build Off. Your plot number is " + plotNr, ChatColor.GREEN);

        }
        return true;
    }

    public boolean onLeaveCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "Johnsole is not allowed to leave the BO");
            return false;
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
        return true;
    }

    private boolean onInitCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        return true;
        
    }

    private boolean onListPlayersCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        List<String> onlineContestants = new LinkedList<>();
        List<String> offlineContestants = new LinkedList<>();
        List<String> onlineNonContestants = new LinkedList<>();
        List<String> resetContestants = new LinkedList<>();
        for (Plot plot : BuildOffManager.getPlugin().getActiveBuildOff().getPlots().values()) {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean onThemeCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        message(sender, "The theme is: " + ChatColor.BLUE + ChatColor.BOLD + BuildOffManager.getPlugin().getActiveBuildOff().getThemeSign().getTheme(), ChatColor.WHITE);
        return true;
    }

    private boolean onSetThemeCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        String[] themeArray = Arrays.copyOfRange(args, 1, args.length);
        BuildOffManager.getPlugin().getActiveBuildOff().getThemeSign().setTheme(String.join(" ", themeArray));
        return true;
    }

    private boolean onExpandSizeCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean onTimeCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        String totalTime = "";
        if (BuildOffManager.getPlugin().getActiveBuildOff().hoursToBOEnd() > 0) {
            totalTime = BuildOffManager.getPlugin().getActiveBuildOff().hoursToBOEnd() + "h ";
        }
        if (!"".equals(totalTime) || BuildOffManager.getPlugin().getActiveBuildOff().minutesToBOEnd() > 0) {
            totalTime += (BuildOffManager.getPlugin().getActiveBuildOff().minutesToBOEnd() % 60) + "m ";
        }
        if (!"".equals(totalTime) || BuildOffManager.getPlugin().getActiveBuildOff().secondsToBOEnd() > 0) {
            totalTime += (BuildOffManager.getPlugin().getActiveBuildOff().secondsToBOEnd() % 60) + "s";
        }
        message(sender, "You have" + totalTime + " left to complete your build.", ChatColor.GOLD);
        return true;
    }

    private boolean onMailCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void message(CommandSender sender, String message, ChatColor color) {
        if (sender instanceof Player) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(true) + color + message);
        } else {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + message);
        }
    }

    private boolean onCleanLegacyCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
