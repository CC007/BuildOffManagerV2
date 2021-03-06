/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

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
        //TODO region for theme sign
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

    public byte getDirection() {
        return direction;
    }

    public void update(boolean secret) {
        signLocation.getBlock().setType(Material.WALL_SIGN);
        signLocation.getBlock().setData(directionToData(direction));
        Sign sign = (Sign) signLocation.getBlock().getState();
        sign.setLine(0, "=-=-=-=-=-=-=-=");
        if (secret) {
            sign.setLine(1, ChatColor.BOLD + "Secret till");
            sign.setLine(2, ChatColor.BOLD + "the start");
        } else {
            sign.setLine(1, ChatColor.BOLD + theme);
            sign.setLine(2, "");
        }
        sign.setLine(3, "=-=-=-=-=-=-=-=");
        sign.update();

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
