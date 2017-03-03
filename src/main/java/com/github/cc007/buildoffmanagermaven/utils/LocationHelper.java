/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.utils;

import org.bukkit.Location;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class LocationHelper {

    public static Location getLocation(Location startLocation, int xOffset, int yOffset, int hightOffset, byte direction) {
        Location newLocation = startLocation.clone().subtract(startLocation);

        if (direction / 4 > 0) {
            xOffset = -xOffset;
        }

        switch (direction % 4) {
            case 0:
                newLocation.add(xOffset, hightOffset, yOffset);
                break;
            case 1:
                newLocation.add(-yOffset, hightOffset, xOffset);
                break;
            case 2:
                newLocation.add(-xOffset, hightOffset, -yOffset);
                break;
            case 3:
                newLocation.add(yOffset, hightOffset, -xOffset);
                break;
        }
        newLocation.add(startLocation);
        return newLocation;
    }

}
