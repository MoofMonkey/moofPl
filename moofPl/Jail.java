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
	 * ��������� ������ ������� ����� ������
	 *
	 * @param x2
	 *            - X ���������� ������
	 * @param y2
	 *            - Y ���������� ������
	 * @param z2
	 *            - Z ���������� ������
	 * @param name2
	 *            - ��� ������
	 * @param world2
	 *            - ��� � ������� ����������� ������
	 */
	public Jail(long x2, long y2, long z2, String name2, String world2) {
		x = x2;
		y = y2;
		z = z2;
		name = name2;
		world = world2;
	}

	/**
	 * ��������� ���� ����������� � ���� ������
	 */
	public void clear() {
		jailed.clear();
	}

	/**
	 * ��������� � ������ ������
	 *
	 * @param p
	 *            - �����
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
