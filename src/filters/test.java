package filters;
import java.awt.Color;

public class test implements filters.FilterPlugin {
	public String name() {
		return "Test: Greenify";
	}
	
	public void process(InputImage image, PluginHelper h) {
		InputImage[] images = new InputImage[2];
		images[0] = image;
		images[1] = h.requestAdditionalImage(); // returns null if canceled
		for (int i=0; i<2; i++) {
			InputImage img = images[i];
			if (img == null) continue; // !!!
			for (int y=0; y<img.getHeight(); y++) {
				for (int x=0; x<img.getWidth(); x++) {
					Color prev = img.get(x, y);
					img.set(x, y, new Color(prev.getRed(), 255, prev.getBlue(), 255));
				}
			}
		}
	}
}
