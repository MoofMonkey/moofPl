package moofPl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapRender extends MapRenderer {

	public static BufferedImage writeTextOnImage(BufferedImage img, int x, int y, String fontname, int fontsize,
			String name_color, boolean stroke, String stroke_color, String text) {
		BufferedImage image = new BufferedImage(img.getColorModel(), img.copyData(null),
				img.getColorModel().isAlphaPremultiplied(), null);
		Graphics g = image.getGraphics();
		Font font = new Font(fontname, 0, fontsize);
		g.setFont(font);
		if (stroke) {
			g.setColor(Color.decode(stroke_color));
			g.drawString(text, x - 1, y);
			g.drawString(text, x + 1, y);
			g.drawString(text, x, y - 1);
			g.drawString(text, x, y + 1);
		}
		g.setColor(Color.decode(name_color));
		g.drawString(text, x, y);
		return image;
	}

	int name_x = 1;
	int name_y = 122;
	String font_name = "Serif";
	int font_size = 9;
	String name_color = "#000000";
	String stroke_color = "#FFFFFF";
	boolean stroke = true;

	boolean first = true;
	BufferedImage img;
	int i = 0;

	/**
	 * Генератор нового объекта этого класса
	 *
	 * @param img2
	 *            - картинка для карты
	 */
	public MapRender(BufferedImage img2) {
		img = img2;
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player p) {
		if (first) {
			canvas.drawImage(0, 0, img);
			first = false;
		}
	}
}
