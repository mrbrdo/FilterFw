import java.awt.Color;
import java.awt.image.BufferedImage;
import filters.InputImage;


public class InputImageImpl implements InputImage {
	private BufferedImage img = null;
	
	public InputImageImpl(BufferedImage image) {
		img = image;
	}
	
	@Override
	public Color get(int x, int y) {
		int argb = img.getRGB(x, y);
		return ImageHelper.argb2Color(argb);
	}

	@Override
	public void set(int x, int y, Color color) {
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
	
	@Override
	public BufferedImage getSourceImage() {
		return img;
	}
}
