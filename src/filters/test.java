package filters;
import java.awt.Color;
import java.util.Random;

public class test implements filters.FilterPlugin {
	public String name() {
		return "Test1: Random noise";
	}
	
	public void process(ImagePixels image) {
		Random rand = new Random();
		for (int i=0; i<image.getHeight(); i++) {
			for (int j=0; j<image.getWidth(); j++) {
				image.set(j, i, new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), 255));
			}
		}
	}
}
