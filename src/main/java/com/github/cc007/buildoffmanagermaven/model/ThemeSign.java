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
    private byte direction;

    public ThemeSign(String theme, Location signLocation, byte direction) {
        this.theme = theme;
        this.signLocation = signLocation;
        this.direction = direction;
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

    public void setDirection(byte direction) {
        this.direction = direction;
    }

    public byte getDirection() {
        return direction;
    }

}
