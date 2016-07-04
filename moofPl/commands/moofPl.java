package moofPl.commands;

import java.io.File;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import moofPl.Jail;
import moofPl.Main;
import moofPl.PlayerLogger;
import moofPl.Utils;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class moofPl implements CommandExecutor {
	Main main;

	/**
	 * Генератор нового объекта этого класса
	 *
	 * @param main
	 *            - главный метод. Праметр служит для получения Handlers,
	 *            перезагрузки конфигурации и логгера
	 */
	public moofPl(Main main) {
		this.main = main;
	}

	/**
	 * Выполняет команду плагина
	 *
	 * @return true, Всегда
	 * @param sender
	 *            - Отправитель команды
	 * @param command
	 *            - Команда оторая была выполнена
	 * @param label
	 *            - Другие названия команды которые могут использоваться
	 * @param args
	 *            - Аргументы команды
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender,
	 *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player p = sender instanceof Player ? (Player) sender : null;

		if (p != null) {
			if (!PermissionsEx.getUser(p).has("moofPl.commands")) {
				sender.sendMessage(
						Main.prefix + ChatColor.RED + ChatColor.BOLD + "You don't have permission to do that! [1]");
				return true;
			}
			if (!Utils.checkOp(p)) {
				sender.sendMessage(
						Main.prefix + ChatColor.RED + ChatColor.BOLD + "You don't have permission to do that! [2]");
				return true;
			}
		}

		if (args.length < 1 || !args[0].equalsIgnoreCase("reload") && !args[0].equalsIgnoreCase("give")
				&& !args[0].equalsIgnoreCase("jail") && !args[0].equalsIgnoreCase("unjail")
				&& !args[0].equalsIgnoreCase("unjailall") && !args[0].equalsIgnoreCase("map")
				&& !args[0].equalsIgnoreCase("maplist") && !args[0].equalsIgnoreCase("gc")
				&& !args[0].equalsIgnoreCase("sudo") && !args[0].equalsIgnoreCase("antilagg")
				&& !args[0].equalsIgnoreCase("sudoall") && !args[0].equalsIgnoreCase("targetloc")
				&& !args[0].equalsIgnoreCase("saveecs")) {
			sender.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
					+ ChatColor.AQUA
					+ "/moofPl <reload|give|jail|unjail|unjailall|map|maplist|sudo|sudoall|antilagg|targetloc|saveecs>");
			return true;
		}

		if (args[0].equalsIgnoreCase("reload")) {
			main.reloadConfiguration();
			sender.sendMessage(Main.prefix + "Config reloaded!");
			main.getLogger().warning(sender.getName() + " reloaded config'o'plugin!");
		}

		if (args[0].equalsIgnoreCase("give")) {
			if (args.length < 6) {
				sender.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
						+ ChatColor.AQUA + "/moofPl give <player> <name> <lore> <id> <meta>");
				return true;
			}
			Player giveTo = main.candles.getPlayer(args[1]);
			if (giveTo == null)
				sender.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Player not found.");
			int id = -1;
			Material Id = null;
			try {
				id = Integer.parseInt(args[4]);
			} catch (Throwable t) {
				Id = Material.getMaterial(args[4].toUpperCase());
				if (id == -1 && Id == null)
					sender.sendMessage(Main.prefix + "Invalid ID!");
			}
			try {
				int meta = Integer.parseInt(args[5]);
				if (id != -1 && Id == null)
					Utils.openMyGUI(giveTo, args[2], args[3], id, meta);
				if (Id != null)
					Utils.openMyGUI(giveTo, args[2], args[3], Id, meta);
			} catch (NumberFormatException nfe) {
				sender.sendMessage(Main.prefix + "Invalid meta!");
				return true;
			}
		}

		if (args[0].equalsIgnoreCase("jail")) {
			if (args.length < 3) {
				sender.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
						+ ChatColor.AQUA + "/moofPl jail <jail> <player>");
				return true;
			}
			Player jailer = main.candles.getPlayer(args[2]);
			if (jailer == null)
				sender.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Player is not online.");
			int c = 0;
			for (Jail i : main.jls)
				if (i.name.equalsIgnoreCase(args[1].toLowerCase())) {
					if (!i.jail(jailer))
						sender.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Player is jailed!");
					else
						sender.sendMessage(Main.prefix + ChatColor.GREEN + ChatColor.BOLD + "Player jailed!");
					++c;
				}
			if (c == 0)
				sender.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Jail not found!");
		}

		if (args[0].equalsIgnoreCase("unjail")) {
			if (args.length < 2) {
				sender.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
						+ ChatColor.AQUA + "/moofPl unjail <player>");
				return true;
			}
			Player jailer = main.candles.getPlayer(args[1]);
			if (jailer == null)
				sender.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Player is not online.");
			if (!main.candles.unJail(jailer))
				sender.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Player is not jailed.");
			else
				sender.sendMessage(Main.prefix + ChatColor.GREEN + ChatColor.BOLD + "Player unjailed =3");
		}

		if (args[0].equalsIgnoreCase("unjailall")) {
			main.candles.unJailAll();
			sender.sendMessage(Main.prefix + ChatColor.GREEN + ChatColor.BOLD + "All players unjailed =3");
		}

		if (args[0].equalsIgnoreCase("map")) {
			if (args.length < 3) {
				sender.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
						+ ChatColor.AQUA + "/moofPl map <player> <file>");
				return true;
			}
			Player give = main.candles.getPlayer(args[1]);
			if (give == null) {
				sender.sendMessage(Main.prefix + ChatColor.RED + "Player is not online!");
				return true;
			}
			if (args[2].equalsIgnoreCase("null")) {
				if (give.getItemInHand().getType() == Material.MAP) {
					give.getItemInHand().setDurability(
							/* Utils.createNewMap(ImageIO.read(img)) */(short) 0);
					ItemMeta meta = give.getItemInHand().getItemMeta();
					meta.setDisplayName(ChatColor.RED + "Null filled map");
					give.getItemInHand().setItemMeta(meta);
				}
				return true;
			} else {
				String file = args[2];
				try {
					if (give.getItemInHand().getType() == Material.MAP) {
						give.getItemInHand().setDurability((short) Integer.parseInt(main.maps.get(file.toLowerCase())));
						ItemMeta meta = give.getItemInHand().getItemMeta();
						meta.setDisplayName(ChatColor.RED + file.substring(0, file.length() - 4));
						give.getItemInHand().setItemMeta(meta);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		if (args[0].equalsIgnoreCase("maplist")) {
			File img = new File(main.getDataFolder() + "/images");
			if (!img.exists())
				img.mkdirs();
			int i = 0;
			sender.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Files:");
			for (String s : img.list())
				sender.sendMessage(ChatColor.BLUE + "" + (++i) + ". " + ChatColor.GREEN + s);
		}

		if (args[0].equalsIgnoreCase("sudo")) {
			if (args.length < 3) {
				sender.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
						+ ChatColor.AQUA + "/moofPl sudo <player> <command>");
				return true;
			}
			Player pl = main.candles.getPlayer(args[1]);
			if (pl == null) {
				sender.sendMessage(Main.prefix + ChatColor.RED + "Player is not online!");
				return true;
			}
			String command = args[2].replaceAll(" ", "").replaceAll("''", " ");

			if (pl.performCommand(command))
				sender.sendMessage(Main.prefix + ChatColor.GREEN + "Sudo success!");
			else
				sender.sendMessage(Main.prefix + ChatColor.RED + "Sudo failed.");
			sender.sendMessage(Main.prefix + ChatColor.GREEN + "Command: " + command);
		}

		if (args[0].equalsIgnoreCase("sudoall")) {
			if (args.length < 2) {
				sender.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
						+ ChatColor.AQUA + "/moofPl sudoall <command>");
				return true;
			}

			String command = args[1].replaceAll(" ", "").replaceAll("''", " ");

			for (PlayerLogger i : main.candles.pl)
				if (i.player.performCommand(command))
					sender.sendMessage(
							Main.prefix + ChatColor.GREEN + "Sudo success! (player: " + i.player.getName() + ")");
				else
					sender.sendMessage(
							Main.prefix + ChatColor.RED + "Sudo failed. (player: " + i.player.getName() + ")");
			sender.sendMessage(Main.prefix + ChatColor.GREEN + "Command: " + command);
		}

		if (args[0].equalsIgnoreCase("antilagg")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lagg clear");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lagg unloadchunks");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stoplag");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stoplag -c");
			System.gc();
			System.out.println("[moofPl] Lagg cleared!");
			sender.sendMessage(Main.prefix + ChatColor.GREEN + "Lagg cleared!");
		}

		if (args[0].equalsIgnoreCase("targetloc")) {
			if (p == null)
				return true;
			@SuppressWarnings("deprecation")
			Location loc = p.getTargetBlock((HashSet<Byte>) null, 5).getLocation();
			sender.sendMessage(Main.prefix + ChatColor.GREEN + "Target location - X: " + loc.getBlockX() + " Y: "
					+ loc.getBlockY() + " Z: " + loc.getBlockZ() + " World: " + loc.getWorld().getName());
		}

		if (args[0].equalsIgnoreCase("saveecs")) {
			main.saveecs();
			sender.sendMessage(Main.prefix + ChatColor.GREEN + "ECs saved.");
		}
		return true;
	}
}
