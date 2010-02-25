import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class ImageHelper {
	public static Color argb2Color(int argb) {
		return new Color((argb >> 16) & 0x000000FF,
				(argb >> 8) & 0x000000FF,
				argb & 0x000000FF, argb >> 24 & 0x000000FF);
	}
	
	public static BufferedImage loadImage(String ref) {  
		if (ref == null || ref == "") return null;
        BufferedImage bimg = null;  
        try {
            bimg = ImageIO.read(new File(ref));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return bimg;
    }
	
	public static String saveImage(BufferedImage image, String ref) {  
	    try {
	    	if (ref.indexOf(".") == -1) ref += ".jpg"; // default ext, .jpg
	        String format = (ref.endsWith(".png")) ? "png" : "jpg";  
	        ImageIO.write(image, format, new File(ref));  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	    return ref;
	}
}
