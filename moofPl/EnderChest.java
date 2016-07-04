package moofPl;

import java.io.Serializable;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderChest implements Serializable {
	private static final long serialVersionUID = -8561586620219940872L;
	long x;
	long y;
	long z;
	String world;
	String name;
	List<String> access;
	public Inventory inv;
	public String slName;

	/**
	 * Генератор нового объекта этого класса
	 *
	 * @param x
	 *            - X координата
	 * @param y
	 *            - Y координата
	 * @param z
	 *            - Z координата
	 * @param world
	 *            - мир
	 * @param name
	 *            - имя сундука
	 * @param access
	 *            - список игроков имеющих доступ к сундуку
	 */
	public EnderChest(long x2, long y2, long z2, String world2, String name2, List<String> access2, int slots,
			String slName2) {
		x = x2;
		y = y2;
		z = z2;
		world = world2;
		name = name2.replaceAll("_", " ").replaceAll("=", "§");
		access = access2;
		inv = Bukkit.createInventory(null, slots, name);
		slName = slName2;
	}

	public boolean canAccess(Player p) {
		String pN = p.getName();
		if (access.contains(pN.toLowerCase()))
			return true;
		return false;
	}

	public boolean openInv(long x2, long y2, long z2, String world2, Player p) {
		if (x2 != x || y2 != y || z2 != z || !world.toLowerCase().equals(world2.toLowerCase()))
			return false;
		if (!canAccess(p)) /*
							 * { System.out.println(p.getName() +
							 * " пытался получить доступ к админскому эндеру!");
							 */
			return false;
		/* } */ else {
			p.openInventory(inv);
			return true;
		}
	}

	/**
	 * Возращает этот объект в строке, но не сохраняет список canAccess и
	 * метадату предметов.
	 * 
	 * @return Возращает этот объект
	 */
	@SuppressWarnings("deprecation")
	@Override
	public String toString() {
		String s = name.replaceAll(" ", "_").replaceAll("§", "=") + "$" + world + "$" + x + "$" + y + "$" + z + "$"
				+ inv.getSize();
		for (ItemStack i : inv.getContents())
			if (i != null)
				s += "$" + i.getTypeId() + ":" + i.getDurability() + ":" + i.getAmount();
		return s;
	}
}
