/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

import com.github.cc007.buildoffmanagermaven.BuildOffManager;
import com.github.cc007.buildoffmanagermaven.utils.LocationHelper;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class OverviewBoard {

    private Map<Integer, Plot> plots;
    private Location location;
    private byte direction;
    private int plotsPerRow;

    public OverviewBoard(Map<Integer, Plot> plots, Location location, byte direction, int plotsPerRow) {
        this.plots = plots;
        this.location = location;
        this.direction = direction;
        this.plotsPerRow = plotsPerRow;
    }

    public Location getLocation() {
        return location;
    }

    public byte getDirection() {
        return direction;
    }

    public void update() {
        //workaround for the fact that the gson breaks the link between plots here and in the BuildOff class
        plots = BuildOffManager.getPlugin().getActiveBuildOff().getPlots();
        
        for (int plotNr = 0; plotNr < plots.size(); plotNr++) {
            int xPlot = plotNr % plotsPerRow;
            int yPlot = plotNr / plotsPerRow;
            Location signLocation = LocationHelper.getLocation(location, xPlot, 0, yPlot, direction);

            Block b = signLocation.getWorld().getBlockAt(signLocation);
            b.setType(Material.WALL_SIGN);
            b.setData(directionToData(direction));
            String contestantName = "";
            Contestant contestant = plots.get(plotNr).getContestant();
            if (contestant != null) {
                contestantName = contestant.getName();
            }
            Sign s = (Sign) b.getState();
            s.setLine(0, ChatColor.DARK_BLUE + "<" + ChatColor.BLUE + (plotNr + 1) + ChatColor.DARK_BLUE + ">");
            s.setLine(2, contestantName);
            s.update();
        }
    }

    private byte directionToData(int direction) {
        switch (direction % 4) {
            case 0:
                return 2;
            case 1:
                return 5;
            case 2:
                return 3;
            case 3:
                return 4;
        }
        return 2;
    }

}
