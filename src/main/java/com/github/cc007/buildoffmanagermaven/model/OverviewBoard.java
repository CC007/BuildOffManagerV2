/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

import java.util.List;
import org.bukkit.Location;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class OverviewBoard {

    private final List<Plot> plots;
    private Location location;

    public OverviewBoard(List<Plot> plots, Location location) {
        this.plots = plots;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
