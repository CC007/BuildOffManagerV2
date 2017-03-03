/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven.utils;

import com.github.cc007.buildoffmanagermaven.BuildOffManager;
import com.github.cc007.buildoffmanagermaven.model.BuildOff;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class PersistencyHelper {

    public static boolean loadBuildOff() {
        BuildOff bo = null;
        boolean success = false;
        try {
            File boFile = new File(BuildOffManager.getPlugin().getDataFolder(), "BuildOff.json");
            if (boFile.exists()) {
                String boString = FileUtils.readFileToString(boFile);
                Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).create();
                bo = gson.fromJson(boString, BuildOff.class);
                success = true;
            }
        } catch (IOException ex) {
            Logger.getLogger(PersistencyHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        BuildOffManager.getPlugin().setActiveBuildOff(bo);
        return success;
    }

    public static boolean saveBuildOff() {
        try {
            File boFile = new File(BuildOffManager.getPlugin().getDataFolder(), "BuildOff.json");
            Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).create();
            String boString = gson.toJson(BuildOffManager.getPlugin().getActiveBuildOff());
            FileUtils.writeStringToFile(boFile, boString, Charset.defaultCharset());
            return true;
        } catch (IOException ex) {
            Logger.getLogger(PersistencyHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
