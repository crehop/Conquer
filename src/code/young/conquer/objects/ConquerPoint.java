package code.young.conquer.objects;

import code.young.conquer.Conquer;
import code.young.conquer.management.FileManager;
import code.young.conquer.utils.Cooldowns;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;
import java.util.UUID;

public class ConquerPoint {

    private Conquer main = Conquer.getInstance();
    private FileManager fm = main.getFileManager();

    private String name;
    private Location pos1;
    private Location pos2;
    private ConquerPointState state;
    private UUID holder;
    private Nation nation;
    private int captureTID;
    private int captureTime;
    private int cooldownTID;
    private int cooldownTime;

    public ConquerPoint(String name, Location pos1, Location pos2) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.state = ConquerPointState.OPEN;
        this.holder = null;
        this.captureTID = new Random().nextInt(1000000);
        this.captureTime = 601;
        this.cooldownTID = captureTID + 1;
        this.cooldownTime = 0;
    }

    public ConquerPoint(String name, Location pos1, Location pos2, Nation nation) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.state = ConquerPointState.OPEN;
        this.holder = null;
        this.nation = nation;
        this.captureTID = new Random().nextInt(1000000);
        this.captureTime = 601;
        this.cooldownTID = captureTID + 1;
        this.cooldownTime = 0;
    }

    public String getName() {
        return name;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public ConquerPointState getState() {
        return state;
    }

    public UUID getHolder() {
        return holder;
    }

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public int getCaptureTID() {
        return captureTID;
    }

    public int getCaptureTime() {
        return captureTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public void setState(ConquerPointState state) {
        this.state = state;
    }

    public void setCaptureTime(int time) {
        captureTime = time;
    }

    public int getCooldownTime() {
        return cooldownTime;
    }

    public boolean isOnCooldown() {
        return cooldownTime > 0;
    }

    //&c[&4&lConquer&c]: &5Vollandore &chas begun capturing &dHaven&c! [&415:00&c]

    public void startCapture(final Player p) {
        try {
            final Resident res = TownyUniverse.getDataSource().getResident(p.getName());
            final Nation n = res.getTown().getNation();
            Bukkit.getScheduler().cancelTask(this.hashCode());
            setState(ConquerPointState.CAPTURING);
            holder = p.getUniqueId();
            captureTID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
                public void run() {
                    if (captureTime > 0) {
                        captureTime--;
                    }

                    if (captureTime <= 601 && captureTime > 16) {
                        if (captureTime % 30 == 0) {
                            try {
                                Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis conquering §d" + getName() + "§c! [§4" + convertSecondsToMinutes(captureTime) + "§c] + §e /rules control");
                            } catch (NotRegisteredException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (captureTime <= 10 && captureTime >= 1) {
                        try {
                            Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis conquering §d" + getName() + "§c! [§4" + convertSecondsToMinutes(captureTime) + "§c]");
                        } catch (NotRegisteredException e) {
                            e.printStackTrace();
                        }
                    }

                    if (captureTime == 0) {
                        Bukkit.getScheduler().cancelTask(captureTID);
                        setState(ConquerPointState.CAPTURED);
                        try {
                            if (getNation() == null) {
                                setNation(n);
                                Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §chas conquered §d" + getName() + "§c! Congratulations!");
                            } else {
                                Nation n = getNation();
                                setNation(res.getTown().getNation());
                                Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §chas conquered §5" + n.getName() + "§c's §d" + getName() + "§c! Congratulations!");
                            }
                        } catch (NotRegisteredException e) {
                            e.printStackTrace();
                        }
                        for (Player all : TownyUniverse.getOnlinePlayers(n)) {
                            all.sendMessage("§c[§4§lConquer§c]: §cYour nation has conquered §d" + getName() + "§c!\nDefend it at all costs!");
                            all.getInventory().addItem(new ItemStack(Material.DRAGON_EGG, 3));
                        }

                        Firework fw = p.getWorld().spawn(p.getEyeLocation(), Firework.class);
                        FireworkMeta meta = fw.getFireworkMeta();
                        FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(Color.RED).withFade(Color.BLACK).with(FireworkEffect.Type.BALL_LARGE).trail(true).build();
                        meta.setPower(1);
                        meta.addEffect(effect);
                        fw.setFireworkMeta(meta);

                        Bukkit.getScheduler().cancelTask(captureTID);
                        holder = null;
                        captureTime = 601;
                        cooldownTime = 1801;
                        startCooldown();
                    }
                }
            }, 0, 20);
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public void startCooldown() {
        Bukkit.getScheduler().cancelTask(cooldownTID);
        cooldownTID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
            public void run() {
                if (cooldownTime > 0) {
                    cooldownTime--;
                }
            }
        }, 0, 20);
    }

    public void stopCapturing() {
        setState(ConquerPointState.OPEN);
        Bukkit.getScheduler().cancelTask(captureTID);
        holder = null;
        captureTime = 601;
    }

    public static String convertSecondsToMinutes(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        String disMinu = "" + minutes;
        String disSec = (seconds < 10 ? "0" : "") + seconds;
        String formattedTime = disMinu + ":" + disSec;
        return formattedTime;
    }
}
