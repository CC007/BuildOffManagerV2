/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

import com.github.cc007.buildoffmanagermaven.BuildOffManager;
import com.github.cc007.buildoffmanagermaven.utils.Cuboid;
import com.github.cc007.buildoffmanagermaven.utils.LocationHelper;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Painting;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class Plot {

    private Contestant contestant;
    private final int plotNr;
    private final Location plotLocation;
    private final byte direction;
    private final int plotSize;

    public Plot(int plotNr, Location plotLocation, byte direction, int plotSize) {
        this.plotNr = plotNr;
        this.plotLocation = plotLocation;
        this.direction = direction;
        this.plotSize = plotSize;
        this.contestant = null;
    }

    public Plot(int plotNr, Location plotLocation, byte direction, int plotSize, Contestant contestant) {
        this.plotNr = plotNr;
        this.plotLocation = plotLocation;
        this.direction = direction;
        this.plotSize = plotSize;
        this.contestant = contestant;
    }

    public void setContestant(Contestant contestant) {
        this.contestant = contestant;
        RegionManager rgm = WGBukkit.getRegionManager(BuildOffManager.getPlugin().getActiveBuildOff().getLocation().getWorld());
        DefaultDomain dd = new DefaultDomain();
        dd.addPlayer(contestant.getUuid());
        try {
            rgm.getRegion("plotbig_" + plotNr).setMembers(dd);
            rgm.getRegion("plotedge_" + plotNr).setMembers(dd);
        } catch (NullPointerException ex) {
            BuildOffManager.getPlugin().getLogger().warning("The regions aren't made yet when trying to join a buildoff.");
        }
        try {
            rgm.save();
        } catch (StorageException ex) {
            Logger.getLogger(Plot.class.getName()).log(Level.SEVERE, null, ex);
        }
        Block b = plotLocation.getWorld().getBlockAt(LocationHelper.getLocation(plotLocation, 0, 0, 1, direction));
        b.setType(Material.SIGN_POST);
        b.setData(getSignDirection(direction));
        Sign signState = (Sign) b.getState();
        signState.setLine(0, ChatColor.DARK_BLUE + "<" + ChatColor.BLUE + (plotNr + 1) + ChatColor.DARK_BLUE + ">");
        signState.setLine(2, contestant.getName());
        signState.update();
    }

    public Contestant getContestant() {
        return contestant;
    }

    public void reset() {
        if (contestant == null) {
            return;
        }
        BuildOff bo = BuildOffManager.getPlugin().getActiveBuildOff();
        bo.addResetContestant(contestant);
        this.contestant = null;
        setBlocks();
        removeEntities();
        RegionManager rgm = WGBukkit.getRegionManager(bo.getLocation().getWorld());
        DefaultDomain dd = new DefaultDomain();
        try {
            rgm.getRegion("plotbig_" + plotNr).setMembers(dd);
            rgm.getRegion("plotedge_" + plotNr).setMembers(dd);
        } catch (NullPointerException ex) {
            BuildOffManager.getPlugin().getLogger().warning("The regions aren't made yet when trying to join a buildoff.");
        }
        try {
            rgm.save();
        } catch (StorageException ex) {
            Logger.getLogger(Plot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void init() {
        setBlocks();
        setRegion();
    }

    private void setBlocks() {
        Location signLoc = LocationHelper.getLocation(plotLocation, 0, 0, 1, direction);

        Location slabLoc1 = plotLocation.clone();
        Location slabLoc2 = LocationHelper.getLocation(plotLocation, plotSize + 1, plotSize + 1, 0, direction);

        Location airLoc1 = LocationHelper.getLocation(plotLocation, 1, 1, 0, direction);
        airLoc1.setY(255);
        Location airLoc2 = LocationHelper.getLocation(plotLocation, plotSize, plotSize, 0, direction);

        Location grassLoc1 = LocationHelper.getLocation(plotLocation, 0, 0, -1, direction);
        Location grassLoc2 = LocationHelper.getLocation(plotLocation, plotSize + 1, plotSize + 1, -1, direction);

        Location dirtLoc1 = LocationHelper.getLocation(plotLocation, 0, 0, -2, direction);
        Location dirtLoc2 = LocationHelper.getLocation(plotLocation, plotSize + 1, plotSize + 1, -4, direction);

        Location stoneLoc1 = LocationHelper.getLocation(plotLocation, 0, 0, -5, direction);
        Location stoneLoc2 = LocationHelper.getLocation(plotLocation, plotSize + 1, plotSize + 1, -4, direction);
        stoneLoc2.setY(1);

        Location brLoc1 = LocationHelper.getLocation(plotLocation, 0, 0, 0, direction);
        brLoc1.setY(0);
        Location brLoc2 = LocationHelper.getLocation(plotLocation, plotSize + 1, plotSize + 1, -4, direction);
        brLoc2.setY(0);

        for (Block block : new Cuboid(slabLoc1, slabLoc2).getBlocks()) {
            block.setType(Material.STEP);
        }
        for (Block block : new Cuboid(airLoc1, airLoc2).getBlocks()) {
            block.setType(Material.AIR);
        }
        for (Block block : new Cuboid(grassLoc1, grassLoc2).getBlocks()) {
            block.setType(Material.GRASS);
        }
        for (Block block : new Cuboid(dirtLoc1, dirtLoc2).getBlocks()) {
            block.setType(Material.DIRT);
        }
        for (Block block : new Cuboid(stoneLoc1, stoneLoc2).getBlocks()) {
            block.setType(Material.STONE);
        }
        for (Block block : new Cuboid(brLoc1, brLoc2).getBlocks()) {
            block.setType(Material.BEDROCK);
        }
        plotLocation.getWorld().getBlockAt(plotLocation).setType(Material.GLOWSTONE);
        signLoc.getWorld().getBlockAt(signLoc).setType(Material.SIGN_POST);
        signLoc.getWorld().getBlockAt(signLoc).setData(getSignDirection(direction));
        Sign signState = (Sign) signLoc.getWorld().getBlockAt(signLoc).getState();
        signState.setLine(0, ChatColor.DARK_BLUE + "<" + ChatColor.BLUE + (plotNr + 1) + ChatColor.DARK_BLUE + ">");
        signState.setLine(2, "");
        signState.update();
    }

    private void setRegion() {
        Location buildAreaLoc1 = LocationHelper.getLocation(plotLocation, 1, 1, 0, direction);
        BlockVector buildArea1 = new BlockVector(buildAreaLoc1.getBlockX(), 1, buildAreaLoc1.getBlockZ());
        Location buildAreaLoc2 = LocationHelper.getLocation(plotLocation, plotSize, plotSize, 0, direction);
        BlockVector buildArea2 = new BlockVector(buildAreaLoc2.getBlockX(), 255, buildAreaLoc2.getBlockZ());
        ProtectedCuboidRegion buildAreaPcr = new ProtectedCuboidRegion("plotbig_" + plotNr, buildArea1, buildArea2);
        buildAreaPcr.setPriority(2);

        Location edgeAreaLoc1 = plotLocation.clone();
        BlockVector edgeArea1 = new BlockVector(edgeAreaLoc1.getBlockX(), edgeAreaLoc1.getBlockY(), edgeAreaLoc1.getBlockZ());
        Location edgeAreaLoc2 = LocationHelper.getLocation(plotLocation, plotSize + 1, plotSize + 1, -1, direction);
        BlockVector edgeArea2 = new BlockVector(edgeAreaLoc2.getBlockX(), edgeAreaLoc2.getBlockY(), edgeAreaLoc2.getBlockZ());
        ProtectedCuboidRegion edgeAreaPcr = new ProtectedCuboidRegion("plotedge_" + plotNr, edgeArea1, edgeArea2);
        edgeAreaPcr.setPriority(1);

        ProtectedCuboidRegion glowstoneAreaPcr = new ProtectedCuboidRegion("plotblock_" + plotNr, edgeArea1, edgeArea1);
        glowstoneAreaPcr.setPriority(2);

        RegionManager rgm = WGBukkit.getRegionManager(plotLocation.getWorld());
        rgm.addRegion(buildAreaPcr);
        rgm.addRegion(edgeAreaPcr);
        rgm.addRegion(glowstoneAreaPcr);
    }

    private void removeEntities() {
        Location l1 = plotLocation.clone();
        l1.setY(0);
        Location l2 = LocationHelper.getLocation(plotLocation, plotSize + 1, plotSize + 1, 0, direction);
        l2.setY(255);
        Cuboid c = new Cuboid(l1, l2);
        for (ArmorStand armorStand : c.getArmorStands()) {
            armorStand.remove();
        }
        for (Boat boat : c.getBoats()) {
            boat.remove();
        }
        for (Minecart minecart : c.getMinecarts()) {
            minecart.remove();
        }
        for (Painting painting : c.getPaintings()) {
            painting.remove();
        }
        for (ItemFrame itemFrame : c.getItemFrames()) {

        }
    }

    private byte getSignDirection(byte direction) {
        switch (direction) {
            case 0:
                return 6;
            case 1:
                return 10;
            case 2:
                return 14;
            case 3:
                return 2;
            case 4:
                return 10;
            case 5:
                return 14;
            case 6:
                return 2;
            case 7:
                return 6;
        }
        return 0;
    }

    public Location getPlotLocation() {
        return plotLocation;
    }

    public byte getDirection() {
        return direction;
    }

    public int getPlotNr() {
        return plotNr;
    }

    public int getPlotSize() {
        return plotSize;
    }

}
