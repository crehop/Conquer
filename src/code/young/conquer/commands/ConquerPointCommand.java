package code.young.conquer.commands;

import code.young.conquer.Conquer;
import code.young.conquer.management.ConquerPointManager;
import code.young.conquer.management.FileManager;
import code.young.conquer.objects.ConquerPoint;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 2/26/2015 for the BreakMC Network.
 * Project: PracticePots
 * Copyright 2015, Sairex Media, All rights reserved.
 */
public class ConquerPointCommand implements CommandExecutor {

    private double pos1x;
    private double pos1y;
    private double pos1z;
    private double pos2x;
    private double pos2y;
    private double pos2z;
    private String worldName;
    private World selWorld;
    private Location pos1;
    private Location pos2;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        ConquerPointManager cpm = Conquer.getInstance().getConquerPointManager();

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create") && p.isOp()) {
                try {
                    LocalSession session = WorldEdit.getInstance().getSession(p.getName());
                    LocalWorld world = session.getSelectionWorld();
                    Region region = session.getSelection(world);
                    worldName = region.getWorld().getName();
                    selWorld = Bukkit.getWorld(worldName);
                    pos1x = region.getMaximumPoint().getX();
                    pos1y = region.getMaximumPoint().getY();
                    pos1z = region.getMaximumPoint().getZ();
                    pos1 = new Location(selWorld, pos1x, pos1y, pos1z);
                    pos2x = region.getMinimumPoint().getX();
                    pos2y = region.getMinimumPoint().getY();
                    pos2z = region.getMinimumPoint().getZ();
                    pos2 = new Location(selWorld, pos2x, pos2y, pos2z);
                } catch (IncompleteRegionException e) {
                    return false;
                } catch (NullPointerException e) {
                    return false;
                }

                String name = args[1];

                ConquerPoint newCP = new ConquerPoint(name, pos1, pos2);
                cpm.addConquerPoint(newCP);

                FileManager fm = Conquer.getInstance().getFileManager();
                fm.saveControlPointsFile();
                fm.reloadConquerPoints();
                p.sendMessage("Created " + newCP.getName());
            }
            if (args[0].equalsIgnoreCase("remove")) {
                String name = args[1];
                ConquerPoint cprem = null;
                for (ConquerPoint cp : cpm.getConquerPoints()) {
                    if (cp.getName().equalsIgnoreCase(name)) {
                        cprem = cp;
                        cp.stopCapturing();
                        FileManager fm = Conquer.getInstance().getFileManager();
                        FileConfiguration controlpoints = fm.getControlPoints();
                        if (controlpoints.isSet("conquerpoints." + name)) {
                            controlpoints.set("conquerpoints." + name, null);
                            fm.saveControlPointsFile();
                            fm.reloadConquerPoints();
                            p.sendMessage("Removed Conquer Point: &e" + name);
                        }
                    }
                }
                cpm.removeConquerPoint(cprem);
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                for (ConquerPoint cp : cpm.getConquerPoints()) {
                    if (cp.getNation() != null) {
                        if (cp.getHolder() != null) {
                            p.sendMessage("§aName: §e" + cp.getName() + " §aHolder: §e" + Bukkit.getPlayer(cp.getHolder()).getName() + " §aNation: §e" + cp.getNation().getName() + "§aState: §e" + cp.getState() + " §aTime: §e" + cp.getCaptureTime());
                        } else {
                            p.sendMessage("§aName: §e" + cp.getName() + " §aHolder: §enone" + " §aState: §e" + cp.getState() + " §aTime: §e" + cp.getCaptureTime());
                        }
                    } else {
                        if (cp.getHolder() != null) {
                            p.sendMessage("§aName: §e" + cp.getName() + " §aHolder: §e" + Bukkit.getPlayer(cp.getHolder()).getName() + " §aNation: §enone" + "§aState: §e" + cp.getState() + " §aTime: §e" + cp.getCaptureTime());
                        } else {
                            p.sendMessage("§aName: §e" + cp.getName() + " §aHolder: §enone" + " §aState: §e" + cp.getState() + " §aTime: §e" + cp.getCaptureTime());
                        }
                    }
                }
            }
        }
        return false;
    }
}
