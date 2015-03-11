package code.young.conquer.management;

import java.util.ArrayList;
import java.util.List;

import code.young.conquer.objects.ConquerPoint;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ConquerPointManager {

	private ArrayList<ConquerPoint> conquerpoints = new ArrayList<>();

	public List<ConquerPoint> getConquerPoints() {
		return conquerpoints;
	}

	public void addConquerPoint(ConquerPoint cp) {
		getConquerPoints().add(cp);
	}

	public void removeConquerPoint(ConquerPoint targetcp) {
		for (ConquerPoint cp : getConquerPoints()) {
			if (targetcp == cp) {
				getConquerPoints().remove(targetcp);
				return;
			}
		}
	}

	public ConquerPoint getConquerPointByName(String name) {
		for (ConquerPoint cp : getConquerPoints()) {
			if (cp.getName().equalsIgnoreCase(name)) {
				return cp;
			}
		}
		return null;
	}

	public boolean isCapturing(Player p) {
		for (ConquerPoint cp : getConquerPoints()) {
            try {
                if (cp.getHolder().equals(TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation())) {
                    return true;
                }
            } catch (NotRegisteredException e) {
                e.printStackTrace();
                return false;
            }
        }
		return false;
	}

	public ConquerPoint getPlayerConquerPoint(Player p) {
		for (ConquerPoint cp : getConquerPoints()) {
            try {
                if (cp.getHolder().equals(TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation())) {
                    return cp;
                }
            } catch (NotRegisteredException e) {
                e.printStackTrace();
                return null;
            }
		}
		return null;
	}

	public ConquerPoint getConquerPointFromLocation(Location loc) {
		for (ConquerPoint points : getConquerPoints()) {
			if (isInside(loc, points.getPos1(), points.getPos2())) {
				return points;
			}
		}
		return null;
	}

	public boolean isInside(Location loc, Location l1, Location l2) {
		int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
		int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
		int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
		int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
		int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
		int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
		return loc.getX() >= x1 && loc.getX() <= x2 && loc.getY() >= y1 && loc.getY() <= y2 && loc.getZ() >= z1 && loc.getZ() <= z2;
	}
}