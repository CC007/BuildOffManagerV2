/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.model;

import java.util.UUID;
import org.bukkit.entity.Player;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class Contestant {

    private String name;
    private String displayName;
    private final UUID uuid;

    public Contestant(String name, String displayName, UUID uuid) {
        this.name = name;
        this.displayName = displayName;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UUID getUuid() {
        return uuid;
    }

}
