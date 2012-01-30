package toritools.render;

import java.awt.Color;

public class ColorUtils {

	public static Color blend(Color color1, Color color2, double ratio) {
		float r = (float) ratio;
		float ir = (float) 1.0 - r;

		float rgb1[] = new float[3];
		float rgb2[] = new float[3];

		color1.getColorComponents(rgb1);
		color2.getColorComponents(rgb2);

		Color color = new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r
				+ rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir);

		return color;
	}
}
