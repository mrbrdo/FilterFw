package filters;
import java.awt.Color;

public class test2 implements filters.FilterPlugin {
	public String name() {
		return "Test2: Greenify";
	}
	
	public void process(ImagePixels image) {
		for (int y=0; y<image.getHeight(); y++) {
			for (int x=0; x<image.getWidth(); x++) {
				Color prev = image.get(x, y);
				image.set(x, y, new Color(prev.getRed(), 255, prev.getBlue(), 255));
			}
		}
	}
}
