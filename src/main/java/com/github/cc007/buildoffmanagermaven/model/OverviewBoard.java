/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

import java.util.List;
import java.util.Map;
import org.bukkit.Location;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class OverviewBoard {

    private final Map<Integer, Plot> plots;
    private Location location;
    private byte direction;

    public OverviewBoard(Map<Integer, Plot> plots, Location location, byte direction) {
        this.plots = plots;
        this.location = location;
        this.direction = direction;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void update() {
        //TODO
    }

}
