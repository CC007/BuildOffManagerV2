/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

import com.github.cc007.buildoffmanagermaven.BuildOffManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class BuildOff {

    private final Map<Integer, Plot> plots;
    private final Set<Contestant> resetContestants;
    private BuildOffState state;
    private Location location;
    private ThemeSign themeSign;
    private OverviewBoard board;
    private Date buildOffEnd;
    private int plotsPerRow;

    public BuildOff() {
        this.plots = new HashMap<>();
        this.resetContestants = new HashSet<>();
        this.state = BuildOffState.DISABLED;
    }

    public BuildOff(Map<Integer, Plot> plots, Set<Contestant> resetContestants, BuildOffState state) {
        this.plots = plots;
        this.resetContestants = resetContestants;
        this.state = state;
    }

    public boolean openBO() {
        if (state == BuildOffState.DISABLED) {
            state = BuildOffState.OPENED;
            return true;
        }
        return false;
    }

    public boolean startBO() {
        if (state == BuildOffState.DISABLED) {
            openBO();
        }
        if (state == BuildOffState.OPENED) {
            state = BuildOffState.RUNNING;
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, 1);
            buildOffEnd = c.getTime();
            return true;
        }
        return false;
    }

    public boolean closeBO() {
        if (state == BuildOffState.RUNNING) {
            state = BuildOffState.CLOSED;
            return true;
        }
        return false;
    }

    public boolean resetPlot(int plotNr) {
        Plot plot = plots.get(plotNr);
        if (plot != null) {
            plot.reset();
            return true;
        }

        return false;
    }

    public boolean resetPlot(int plotNr, CommandSender sender) {
        if (resetPlot(plotNr)) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "Plot " + plotNr + " is reset.");
            return true;
        }
        sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "Plot " + plotNr + " doesn't exist.");
        return false;
    }

    public boolean resetPlot(Player player) {
        for (Map.Entry<Integer, Plot> plotEntry : plots.entrySet()) {
            if (player != null && player.equals(Bukkit.getPlayer(plotEntry.getValue().getContestant().getUuid()))) {
                return resetPlot(plotEntry.getKey());

            }
        }
        return false;
    }

    public boolean resetPlot(Player player, CommandSender sender) {
        if (resetPlot(player)) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "The plot of " + player.getName() + " is reset.");
            return true;
        }
        sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + player.getName() + " doesn't have a Build Off plot.");
        return false;
    }

    public boolean resetPlot(String name) {
        for (Map.Entry<Integer, Plot> plotEntry : plots.entrySet()) {
            if (name != null && name.equals(plotEntry.getValue().getContestant().getName())) {
                return resetPlot(plotEntry.getKey());
            }
        }
        return false;
    }

    public boolean resetPlot(String name, CommandSender sender) {
        if (resetPlot(name)) {
            sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + "The plot of " + name + " is reset.");
            return true;
        }
        sender.sendMessage(BuildOffManager.pluginChatPrefix(false) + name + " doesn't have a Build Off plot. Did you spell the name correctly?");
        return false;
    }

    public void resetBO() {
        if (state == BuildOffState.CLOSED) {
            state = BuildOffState.DISABLED;
            for (int plotNr : plots.keySet()) {
                resetPlot(plotNr);
            }
        }
    }

    public int joinPlot(Player player) {
        if (getPlot(player) != null) {
            return -2;
        }
        for (int plotNr = 1; plotNr <= plots.size(); plotNr++) {
            if (plots.get(plotNr).getContestant() == null) {
                plots.get(plotNr).setContestant(new Contestant(player.getName(), player.getDisplayName(), player.getUniqueId()));
                return plotNr;
            }
        }
        return -1;
    }

    public Boolean leavePlot(Player player) {
        if (state == BuildOffState.OPENED) {
            return resetPlot(player);
        }
        return null;
    }

    public Plot getPlot(int plotNr) {
        return plots.get(plotNr);
    }

    public Plot getPlot(Player player) {
        for (Plot plot : plots.values()) {
            if (player != null && player.equals(Bukkit.getPlayer(plot.getContestant().getUuid()))) {
                return plot;
            }
        }
        return null;
    }

    public Plot getPlot(String name) {
        for (Plot plot : plots.values()) {
            if (name != null && name.equals(plot.getContestant().getName())) {
                return plot;
            }
        }
        return null;
    }

    public void addResetContestant(Contestant contestant) {
        resetContestants.add(contestant);
    }

    public void clearResetContestants() {
        resetContestants.clear();
    }

    public ThemeSign getThemeSign() {
        return themeSign;
    }

    public int secondsToBOEnd() {
        return (int) ((buildOffEnd.getTime() - new Date().getTime()) / 1000);
    }

    public int minutesToBOEnd() {
        return secondsToBOEnd() / 60;
    }

    public int hoursToBOEnd() {
        return minutesToBOEnd() / 60;
    }

    public Map<Integer, Plot> getPlots() {
        return plots;
    }

    public Set<Contestant> getResetContestants() {
        return resetContestants;
    }

}
