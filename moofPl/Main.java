package moofPl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import moofPl.commands.moofPl;

public class Main extends JavaPlugin {
	public static List<String> blockedCmds, infoCmds, canOp;
	public static String prefix = ChatColor.RED + "" + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.GOLD
			+ "moofPl" + ChatColor.RED + ChatColor.BOLD + "] " + ChatColor.RESET;

	/**
	 * Выполняет любую команду от имени консоли
	 *
	 * @param command
	 *            - команда
	 * @return true, если команда выполнена успешно
	 */
	public static boolean exec(String command) {
		return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	public HashMap<String, String> maps = new HashMap<String, String>();
	public ArrayList<EnderChest> ec = new ArrayList<EnderChest>();

	public Handlers candles = new Handlers(this);

	public ArrayList<Jail> jls = new ArrayList<Jail>();

	public ItemStack[] loadInv(String slName) {
		List<ItemStack> itemstackList = new ArrayList<ItemStack>();
		boolean done = false;
		int i = -1;
		while (!done) {
			i++;
			if (getConfig().contains("moofPl.enderchests." + slName + ".inv." + i))
				itemstackList.add(getConfig().getItemStack("moofPl.enderchests." + slName + ".inv." + i));
			else
				done = true;
		}
		ItemStack[] toReturn = itemstackList.toArray(new ItemStack[itemstackList.size()]);
		return toReturn;
	}

	/**
	 * Создаёт новый конфигурационный файл плагина
	 */
	public void newConfig() {
		try {
			saveDefaultConfig();
			reloadConfig();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		getConfig().addDefault("moofPl.blockedCmds", "");
		getConfig().addDefault("moofPl.infoCmds", "");
		getConfig().addDefault("moofPl.canOp", "");
		getConfig().addDefault("moofPl.jails", "");
		getConfig().addDefault("moofPl.jails.jail.world", "world");
		getConfig().addDefault("moofPl.jails.jail.x", "");
		getConfig().addDefault("moofPl.jails.jail.y", "");
		getConfig().addDefault("moofPl.jails.jail.z", "");
		getConfig().addDefault("moofPl.jails.jail.name", "jail");
		getConfig().addDefault("moofPl.jails.list", "");
		getConfig().addDefault("moofPl.enderchests", "");
		getConfig().addDefault("moofPl.enderchests.list", "");
		getConfig().addDefault("moofPl.enabled", "");
		getConfig().options().copyDefaults(true);
		reloadConfig();
	}

	/**
	 * Используется при выключении сервера/плагина
	 */
	@Override
	public void onDisable() {
		saveecs();

		try {
			candles.onClose();
			Bukkit.getScheduler().cancelTasks(this);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		saveCfg();
	}

	/**
	 * Используется при включении сервера/плагина
	 */
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(candles, this);
		reloadConfiguration();
		getCommand("moofPl").setExecutor(new moofPl(this));
	}

	/**
	 * Перезагружает конфигурацию плагина
	 */
	@SuppressWarnings("deprecation")
	public void reloadConfiguration() {
		if (!new File(getDataFolder() + File.separator + "config.yml").exists())
			newConfig();
		try {
			reloadConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[moofPl] moofPl " + (getConfig().getBoolean("moofPl.enabled", true) ? "enabled" : "disabled"));
		setEnabled(getConfig().getBoolean("moofPl.enabled"));
		blockedCmds = getConfig().getStringList("moofPl.blockedCmds");
		infoCmds = getConfig().getStringList("moofPl.infoCmds");
		canOp = getConfig().getStringList("moofPl.canOp");
		jls.clear();
		ec.clear();
		List<String> jails = getConfig().getStringList("moofPl.jails.list");
		for (String i : jails)
			jls.add(new Jail(getConfig().getLong("moofPl.jails." + i + ".x"),
					getConfig().getLong("moofPl.jails." + i + ".y"), getConfig().getLong("moofPl.jails." + i + ".z"),
					getConfig().getString("moofPl.jails." + i + ".name"),
					getConfig().getString("moofPl.jails." + i + ".world")));
		List<String> ecnames = getConfig().getStringList("moofPl.enderchests.list");
		try {
			for (String i : ecnames) {
				System.out.println("[moofPl] Loading " + i + " from config...");
				ec.add(new EnderChest(getConfig().getLong("moofPl.enderchests." + i + ".x"),
						getConfig().getLong("moofPl.enderchests." + i + ".y"),
						getConfig().getLong("moofPl.enderchests." + i + ".z"),
						getConfig().getString("moofPl.enderchests." + i + ".world"),
						getConfig().getString("moofPl.enderchests." + i + ".name"),
						getConfig().getStringList("moofPl.enderchests." + i + ".canAccess"),
						getConfig().getInt("moofPl.enderchests." + i + ".slots") * 9, i));
				if (getConfig().getBoolean("moofPl.enderchests." + i + ".inFile", false))
					ec.get(ec.size() - 1).inv.addItem(loadInv(i));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		File img = new File(getDataFolder() + "/images");
		MapView map;
		MapRender mr;

		map = Bukkit.getServer().getMap((short) 0);
		if (map == null)
			map = Bukkit.getServer().createMap(Bukkit.getWorlds().get(0));
		maps.put("null", Integer.toString(map.getId()));
		map.setCenterX(Integer.MAX_VALUE);
		map.setCenterZ(Integer.MAX_VALUE);
		try {
			mr = new MapRender(new BufferedImage(128, 128, 2));
			mr.initialize(map);
			map.getRenderers().clear();
			map.addRenderer(mr);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		if (!img.exists())
			img.mkdirs();
		File img2 = img;
		short i = 0;
		for (String s : img.list()) {
			++i;
			img2 = new File(img, s);
			map = Bukkit.getServer().getMap(i);
			if (map == null)
				map = Bukkit.getServer().createMap(Bukkit.getWorlds().get(0));
			maps.put(s.toLowerCase(), Integer.toString(map.getId()));
			map.setCenterX(Integer.MAX_VALUE);
			map.setCenterZ(Integer.MAX_VALUE);
			try {
				mr = new MapRender(ImageIO.read(img2));
				mr.initialize(map);
				map.getRenderers().clear();
				map.addRenderer(mr);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		Bukkit.getLogger().info("Configuration loaded.");
	}

	/**
	 * Альтернатива saveConfig()
	 */
	public void saveCfg() {
		saveConfig();
	}

	public void saveecs() {
		for (EnderChest i : ec) {
			saveInv(i.inv.getContents(), i.slName);
			getConfig().set("moofPl.enderchests." + i.slName + ".inFile", true);
		}
		saveCfg();
	}

	public void saveInv(ItemStack[] itemstackList, String slName) {
		for (int i = 0; i < itemstackList.length; i++)
			getConfig().set("moofPl.enderchests." + slName + ".inv." + i, itemstackList[i]);
		saveCfg();
	}
}
