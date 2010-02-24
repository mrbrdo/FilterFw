import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImagePixelsImpl implements filters.ImagePixels {
	BufferedImage img = null;
	
	public void setImage(BufferedImage image) {
		img = image;
	}
	
	public static Color int2Color(int argb) {
		return new Color((argb >> 16) & 0x000000FF,
				(argb >> 8) & 0x000000FF,
				argb & 0x000000FF, argb >> 24 & 0x000000FF);
	}
	
	@Override
	public Color get(int x, int y) {
		int argb = img.getRGB(x, y);
		return int2Color(argb);
	}

	@Override
	public void set(int x, int y, Color color) {
		// TODO Auto-generated method stub
		img.setRGB(x, y, color.getRGB());
	}

	@Override
	public int getHeight() {
		return img.getHeight();
	}

	@Override
	public int getWidth() {
		return img.getWidth();
	}

}
