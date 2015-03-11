package code.young.conquer.management;

import code.young.conquer.Conquer;
import code.young.conquer.objects.ConquerPoint;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

public class FileManager {

    private Conquer main = Conquer.getInstance();

    private FileConfiguration conquerpoints = null;
    private File conquerpointsFile = null;

    public void reloadConquerPoints() {
        if (conquerpointsFile == null) {
            conquerpointsFile = new File(main.getDataFolder(), "conquerpoints.yml");
        }

        conquerpoints = YamlConfiguration.loadConfiguration(conquerpointsFile);

        InputStream defConfigStream = main.getResource("conquerpoints.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            conquerpoints.setDefaults(defConfig);
        }
    }

    public void saveControlPointsFile() {
        try {
            conquerpoints.save(conquerpointsFile);
            // Saved arenas
        } catch (Exception e) {
            // error while saving
        }
    }

    public FileConfiguration getControlPoints() {
        if (conquerpoints == null) {
            reloadConquerPoints();
        }
        return conquerpoints;
    }

    public void saveDefaultControlPoints() {
        if (conquerpointsFile == null) {
            conquerpointsFile = new File(main.getDataFolder(), "conquerpoints.yml");
        }
        if (!conquerpointsFile.exists()) {
            main.saveResource("conquerpoints.yml", false);
        }
    }

    public void saveControlPoints() {
        if (conquerpoints == null) {
            reloadConquerPoints();
        }
        ConquerPointManager cpm = main.getConquerPointManager();
        if (cpm.getConquerPoints().size() == 0) {
            return;
        }
        for (ConquerPoint cp : cpm.getConquerPoints()) {
            String path = "conquerpoints." + cp.getName() + ".";
            conquerpoints.set(path + "pos1.world", cp.getPos1().getWorld().getName());
            conquerpoints.set(path + "pos1.x", cp.getPos1().getBlockX());
            conquerpoints.set(path + "pos1.y", cp.getPos1().getBlockY());
            conquerpoints.set(path + "pos1.z", cp.getPos1().getBlockZ());

            conquerpoints.set(path + "pos2.world", cp.getPos2().getWorld().getName());
            conquerpoints.set(path + "pos2.x", cp.getPos2().getBlockX());
            conquerpoints.set(path + "pos2.y", cp.getPos2().getBlockY());
            conquerpoints.set(path + "pos2.z", cp.getPos2().getBlockZ());
        }
        try {
            conquerpoints.save(conquerpointsFile);
        } catch (Exception e) {
        }
    }

    public void loadControlPoints() {
        if (conquerpoints == null) {
            reloadConquerPoints();
        }
        ConquerPointManager cpm = main.getConquerPointManager();

        ConfigurationSection sec = conquerpoints.getConfigurationSection("conquerpoints");
        if (sec == null) {
            return;
        }
        Set<String> cpSet = sec.getKeys(false);
        if (cpSet != null) {
            for (String aCP : cpSet) {
                String path = "conquerpoints." + aCP + ".";

                String cpName = aCP;

                String pos1w = conquerpoints.getString(path + "pos1.world");
                int pos1x = conquerpoints.getInt(path + "pos1.x");
                int pos1y = conquerpoints.getInt(path + "pos1.y");
                int pos1z = conquerpoints.getInt(path + "pos1.z");

                String pos2w = conquerpoints.getString(path + "pos2.world");
                int pos2x = conquerpoints.getInt(path + "pos2.x");
                int pos2y = conquerpoints.getInt(path + "pos2.y");
                int pos2z = conquerpoints.getInt(path + "pos2.z");

                Location pos1 = new Location(Bukkit.getWorld(pos1w), pos1x, pos1y, pos1z);
                Location pos2 = new Location(Bukkit.getWorld(pos2w), pos2x, pos2y, pos2z);

                ConquerPoint cp = new ConquerPoint(cpName, pos1, pos2);

                cpm.addConquerPoint(cp);
            }
        }
    }
}