/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

import com.github.cc007.buildoffmanagermaven.utils.Cuboid;
import com.github.cc007.buildoffmanagermaven.utils.LocationHelper;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class Plot {

    private BuildOff bo;
    private Contestant contestant;
    private int plotNr;

    public Plot(BuildOff bo, int plotNr) {
        this.bo = bo;
        this.plotNr = plotNr;
        this.contestant = null;
    }

    public Plot(BuildOff bo, int plotNr, Contestant contestant) {
        this.bo = bo;
        this.contestant = contestant;
        this.plotNr = plotNr;
    }

    public void setContestant(Contestant contestant) {
        this.contestant = contestant;
    }

    public Contestant getContestant() {
        return contestant;
    }

    public void reset(Location plotLocation, byte direction, int plotSize) {
        bo.addResetContestant(contestant);
        this.contestant = null;
        setBlocks(plotLocation, direction, plotSize);
        removeArmorStands(plotLocation, direction, plotSize);
    }

    private void clear() {

    }

    public void init(Location plotLocation, byte direction, int plotSize) {
        setBlocks(plotLocation, direction, plotSize);
        setRegion(plotLocation, direction, plotSize);
    }

    private void setBlocks(Location plotLocation, byte direction, int plotSize) {
        Location signLoc = LocationHelper.getLocation(plotLocation, 0, 0, 1, direction);

        Location slabLoc1 = plotLocation;
        Location slabLoc2 = LocationHelper.getLocation(plotLocation, plotSize + 1, plotSize + 1, 0, direction);

        Location airLoc1 = LocationHelper.getLocation(plotLocation, 1, 1, 0, direction);
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
    }

    private void setRegion(Location plotLocation, byte direction, int plotSize) {
        Location buildAreaLoc1 = LocationHelper.getLocation(plotLocation, 1, 1, 0, direction);
        BlockVector buildArea1 = new BlockVector(buildAreaLoc1.getBlockX(), 1, buildAreaLoc1.getBlockZ());
        Location buildAreaLoc2 = LocationHelper.getLocation(plotLocation, plotSize, plotSize, 0, direction);
        BlockVector buildArea2 = new BlockVector(buildAreaLoc2.getBlockX(), 255, buildAreaLoc2.getBlockZ());
        ProtectedCuboidRegion buildAreaPcr = new ProtectedCuboidRegion("plotbig_" + plotNr, buildArea1, buildArea2);
        buildAreaPcr.setPriority(1);

        Location edgeAreaLoc1 = LocationHelper.getLocation(plotLocation, 0, 0, 0, direction);
        BlockVector edgeArea1 = new BlockVector(edgeAreaLoc1.getBlockX(), edgeAreaLoc1.getBlockY(), edgeAreaLoc1.getBlockZ());
        Location edgeAreaLoc2 = LocationHelper.getLocation(plotLocation, plotSize + 1, plotSize + 1, -1, direction);
        BlockVector edgeArea2 = new BlockVector(edgeAreaLoc2.getBlockX(), edgeAreaLoc1.getBlockY(), edgeAreaLoc2.getBlockZ());
        ProtectedCuboidRegion edgeAreaPcr = new ProtectedCuboidRegion("plotedge_" + plotNr, buildArea1, buildArea2);
        edgeAreaPcr.setPriority(1);

        ProtectedCuboidRegion glowstoneAreaPcr = new ProtectedCuboidRegion("plotblock_" + plotNr, edgeArea1, edgeArea1);
        glowstoneAreaPcr.setPriority(2);

        RegionManager rgm = WGBukkit.getRegionManager(plotLocation.getWorld());
        rgm.addRegion(buildAreaPcr);
        rgm.addRegion(edgeAreaPcr);
        rgm.addRegion(glowstoneAreaPcr);
    }

    private void removeArmorStands(Location plotLocation, byte direction, int plotSize) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private byte getSignDirection(byte direction) {
        switch (direction) {
            case 0:
                return (byte) 6;
            case 1:
                return (byte) 2;
            case 2:
                return (byte) 10;
            case 3:
                return (byte) 14;
            case 4:
                return (byte) 14;
            case 5:
                return (byte) 10;
            case 6:
                return (byte) 2;
            case 7:
                return (byte) 6;
        }
        return 0;
    }

}
