package code.young.conquer.events;

import code.young.conquer.Conquer;
import code.young.conquer.management.ConquerPointManager;
import code.young.conquer.objects.ConquerPoint;
import code.young.conquer.utils.Cooldowns;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class ConquerPointEvents implements Listener {

    private Conquer main = Conquer.getInstance();
    private ConquerPointManager cpm = main.getConquerPointManager();
    private HashMap<UUID, ConquerPoint> inArea = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
    	if(e.getPlayer().getWorld().toString().contains("world")){
	        Player p = e.getPlayer();
	
	        for (ConquerPoint cps : cpm.getConquerPoints()) {
	            if (cps.getPos1().distance(p.getLocation()) <= 150) {
	                if (!inArea.containsKey(p.getUniqueId())) {
	                    inArea.put(p.getUniqueId(), cps);
	                    if (cps.getNation() != null) {
	                        p.sendMessage("§c[§4§lConquer§c]: §cThis area is currently conquered by §d" + cps.getNation().getName());
	                    } else {
	                        p.sendMessage("§c[§4§lConquer§c]: §cThis area is currently not conquered by a nation.");
	                    }
	                }
	            } else {
	                if (inArea.containsKey(p.getUniqueId())) {
	                    inArea.remove(p.getUniqueId());
	                }
	            }
	        }
	
	        try {
	            Resident res = TownyUniverse.getDataSource().getResident(p.getName());
	
	            if (!res.hasTown()) {
	                return;
	            }
	
	            if (!res.hasNation()) {
	                return;
	            }
	
	            if (!res.getTown().hasNation()) {
	                return;
	            }
	
	            Nation n = res.getTown().getNation();
	            if (Cooldowns.getCooldown(p, "CaptureCooldown") > 0) {
	                return;
	            }
	            if (cpm.getConquerPointFromLocation(p.getLocation()) != null) {
	                ConquerPoint cp = cpm.getConquerPointFromLocation(p.getLocation());
	                if (cp.isOnCooldown()) {
	                    if (Cooldowns.tryCooldown(p, "MessageC", 3000)) {
	                        p.sendMessage("§c[§4§lConquer§c]: §d" + cp.getName() + " §cwas recently conquered, and can be reconquered in " + convertSecondsToMinutes(cp.getCooldownTime()));
	                    }
	                    return;
	                }
	                if (cp.getHolder() == null) {
	                    if (n.equals(cp.getNation())) {
	                        return;
	                    }
	                    if (cp.getNation() == null) {
	                        Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis attempting to conquer §d" + cp.getName() + "§c!");
	                    } else {
	                        Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis attempting to conquer §5" + cp.getNation().getName() + "§c's §d" + cp.getName() + "§c!");
	                        for (Player all : TownyUniverse.getOnlinePlayers(cp.getNation())) {
	                            all.sendMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis trying to conquer your territory in §5" + cp.getName() + "§c!\nDefend your territory!");
	                        }
	                    }
	                    cp.startCapture(p);
	                }
	            } else {
	                for (ConquerPoint cps : cpm.getConquerPoints()) {
	                    if (cps.getHolder() != null) {
	                        if (cps.getHolder().equals(p.getUniqueId())) {
	                            cps.stopCapturing();
	                            Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §chas stopped trying to conquer §d" + cps.getName() + "§c!");
	                        }
	                    }
	                }
	                Cooldowns.setCooldown(p, "CaptureCooldown", 30000);
	            }
	
	            for (ConquerPoint cps : cpm.getConquerPoints()) {
	                if (cps.getPos1().distance(p.getLocation()) <= 150) {
	                    if (cps.getNation().equals(n)) {
	                        p.addPotionEffect(PotionEffectType.REGENERATION.createEffect(10*20, 1), true);
	                    }
	                }
	            }
	        } catch (NotRegisteredException e1) {
	            e1.printStackTrace();
	        }
    	}
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
    	if(e.getEntity().getWorld().toString().contains("world")){
	        Player p = e.getEntity();
	        try {
	            Resident res = TownyUniverse.getDataSource().getResident(p.getName());
	 
	            if (!res.hasTown()) {
	                return;
	            }
	 
	            if (!res.hasNation()) {
	                return;
	            }
	 
	            if (!res.getTown().hasNation()) {
	                return;
	            }
	 
	            Nation n = res.getTown().getNation();
	            if (Cooldowns.getCooldown(p, "CaptureCooldown") > 0) {
	                return;
	            }
	            if (cpm.getConquerPointFromLocation(p.getLocation()) != null) {
	                ConquerPoint cp = cpm.getConquerPointFromLocation(p.getLocation());
	                if (cp.isOnCooldown()) {
	                    if (Cooldowns.tryCooldown(p, "MessageC", 3000)) {
	                        p.sendMessage("§c[§4§lConquer§c]: §d" + cp.getName() + " §cwas recently conquered, and can be reconquered in " + convertSecondsToMinutes(cp.getCooldownTime()));
	                    }
	                    return;
	                }
	                if (cp.getHolder() == null) {
	                    if (n.equals(cp.getNation())) {
	                        return;
	                    }
	                    if (cp.getNation() == null) {
	                        Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis attempting to conquer §d" + cp.getName() + "§c!");
	                    } else {
	                        Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis attempting to conquer §5" + cp.getNation().getName() + "§c's §d" + cp.getName() + "§c!");
	                        for (Player all : TownyUniverse.getOnlinePlayers(cp.getNation())) {
	                            all.sendMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis trying to conquer your territory in §5" + cp.getName() + "§c!\nDefend your territory!");
	                        }
	                    }
	                    cp.startCapture(p);
	                }
	            } else {
	                for (ConquerPoint cps : cpm.getConquerPoints()) {
	                    if (cps.getHolder() != null) {
	                        if (cps.getHolder().equals(p.getUniqueId())) {
	                            cps.stopCapturing();
	                            Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §chas stopped trying to conquer §d" + cps.getName() + "§c!");
	                        }
	                    }
		                Cooldowns.setCooldown(p, "CaptureCooldown", 30000);
	                }
	            }
	        } catch (NotRegisteredException e1) {
	            e1.printStackTrace();
	        }
    	}
    }
   
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
    	if(p.getWorld().toString().contains("world")){
	        try {
	            Resident res = TownyUniverse.getDataSource().getResident(p.getName());
	 
	            if (!res.hasTown()) {
	                return;
	            }
	 
	            if (!res.hasNation()) {
	                return;
	            }
	 
	            if (!res.getTown().hasNation()) {
	                return;
	            }
	 
	            Nation n = res.getTown().getNation();
	            if (Cooldowns.getCooldown(p, "CaptureCooldown") > 0) {
	                return;
	            }

	            if (cpm.getConquerPointFromLocation(p.getLocation()) != null) {
	                ConquerPoint cp = cpm.getConquerPointFromLocation(p.getLocation());
	                if (cp.isOnCooldown()) {
	                    if (Cooldowns.tryCooldown(p, "MessageC", 3000)) {
	                        p.sendMessage("§c[§4§lConquer§c]: §d" + cp.getName() + " §cwas recently conquered, and can be reconquered in " + convertSecondsToMinutes(cp.getCooldownTime()));
	                    }
	                    return;
	                }
	                if (cp.getHolder() == null) {
	                    if (n.equals(cp.getNation())) {
	                        return;
	                    }
	                    if (cp.getNation() == null) {
	                        Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis attempting to conquer §d" + cp.getName() + "§c!");
	                    } else {
	                        Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis attempting to conquer §5" + cp.getNation().getName() + "§c's §d" + cp.getName() + "§c!");
	                        for (Player all : TownyUniverse.getOnlinePlayers(cp.getNation())) {
	                            all.sendMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §cis trying to conquer your territory in §5" + cp.getName() + "§c!\nDefend your territory!");
	                        }
	                    }
	                    cp.startCapture(p);
	                }
	            } else {
	                for (ConquerPoint cps : cpm.getConquerPoints()) {
	                    if (cps.getHolder() != null) {
	                        if (cps.getHolder().equals(p.getUniqueId())) {
	                            cps.stopCapturing();
	                            Bukkit.broadcastMessage("§c[§4§lConquer§c]: §5" + res.getTown().getNation().getName() + " §chas stopped trying to conquer §d" + cps.getName() + "§c!");
	                        }
	                    }
		                Cooldowns.setCooldown(p, "CaptureCooldown", 30000);
	                }
	            }
	        } catch (NotRegisteredException e1) {
	            e1.printStackTrace();
	        }
    	}
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
