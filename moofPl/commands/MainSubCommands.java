package moofPl.commands;

import java.io.File;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import moofPl.Jail;
import moofPl.Main;
import moofPl.PlayerLogger;
import moofPl.Utils;

public enum MainSubCommands {
	GIVE (
		"give",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				if (args.length < 6) {
					p.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
							+ ChatColor.AQUA + "/moofPl give <player> <name> <lore> <id> <meta>");
					return;
				}
				
				Player giveTo = main.candles.getPlayer(args[1]);
				if (giveTo == null)
					p.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Player not found.");
				
				int id = -1;
				Material Id = null;
				try {
					id = Integer.parseInt(args[4]);
				} catch (Throwable t) {
					Id = Material.getMaterial(args[4].toUpperCase());
					if (id == -1 && Id == null)
						p.sendMessage(Main.prefix + "Invalid ID!");
				}
				
				try {
					int meta = Integer.parseInt(args[5]);
					if (id != -1 && Id == null)
						Utils.openMyGUI(giveTo, args[2], args[3], id, meta);
					if (Id != null)
						Utils.openMyGUI(giveTo, args[2], args[3], Id, meta);
				} catch (NumberFormatException nfe) {
					p.sendMessage(Main.prefix + "Invalid meta!");
					return;
				}
			}
		}
	),
	JAIL (
		"jail",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				if (args.length < 3) {
					p.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
							+ ChatColor.AQUA + "/moofPl jail <jail> <player>");
					return;
				}
				
				Player jailer = main.candles.getPlayer(args[2]);
				if (jailer == null)
					p.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Player is not online.");
				
				int c = 0;
				for (Jail i : main.jls)
					if (i.name.equalsIgnoreCase(args[1].toLowerCase())) {
						if (!i.jail(jailer))
							p.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Player is jailed!");
						else
							p.sendMessage(Main.prefix + ChatColor.GREEN + ChatColor.BOLD + "Player jailed!");
						++c;
					}
				
				if (c == 0)
					p.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Jail not found!");
			}
		}
	),
	UNJAIL (
		"unjail",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				if (args.length < 2) {
					p.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
							+ ChatColor.AQUA + "/moofPl unjail <player>");
					return;
				}
				
				Player jailer = main.candles.getPlayer(args[1]);
				if (jailer == null)
					p.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Player is not online.");
				if (!main.candles.unJail(jailer))
					p.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "Player is not jailed.");
				else
					p.sendMessage(Main.prefix + ChatColor.GREEN + ChatColor.BOLD + "Player unjailed =3");
			}
		}
	),
	UNJAILALL (
		"unjailall",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				main.candles.unJailAll();
				p.sendMessage(Main.prefix + ChatColor.GREEN + ChatColor.BOLD + "All players unjailed =3");
			}
		}
	),
	GIVEMAP (
		"map",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				if (args.length < 3) {
					p.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
							+ ChatColor.AQUA + "/moofPl map <player> <file>");
					return;
				}
				Player give = main.candles.getPlayer(args[1]);
				if (give == null) {
					p.sendMessage(Main.prefix + ChatColor.RED + "Player is not online!");
					return;
				}
				
				if (args[2].equalsIgnoreCase("null")) {
					if (give.getItemInHand().getType() == Material.MAP) {
						give.getItemInHand().setDurability((short) 0);
						ItemMeta meta = give.getItemInHand().getItemMeta();
						meta.setDisplayName(ChatColor.RED + "Null filled map");
						give.getItemInHand().setItemMeta(meta);
					}
					return;
				} else {
					String file = args[2];
					try {
						if (give.getItemInHand().getType() == Material.MAP) {
							give.getItemInHand().setDurability(main.maps.get(file.toLowerCase()));
							ItemMeta meta = give.getItemInHand().getItemMeta();
							meta.setDisplayName(ChatColor.RED + file.substring(0, file.length() - 4));
							give.getItemInHand().setItemMeta(meta);
						}
					} catch (Throwable e) {
						give.sendMessage(ChatColor.DARK_AQUA + "Not found map " + file);
					}
				}
			}
		}
	),
	MAPLIST (
		"maplist",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				File img = new File(main.getDataFolder() + "/images");
				if (!img.exists())
					img.mkdirs();
				int i = 0;
				p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Files:");
				for (String s : img.list())
					p.sendMessage(ChatColor.BLUE + "" + (++i) + ". " + ChatColor.GREEN + s);
			}
		}
	),
	SUDO (
		"sudo",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				if (args.length < 3) {
					p.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
							+ ChatColor.AQUA + "/moofPl sudo <player> <command>");
					return;
				}
				Player pl = main.candles.getPlayer(args[1]);
				if (pl == null) {
					p.sendMessage(Main.prefix + ChatColor.RED + "Player is not online!");
					return;
				}
				String command = args[2].replaceAll(" ", "").replaceAll("''", " ");

				if (pl.performCommand(command))
					p.sendMessage(Main.prefix + ChatColor.GREEN + "Sudo success!");
				else
					p.sendMessage(Main.prefix + ChatColor.RED + "Sudo failed.");
				p.sendMessage(Main.prefix + ChatColor.GREEN + "Command: " + command);
			}
		}
	),
	SUDOALL (
		"sudoall",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				if (args.length < 2) {
					p.sendMessage(Main.prefix + ChatColor.BOLD + ChatColor.GOLD + "Usage: " + ChatColor.RESET
							+ ChatColor.AQUA + "/moofPl sudoall <command>");
					return;
				}

				String command = args[1].replaceAll(" ", "").replaceAll("''", " ");

				for (PlayerLogger i : main.candles.pl)
					if (i.player.performCommand(command))
						p.sendMessage(
								Main.prefix + ChatColor.GREEN + "Sudo success! (player: " + i.player.getName() + ")");
					else
						p.sendMessage(
								Main.prefix + ChatColor.RED + "Sudo failed. (player: " + i.player.getName() + ")");
				p.sendMessage(Main.prefix + ChatColor.GREEN + "Command: " + command);
			}
		}
	),
	TARGETLOC (
		"targetloc",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				if (p == null)
					return;
				@SuppressWarnings("deprecation")
				Location loc = p.getTargetBlock((HashSet<Byte>) null, 5).getLocation();
				p.sendMessage(Main.prefix + ChatColor.GREEN + "Target location - X: " + loc.getBlockX() + " Y: "
						+ loc.getBlockY() + " Z: " + loc.getBlockZ() + " World: " + loc.getWorld().getName());
			}
		}
	),
	SAVEECS (
		"saveecs",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				main.saveecs();
				p.sendMessage(Main.prefix + ChatColor.GREEN + "ECs saved.");
			}
		}
	),
	RELOADCONFIG (
		"reload",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				main.reloadConfiguration();
				p.sendMessage(Main.prefix + "Config reloaded!");
				main.getLogger().warning(p.getName() + " reloaded config'o'plugin!");
			}
		}
	),
	UNKNOWN (
		"help",
		new SubCommandExecutor() {
			@Override
			public void handle(Main main, Player p, String commandLabel, String[] args) {
				p.sendMessage(helpCmd);
			}
		}
	),
	;
	
	String[] names;
	SubCommandExecutor executor;
	
	static String helpCmd;
	
	private MainSubCommands(String _name, SubCommandExecutor _executor) {
		names = new String[] {
				_name
		};
		executor = _executor;
	}
	
	private MainSubCommands(String[] _names, SubCommandExecutor _executor) {
		names = _names;
		executor = _executor;
	}
	
	public static SubCommandExecutor getExecutorByName(String name) {
		MainSubCommands cmd = UNKNOWN;
		
		mainLoop:
		for(MainSubCommands loopCmd : values())
			for(String loopName : loopCmd.names)
				if(loopName.equalsIgnoreCase(name)) {
					cmd = loopCmd;
					break mainLoop;
				}
		
		return cmd.executor;
	}
	
	private static void init() {
		String cmds = "";
		for(MainSubCommands cmd : values()) {
			for(String name : cmd.names)
				cmds += name + ", ";

			if(cmds.endsWith(", "))
				cmds = cmds.substring(0, cmds.length() - 2);
			cmds += " | ";
		}
		cmds = cmds.substring(0, cmds.length() - 3);
		
		helpCmd =
				Main.prefix +
				ChatColor.BOLD + ChatColor.GOLD + "Usage: " +
				ChatColor.RESET + ChatColor.AQUA
				+ "/moofPl <" + cmds + ">"
		;
	}
	
	static {
		init();
	}
}
