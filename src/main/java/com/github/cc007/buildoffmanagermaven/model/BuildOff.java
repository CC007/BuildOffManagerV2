/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

import com.github.cc007.buildoffmanagermaven.BuildOffManager;
import com.github.cc007.buildoffmanagermaven.utils.LocationHelper;
import com.github.cc007.buildoffmanagermaven.utils.PersistencyHelper;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final Location location;
    private final ThemeSign themeSign;
    private final OverviewBoard board;
    private Date buildOffEnd;
    private final int plotsPerRow;
    private final byte direction;
    private final int plotSize;
    private final int pathWidth;
    public static final int BORDER_WIDTH = 1;

    public BuildOff(int plotCount, Location location, byte direction, Location themeSignLocation, byte themeSignDirection, Location overviewBoardLocation, byte overviewBoardDirection, int plotsPerRow, int plotSize, int pathWidth) {
        this.plots = new HashMap<>();
        this.plotsPerRow = plotsPerRow;
        this.buildOffEnd = null;
        this.direction = direction;
        this.plotSize = plotSize;
        this.pathWidth = pathWidth;
        this.resetContestants = new HashSet<>();
        this.state = BuildOffState.DISABLED;
        this.location = location;
        for (int i = 0; i < plotCount; i++) {
            plots.put(i, new Plot(i, getPlotLocation(i), direction, plotSize));
        }
        this.themeSign = new ThemeSign("Theme", themeSignLocation, themeSignDirection);
        this.board = new OverviewBoard(plots, overviewBoardLocation, overviewBoardDirection, plotsPerRow);
    }

    public BuildOff(Map<Integer, Plot> plots, Set<Contestant> resetContestants, BuildOffState state, Location location, byte direction, ThemeSign themeSign, OverviewBoard board, Date buildOffEnd, int plotsPerRow, int plotSize, int pathWidth) {
        this.plots = plots;
        this.resetContestants = resetContestants;
        this.state = state;
        this.location = location;
        this.themeSign = themeSign;
        this.board = board;
        this.buildOffEnd = buildOffEnd;
        this.plotsPerRow = plotsPerRow;
        this.direction = direction;
        this.plotSize = plotSize;
        this.pathWidth = pathWidth;
    }

    public boolean openBO() {
        if (state == BuildOffState.DISABLED) {
            state = BuildOffState.OPENED;
            PersistencyHelper.saveBuildOff();
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

            // set end date to auto close bo
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, 1);
            buildOffEnd = c.getTime();

            RegionManager rgm = WGBukkit.getRegionManager(location.getWorld());
            rgm.getRegion("contestcomplete").setPriority(0);
            try {
                rgm.save();
            } catch (StorageException ex) {
                Logger.getLogger(Plot.class.getName()).log(Level.SEVERE, null, ex);
            }
            themeSign.update(false);
            return true;
        }
        return false;
    }

    public boolean closeBO() {
        if (state == BuildOffState.RUNNING) {
            state = BuildOffState.CLOSED;
            RegionManager rgm = WGBukkit.getRegionManager(location.getWorld());
            rgm.getRegion("contestcomplete").setPriority(3);
            try {
                rgm.save();
            } catch (StorageException ex) {
                Logger.getLogger(Plot.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            if (player != null && plotEntry.getValue().getContestant() != null && player.equals(Bukkit.getPlayer(plotEntry.getValue().getContestant().getUuid()))) {
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
            if (name != null && plotEntry.getValue().getContestant() != null && name.equals(plotEntry.getValue().getContestant().getName())) {
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

    public boolean resetBO() {
        if (state == BuildOffState.CLOSED) {
            state = BuildOffState.DISABLED;
            for (int plotNr : plots.keySet()) {
                resetPlot(plotNr);
            }
            clearResetContestants();
            themeSign.update(true);
            return true;
        }
        return false;
    }

    public int joinPlot(Player player) {
        if (state != BuildOffState.OPENED && state != BuildOffState.RUNNING) {
            return 0;
        }
        if (getPlot(player) != null) {
            return -2;
        }
        for (int plotNr = 0; plotNr < plots.size(); plotNr++) {
            if (plots.get(plotNr).getContestant() == null) {
                plots.get(plotNr).setContestant(new Contestant(player.getName(), player.getDisplayName(), player.getUniqueId()));
                board.update();
                return plotNr + 1;
            }
        }
        return -1;

    }

    public Boolean leavePlot(Player player) {
        if (state == BuildOffState.OPENED) {
            Boolean result = resetPlot(player);
            board.update();
            return result;
        }
        return null;
    }

    public Plot getPlot(int plotNr) {
        return plots.get(plotNr);
    }

    public Plot getPlot(Player player) {
        for (Plot plot : plots.values()) {
            if (player != null && plot.getContestant() != null && plot.getContestant() != null && player.equals(Bukkit.getPlayer(plot.getContestant().getUuid()))) {
                return plot;
            }
        }
        return null;
    }

    public Plot getPlot(String name) {
        for (Plot plot : plots.values()) {
            if (name != null && plot.getContestant() != null && plot.getContestant() != null && name.equals(plot.getContestant().getName())) {
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

    public void initPlots() {
        initPlots(0, 3);
    }
    public void initPlots(int startPlot, int priority) {
        for (int i = startPlot; i < plots.size(); i++) {
            BuildOffManager.getPlugin().getLogger().info("Init plot " + i);
            plots.get(i).init();
        }
        BlockVector buildArea1 = new BlockVector(location.getBlockX(), 0, location.getBlockZ());

        int boSizeX = (plotsPerRow * plotSize) + (plotsPerRow * BORDER_WIDTH * 2) + ((plotsPerRow - 1) * pathWidth) - 1;
        int boSizeY = (plotsPerColumn() * plotSize) + (plotsPerColumn() * BORDER_WIDTH * 2) + ((plotsPerColumn() - 1) * pathWidth) - 1;

        Location buildAreaLoc2 = LocationHelper.getLocation(location, boSizeX, boSizeY, 0, direction);
        BlockVector buildArea2 = new BlockVector(buildAreaLoc2.getBlockX(), 255, buildAreaLoc2.getBlockZ());
        ProtectedCuboidRegion boAreaPcr = new ProtectedCuboidRegion("contestcomplete", buildArea1, buildArea2);
        boAreaPcr.setPriority(priority);
        RegionManager rgm = WGBukkit.getRegionManager(location.getWorld());
        rgm.removeRegion("contestcomplete");
        rgm.addRegion(boAreaPcr);
        try {
            rgm.save();
        } catch (StorageException ex) {
            Logger.getLogger(Plot.class.getName()).log(Level.SEVERE, null, ex);
        }
        board.update();
        themeSign.update(true);
    }

    public void extendTime(int minutes){
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MINUTE, minutes);
            buildOffEnd = c.getTime();
    }
    
    public Location getPlotLocation(int plotNr) {
        int xPlot = plotNr % plotsPerRow;
        int yPlot = plotNr / plotsPerRow;

        int xOffset = xPlot * (BORDER_WIDTH * 2 + plotSize + pathWidth);
        int yOffset = yPlot * (BORDER_WIDTH * 2 + plotSize + pathWidth);

        return LocationHelper.getLocation(location, xOffset, yOffset, 0, direction);
    }

    private int plotsPerColumn() {
        return (int) Math.ceil(((double) plots.size()) / plotsPerRow);
    }

    public Location getLocation() {
        return location;
    }

    public BuildOffState getState() {
        return state;
    }

    public int getPlotsPerRow() {
        return plotsPerRow;
    }

    public OverviewBoard getOverviewBoard() {
        return board;
    }

    public byte getDirection() {
        return direction;
    }

    public int getPlotSize() {
        return plotSize;
    }
    
    
    
    

}
