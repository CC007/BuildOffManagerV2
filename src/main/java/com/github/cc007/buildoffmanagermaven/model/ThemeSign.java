/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

import org.bukkit.Location;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class ThemeSign {

    private String theme;
    private Location signLocation;

    public ThemeSign(String theme, Location signLocation) {
        this.theme = theme;
        this.signLocation = signLocation;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    public void setSignLocation(Location signLocation) {
        this.signLocation = signLocation;
    }

    public Location getSignLocation() {
        return signLocation;
    }

}
