package filters;

// Interface for plugins to follow
public interface FilterPlugin {
	public String name();
	public void process(ImagePixels image);
}