package com.github.cc007.buildoffmanagermaven.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;

public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable {

    protected String worldName;
    protected final int xPos1, yPos1, zPos1;
    protected final int xPos2, yPos2, zPos2;

    public Cuboid(Location loc) {
        this(loc, loc);
    }

    public Cuboid(Cuboid cuboid) {
        this(cuboid.getWorld(false), cuboid.xPos1, cuboid.yPos1, cuboid.zPos1, cuboid.xPos2, cuboid.yPos2, cuboid.zPos2);
    }

    public Cuboid(Location loc1, Location loc2) {
        if (loc1 != null && loc2 != null) {
            if (loc1.getWorld() != null && loc2.getWorld() != null) {
                if (!loc1.getWorld().equals(loc2.getWorld())) {
                    throw new IllegalStateException("The 2 locations of the cuboid must be in the same world!");
                }
            }
            this.worldName = loc1.getWorld() != null ? loc1.getWorld().getName() : "";
            this.xPos1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
            this.yPos1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
            this.zPos1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
            this.xPos2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
            this.yPos2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
            this.zPos2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        } else {
            this.worldName = "";
            this.xPos1 = 0;
            this.yPos1 = 0;
            this.zPos1 = 0;
            this.xPos2 = 0;
            this.yPos2 = 0;
            this.zPos2 = 0;
        }
    }

    public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = world.getName();
        this.xPos1 = Math.min(x1, x2);
        this.xPos2 = Math.max(x1, x2);
        this.yPos1 = Math.min(y1, y2);
        this.yPos2 = Math.max(y1, y2);
        this.zPos1 = Math.min(z1, z2);
        this.zPos2 = Math.max(z1, z2);
    }

    public Cuboid(Map<String, Object> map) {
        this.worldName = (String) map.get("worldName");
        this.xPos1 = (Integer) map.get("x1");
        this.xPos2 = (Integer) map.get("x2");
        this.yPos1 = (Integer) map.get("y1");
        this.yPos2 = (Integer) map.get("y2");
        this.zPos1 = (Integer) map.get("z1");
        this.zPos2 = (Integer) map.get("z2");
    }

    public List<Block> getBlocks() {
        List<Block> blockList = new ArrayList<Block>();
        World world = this.getWorld(true);
        if (world != null) {
            for (int x = this.xPos1; x <= this.xPos2; x++) {
                for (int y = this.yPos1; y <= this.yPos2; y++) {
                    for (int z = this.zPos1; z <= this.zPos2; z++) {
                        blockList.add(world.getBlockAt(x, y, z));
                    }
                }
            }
            return blockList;
        } else {
            return new ArrayList<Block>();
        }
    }

    public List<Player> getPlayers() {
        List<Player> playerList = new ArrayList<>();
        World world = this.getWorld(true);
        if (world != null) {
            List<Player> worldPlayers = world.getPlayers();
            for (Player player : worldPlayers) {
                int playerX = player.getLocation().getBlockX();
                int playerY = player.getLocation().getBlockY();
                int playerZ = player.getLocation().getBlockZ();
                if (this.xPos1 <= playerX && playerX <= this.xPos2) {
                    if (this.yPos1 <= playerY && playerY <= this.yPos2) {
                        if (this.zPos1 <= playerZ && playerZ <= this.zPos2) {
                            playerList.add(player);
                        }
                    }
                }
            }
            return playerList;
        } else {
            return new ArrayList<>();
        }
    }

    public List<ArmorStand> getArmorStands() {
        List<ArmorStand> armorStandList = new ArrayList<>();
        World world = this.getWorld(true);
        if (world != null) {
            Collection<ArmorStand> worldArmorStands = world.getEntitiesByClass(ArmorStand.class);
            for (ArmorStand armorStand : worldArmorStands) {
                int armorX = armorStand.getLocation().getBlockX();
                int armorY = armorStand.getLocation().getBlockY();
                int armorZ = armorStand.getLocation().getBlockZ();
                if (this.xPos1 <= armorX && armorX <= this.xPos2) {
                    if (this.yPos1 <= armorY && armorY <= this.yPos2) {
                        if (this.zPos1 <= armorZ && armorZ <= this.zPos2) {
                            armorStandList.add(armorStand);
                        }
                    }
                }
            }
            return armorStandList;
        } else {
            return new ArrayList<>();
        }
    }
    
    public List<Boat> getBoats() {
        List<Boat> boatList = new ArrayList<>();
        World world = this.getWorld(true);
        if (world != null) {
            Collection<Boat> worldBoats = world.getEntitiesByClass(Boat.class);
            for (Boat boat : worldBoats) {
                int boatX = boat.getLocation().getBlockX();
                int boatY = boat.getLocation().getBlockY();
                int boatZ = boat.getLocation().getBlockZ();
                if (this.xPos1 <= boatX && boatX <= this.xPos2) {
                    if (this.yPos1 <= boatY && boatY <= this.yPos2) {
                        if (this.zPos1 <= boatZ && boatZ <= this.zPos2) {
                            boatList.add(boat);
                        }
                    }
                }
            }
            return boatList;
        } else {
            return new ArrayList<>();
        }
    }
    
    public List<Minecart> getMinecarts() {
        List<Minecart> minecartList = new ArrayList<>();
        World world = this.getWorld(true);
        if (world != null) {
            Collection<Minecart> worldMinecarts = world.getEntitiesByClass(Minecart.class);
            for (Minecart minecart : worldMinecarts) {
                int minecartX = minecart.getLocation().getBlockX();
                int minecartY = minecart.getLocation().getBlockY();
                int minecartZ = minecart.getLocation().getBlockZ();
                if (this.xPos1 <= minecartX && minecartX <= this.xPos2) {
                    if (this.yPos1 <= minecartY && minecartY <= this.yPos2) {
                        if (this.zPos1 <= minecartZ && minecartZ <= this.zPos2) {
                            minecartList.add(minecart);
                        }
                    }
                }
            }
            return minecartList;
        } else {
            return new ArrayList<>();
        }
    }
    public List<Painting> getPaintings() {
        List<Painting> paintingList = new ArrayList<>();
        World world = this.getWorld(true);
        if (world != null) {
            Collection<Painting> worldPaintings = world.getEntitiesByClass(Painting.class);
            for (Painting painting : worldPaintings) {
                int paintingX = painting.getLocation().getBlockX();
                int paintingY = painting.getLocation().getBlockY();
                int paintingZ = painting.getLocation().getBlockZ();
                if (this.xPos1 <= paintingX && paintingX <= this.xPos2) {
                    if (this.yPos1 <= paintingY && paintingY <= this.yPos2) {
                        if (this.zPos1 <= paintingZ && paintingZ <= this.zPos2) {
                            paintingList.add(painting);
                        }
                    }
                }
            }
            return paintingList;
        } else {
            return new ArrayList<>();
        }
    }
    
    public List<ItemFrame> getItemFrames() {
        List<ItemFrame> itemFrameList = new ArrayList<>();
        World world = this.getWorld(true);
        if (world != null) {
            Collection<ItemFrame> worldItemFrames = world.getEntitiesByClass(ItemFrame.class);
            for (ItemFrame itemFrame : worldItemFrames) {
                int itemFrameX = itemFrame.getLocation().getBlockX();
                int itemFrameY = itemFrame.getLocation().getBlockY();
                int itemFrameZ = itemFrame.getLocation().getBlockZ();
                if (this.xPos1 <= itemFrameX && itemFrameX <= this.xPos2) {
                    if (this.yPos1 <= itemFrameY && itemFrameY <= this.yPos2) {
                        if (this.zPos1 <= itemFrameZ && itemFrameZ <= this.zPos2) {
                            itemFrameList.add(itemFrame);
                        }
                    }
                }
            }
            return itemFrameList;
        } else {
            return new ArrayList<>();
        }
    }

    public int getLowerX() {
        return this.xPos1;
    }

    public int getLowerY() {
        return this.yPos1;
    }

    public int getLowerZ() {
        return this.zPos1;
    }

    public int getUpperX() {
        return this.xPos2;
    }

    public int getUpperY() {
        return this.yPos2;
    }

    public int getUpperZ() {
        return this.zPos2;
    }

    public int getVolume() {
        return (this.xPos2 - this.xPos1 + 1) * (this.yPos2 - this.yPos1 + 1) * (this.zPos2 - this.zPos1 + 1);
    }

    public Location getLowerLocation() {
        return new Location(this.getWorld(false), this.xPos1, this.yPos1, this.zPos1);
    }

    public Location getUpperLocation() {
        return new Location(this.getWorld(false), this.xPos2, this.yPos2, this.zPos2);
    }

    public World getWorld(boolean bypassErrors) {
        World world = Bukkit.getWorld(this.worldName);
        if (world == null) {
            if (!bypassErrors) {
                throw new IllegalStateException("World '" + this.worldName + "' is not loaded");
            }
        }
        return world;
    }

    public void setWorld(World world) {
        if (world != null) {
            this.worldName = world.getName();
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("worldName", this.worldName);
        map.put("x1", this.xPos1);
        map.put("y1", this.yPos1);
        map.put("z1", this.zPos1);
        map.put("x2", this.xPos2);
        map.put("y2", this.yPos2);
        map.put("z2", this.zPos2);
        return map;
    }

    @Override
    public Iterator<Block> iterator() {
        return this.getBlocks().iterator();
    }

    @Override
    public Cuboid clone() {
        return new Cuboid(this);
    }

    @Override
    public String toString() {
        return this.worldName + ":" + this.xPos1 + " " + this.yPos1 + " " + this.zPos1 + ":" + this.xPos2 + " " + this.yPos2 + " " + this.zPos2;
    }

}
