package moofPl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;

public class PlayerLogger extends Thread {
	Main main;
	File log;
	File playersFolder;
	FileWriter filewriter;
	BufferedWriter buffwriter;
	public Player player;
	SimpleDateFormat sdf;

	/**
	 * ��������� ������ ������� ����� ������
	 *
	 * @param main
	 *            - ������� �����. ������� ������ ��� ��������� �������
	 *            ����������.
	 * @param player
	 *            - ����� ������ �������� ����� ������. ������ ��� ��������� IP,
	 *            ����, �������.
	 */
	public PlayerLogger(Main main, Player player) {
		this.main = main;
		playersFolder = new File(main.getDataFolder().getAbsolutePath(), "players");
		if (!playersFolder.exists())
			playersFolder.mkdir();
		log = new File(main.getDataFolder().getAbsolutePath() + "/players", player.getName().toLowerCase());
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
	 * ��������� ����� �����
	 *
	 * @throws Throwable
	 *             - ������ ��� �������� �������. ������� �� ������.
	 */
	public void close() throws Throwable {
		buffwriter.close();
		filewriter.close();
	}

	/**
	 * ����� � ���� �������
	 *
	 * @param command
	 *            - �������� �������.
	 * @throws Throwable
	 *             ������ ��� ������� �������� ������ � ����.
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
	 * ����� � ���� ���/����/�����
	 *
	 * @param text
	 *            - ����� (�� �������� �������) ������� ����� �������� � ����.
	 * @throws Throwable
	 *             ������ ��� ������� �������� ������ � ����.
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
