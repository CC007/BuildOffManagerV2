/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.buildoffmanagermaven;

import com.github.cc007.buildoffmanagermaven.model.BuildOff;
import com.github.cc007.buildoffmanagermaven.utils.PersistencyHelper;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 * @author Flexo013
 */
public class BuildOffManager extends JavaPlugin {

    private Logger log;
    private Plugin vault = null;
    private Permission permission = null;
    private FileConfiguration config = null;
    private File configFile = null;
    private BuildOff activeBuildOff;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        /* Setup the logger */
        log = getLogger();

        /* Setup permissions */
        vault = getPlugin("Vault");
        if (vault != null) {
            setupPermissions();
        }

        /* Setup command executor */
        getCommand("bo").setExecutor(new BuildOffManagerCommands());

        /* Setup listeners */
        getServer().getPluginManager().registerEvents(new OverviewBoardClickEvent(), this);
        
        
        /* Load build off */
        PersistencyHelper.loadBuildOff();
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Gets a plugin
     *
     * @param pluginName Name of the plugin to get
     * @return The plugin from name
     */
    protected Plugin getPlugin(String pluginName) {
        if (getServer().getPluginManager().getPlugin(pluginName) != null && getServer().getPluginManager().getPlugin(pluginName).isEnabled()) {
            return getServer().getPluginManager().getPlugin(pluginName);
        } else {
            log.log(Level.WARNING, "Could not find plugin \"{0}\"!", pluginName);
            return null;
        }
    }

    /**
     * Setup permissions
     *
     * @return True: Setup correctly, Didn't setup correctly
     */
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);

        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        if (permission == null) {
            getLogger().log(Level.WARNING, "Could not hook Vault!");
        } else {
            getLogger().log(Level.WARNING, "Hooked Vault!");
        }

        return (permission != null);
    }

    /**
     * Get the vault
     *
     * @return the vault
     */
    public Plugin getVault() {
        return vault;
    }

    /**
     * Get the permissions
     *
     * @return the permissions
     */
    public Permission getPermission() {
        return permission;
    }

    /**
     * get the minecraft chat prefix for this plugin
     *
     * @return the minecraft chat prefix for this plugin
     */
    public static String pluginChatPrefix(boolean colored) {
        if (colored) {
            return ChatColor.DARK_BLUE + "[" + ChatColor.BLUE + "BuildOff" + ChatColor.DARK_BLUE + "]" + ChatColor.RESET + " ";
        } else {
            return "[BuildOffManager] ";
        }
    }

    public static BuildOffManager getPlugin() {
        Plugin buildOffManager = Bukkit.getServer().getPluginManager().getPlugin("BuildOffManager");
        if (buildOffManager != null && buildOffManager.isEnabled() && buildOffManager instanceof BuildOffManager) {
            return (BuildOffManager) buildOffManager;
        } else {
            Bukkit.getLogger().log(Level.WARNING, "The Build-Off Manager plugin has not been enabled yet");
            return null;
        }
    }

    /**
     * Method to reload the config.yml config file
     */
    @Override
    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(this.getResource("config.yml"), "UTF8");
        } catch (UnsupportedEncodingException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
    }

    /**
     * Method to get YML content of the config.yml config file
     *
     * @return YML content of the categories.yml config file
     */
    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    /**
     * Method to save the config.yml config file
     */
    @Override
    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public void setActiveBuildOff(BuildOff activeBuildOff) {
        this.activeBuildOff = activeBuildOff;
    }

    public BuildOff getActiveBuildOff() {
        return activeBuildOff;
    }

    public void legacyClear() {
        RegionManager rgm = WGBukkit.getRegionManager(Bukkit.getWorld(getConfig().getString("legacyWorld")));
        for (String regionName : rgm.getRegions().keySet()) {
            if (regionName.startsWith("plot")) {
                rgm.removeRegion(regionName);
            }
        }
        try {
            rgm.save();
        } catch (StorageException ex) {
            Logger.getLogger(BuildOff.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
