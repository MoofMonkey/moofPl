package moofPl.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import moofPl.Main;
import moofPl.Utils;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class MainCommand implements CommandExecutor {
	Main main;
	String[] help = new String[] {
		"help"	
	};

	/**
	 * Генератор нового объекта этого класса
	 *
	 * @param main
	 *            - главный метод. Праметр служит для получения Handlers,
	 *            перезагрузки конфигурации и логгера
	 */
	public MainCommand(Main main) {
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
		
		if(args.length == 0)
			args = help;
		
		MainSubCommands.getExecutorByName (
				args[0]
		).handle (
				main,
				p,
				sender,
				commandLabel,
				args
		);
		
		return true;
	}
}
