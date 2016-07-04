package moofPl;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Jail {
	long x;
	long y;
	long z;
	public String name;
	String world;
	ArrayList<Player> jailed = new ArrayList<Player>();

	/**
	 * Генератор нового объекта этого класса
	 *
	 * @param x2
	 *            - X координата тюрьмы
	 * @param y2
	 *            - Y координата тюрьмы
	 * @param z2
	 *            - Z координата тюрьмы
	 * @param name2
	 *            - имя тюрьмы
	 * @param world2
	 *            - мир в котором расположена тюрьма
	 */
	public Jail(long x2, long y2, long z2, String name2, String world2) {
		x = x2;
		y = y2;
		z = z2;
		name = name2;
		world = world2;
	}

	/**
	 * Выпускает всех заключённых в этой тюрьме
	 */
	public void clear() {
		jailed.clear();
	}

	/**
	 * Заключает в тюрьму игрока
	 *
	 * @param p
	 *            - игрок
	 */
	public boolean jail(Player p) {
		if (!jailed.contains(p)) {
			jailed.add(p);
			p.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "You're jailed!");
			p.teleport(new Location(Bukkit.getWorld(world), x, y, z));
			p.setGameMode(GameMode.ADVENTURE);
			p.getInventory().clear();
			return true;
		}
		return false;
	}
}
