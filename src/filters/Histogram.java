package filters;

import java.awt.Color;
import java.awt.Graphics2D;

public class Histogram implements filters.FilterPlugin {
	private final static int histogramHeight = 100;
	
	public String name() {
		return "Histogram";
	}
	
	public void process(InputImage image, PluginHelper h) {
		int[][] freq = new int[3][256];
		for (int i=0; i<3; i++) {
			for (int j=0; j<256; j++) {
				freq[i][j] = 0;
			}
		}
    	for (int x=0; x<image.getWidth(); x++) {
    		for (int y=0; y<image.getHeight(); y++) {
    			Color pixel = image.get(x, y);
    			freq[0][pixel.getRed()]++;
    			freq[1][pixel.getGreen()]++;
    			freq[2][pixel.getBlue()]++;
    		}
    	}
    	
    	int maxval = 0;
		for (int i=0; i<3; i++) {
			for (int j=0; j<256; j++) {
				if (maxval < freq[i][j]) maxval = freq[i][j];
			}
		}
    	
		float mul = histogramHeight / (float) maxval;
		
		InputImage histogramImage = h.newImage(3*256, histogramHeight);
		Graphics2D g = histogramImage.getSourceImage().createGraphics();
		for (int i=0; i<3; i++) {
			for (int j=0; j<256; j++) {
				Color c = Color.red;
				switch (i) {
				case 1: { c = Color.green; break; }
				case 2: { c = Color.blue; break; }
				}
				g.setColor(c);
				g.drawLine(i*256+j, histogramHeight - (int) (freq[i][j] * mul), i*256+j, histogramHeight);
			}
		}
		g.dispose();
	}
}
