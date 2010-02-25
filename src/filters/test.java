package filters;
import java.awt.Color;
import java.util.Random;

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
		
		// new image - noise
		Random rand = new Random();
		InputImage n = h.newImage(200, 200);
		for (int x=0; x<200; x++) {
			for (int y=0; y<200; y++) {
				n.set(x, y, new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256), 255));
			}
		}
	}
}
