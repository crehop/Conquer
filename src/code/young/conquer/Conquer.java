package code.young.conquer;

import code.young.conquer.commands.ConquerPointCommand;
import code.young.conquer.events.ConquerPointEvents;
import code.young.conquer.management.ConquerPointManager;
import code.young.conquer.management.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Calvin on 2/26/2015 for the BreakMC Network.
 * Project: PracticePots
 * Copyright 2015, Sairex Media, All rights reserved.
 */
public class Conquer extends JavaPlugin {

    private static Conquer main;
    private ConquerPointManager cpm;
    private FileManager fm;

    public void onEnable() {
        main = this;

        cpm = new ConquerPointManager();
        fm = new FileManager();

        checkConquerFile();

        fm.loadControlPoints();

        Bukkit.getServer().getPluginManager().registerEvents(new ConquerPointEvents(), this);

        getCommand("Conquer").setExecutor(new ConquerPointCommand());
    }

    public void onDisable() {
        fm.saveControlPoints();
    }

    public void checkConquerFile() {
        if (!new File(getDataFolder(), "conquerpoints.yml").exists()) {
            fm.saveDefaultControlPoints();
        }
    }

    public ConquerPointManager getConquerPointManager() {
        return cpm;
    }

    public FileManager getFileManager() {
        return fm;
    }

    public static Conquer getInstance() {
        return main;
    }
}
