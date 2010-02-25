package filters;

import java.awt.Color;
import java.awt.image.BufferedImage;

// Passed to plugins for modification (implemented in ImagePixelsImpl.java)
public interface InputImage {
	public void set(int x, int y, Color color);
	public Color get(int x, int y);
	public int getWidth();
	public int getHeight();
	public BufferedImage getSourceImage();
}
