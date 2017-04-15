/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven;

import com.github.cc007.buildoffmanagermaven.model.BuildOff;
import com.github.cc007.buildoffmanagermaven.model.Plot;
import com.github.cc007.buildoffmanagermaven.utils.Cuboid;
import com.github.cc007.buildoffmanagermaven.utils.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class OverviewBoardClickEvent implements Listener {

    @EventHandler
    public void onOverviewBoardSignClick(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock().getType() != Material.WALL_SIGN) {
            return;
        }

        Player p = event.getPlayer();
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        if (bo == null || !bo.getLocation().getWorld().equals(p.getWorld())) {
            return;
        }

        Location l1, l2;
        l1 = bo.getOverviewBoard().getLocation();
        l2 = LocationHelper.getLocation(l1, bo.getPlotsPerRow() - 1, 0, ((bo.getPlots().size() - 1) / bo.getPlotsPerRow()), bo.getOverviewBoard().getDirection());
        Cuboid signs = new Cuboid(l1, l2);
        if (!signs.getBlocks().contains(event.getClickedBlock())) {
            return;
        }

        Sign sign = (Sign) event.getClickedBlock().getState();
        String line = sign.getLine(0);
        if (line.length() < 9 || !line.substring(5, line.length() - 3).matches("[0-9]+")) {
            BuildOffManagerCommands.message(p, "Choose a plot number between 1 and " + bo.getPlots().size() + ".", ChatColor.RED);
            return;
        }
        int plotNumber = Integer.parseInt(line.substring(5, line.length() - 3));

        Plot tpPlot;
        tpPlot = bo.getPlot(plotNumber - 1);
        if (tpPlot == null) {
            BuildOffManagerCommands.message(p, "Choose a plot number between 1 and " + bo.getPlots().size() + ".", ChatColor.RED);
            return;
        }
        Location tpLocation = LocationHelper.getLocation(tpPlot.getPlotLocation(), -3, -3, 0, tpPlot.getDirection());
        tpLocation.add(0.5, 0, 0.5);
        tpLocation.setYaw(45.0f);
        tpLocation.setPitch(0.0f);
        p.teleport(tpLocation);
        BuildOffManagerCommands.message(p, "You have been teleported to plot " + plotNumber + ".", ChatColor.GREEN);
    }
}
