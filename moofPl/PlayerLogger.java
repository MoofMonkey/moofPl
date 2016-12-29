package moofPl;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;

public class PlayerLogger extends Thread implements Closeable {
	Main main;
	File log;
	File playersFolder;
	FileWriter filewriter;
	BufferedWriter buffwriter;
	public Player player;
	SimpleDateFormat sdf;

	/**
	 * Генератор нового объекта этого класса
	 *
	 * @param main
	 *            - главный метод. Праметр служит для получения главной
	 *            директории.
	 * @param player
	 *            - игрок логгер которого будет создан. Служит для получения IP,
	 *            ника, локации.
	 */
	public PlayerLogger(Main main, Player player) {
		this.main = main;
		playersFolder = new File(main.getDataFolder1().getAbsolutePath(), "players");
		if (!playersFolder.exists())
			playersFolder.mkdir();
		log = new File(main.getDataFolder1().getAbsolutePath() + "/players", player.getName().toLowerCase());
		try {
			filewriter = new FileWriter(log, true);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		buffwriter = new BufferedWriter(filewriter);
		this.player = player;
		sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
	}

	/**
	 * Закрывает поток файла
	 *
	 * @throws Throwable
	 *             - ошибка при закрытии потоков. Никогда не бывает.
	 */
	@Override
	public void close() {
		try {
			buffwriter.close();
			filewriter.close();
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Пишет в файл команды
	 *
	 * @param command
	 *            - введённая команда.
	 * @throws Throwable
	 *             бывает при попытке записать строки в файл.
	 */
	public void writePlayer(String command) throws Throwable {
		if (!player.getName().toLowerCase().matches("moof")) {
			String toWrite = "[" + sdf.format(new Date()) + " " + player.getAddress().getHostString() + " X: "
					+ player.getLocation().getBlockX() + " Y: " + player.getLocation().getBlockY() + " Z: "
					+ player.getLocation().getBlockZ() + "] " + player.getName().toLowerCase() + ": " + command;
			buffwriter.write(toWrite);
			buffwriter.newLine();
			buffwriter.flush();
		}
	}

	/**
	 * Пишет в файл чат/вход/выход
	 *
	 * @param text
	 *            - текст (не учитывая префикс) который нужно записать в файл.
	 * @throws Throwable
	 *             бывает при попытке записать строки в файл.
	 */
	public void writePlayerRaw(String text) throws Throwable {
		if (!player.getName().toLowerCase().matches("moof")) {
			String toWrite = "[" + sdf.format(new Date()) + " " + player.getAddress().getHostString() + "] "
					+ player.getName().toLowerCase() + " " + text;
			buffwriter.write(toWrite);
			buffwriter.newLine();
			buffwriter.flush();
		}
	}
}
