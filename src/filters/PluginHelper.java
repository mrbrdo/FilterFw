package filters;

// Passed to plugins for modification (implemented in ImagePixelsImpl.java)
public interface PluginHelper {
	public InputImage requestAdditionalImage();
	public InputImage[] allImages();
	public InputImage newImage(int width, int height);
}
