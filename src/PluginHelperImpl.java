import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import filters.InputImage;
import filters.PluginHelper;


public class PluginHelperImpl implements PluginHelper {
	private FilterFw frame;
	private ArrayList<BufferedImage> usedImages;
	
	public PluginHelperImpl(FilterFw frame, BufferedImage image) {
		this.frame = frame;
		usedImages = new ArrayList<BufferedImage>();
		usedImages.add(image);
	}

	@Override
	public InputImage newImage(int width, int height) {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		frame.openImageEditor("New image", newImage, null);
		return new InputImageImpl(newImage);
	}
	
	@Override
	public InputImage[] allImages() {
		ArrayList<ImageFrame> list = frame.getImageFrames();
		if (list.size() == 0) return null;
		InputImage[] result = new InputImage[list.size()];
		for (int i=0; i<list.size(); i++) {
			BufferedImage p = list.get(i).getImage();
			result[i] = new InputImageImpl(p);
			usedImages.add(p);
		}
		return result;
	}
	
	@Override
	public InputImage requestAdditionalImage() {
		ArrayList<ImageFrame> list = frame.getImageFrames();
		if (list.size() - usedImages.size() <= 0) return null;
		Object[] possibilities = new Object[list.size() - usedImages.size()];
		for (int i=0, j=0; i<list.size(); i++) {
			if (usedImages.indexOf(list.get(i).getImage()) == -1)
				possibilities[j++] = Integer.toString(i+1) + ": " + list.get(i).getTitle();
		}
		String s = (String)JOptionPane.showInputDialog(
		                    frame,
		                    "Please select an additional image to be used in the filter:",
		                    "Additional image required",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    possibilities,
		                    possibilities[0]);

		if ((s != null) && (s.length() > 0)) {
			int num = Integer.parseInt(s.substring(0, s.indexOf(":")));
			BufferedImage p = list.get(num-1).getImage();
			usedImages.add(p);
		    return new InputImageImpl(p);
		}
		return null;
	}

	public ArrayList<BufferedImage> getUsedImages() {
		return usedImages;
	}
}
