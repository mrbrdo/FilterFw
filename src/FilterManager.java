import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import filters.FilterPlugin;

public class FilterManager {
	String filtersPath = "";
	ArrayList<FilterPlugin> filters;
	FilterPlugin lastUsed = null;
	FilterFw mainFrame;
	
	public FilterPlugin getLastUsed() {
		return lastUsed;
	}

	public FilterManager(FilterFw mainFrame, String filtersPath) {
		this.filtersPath = filtersPath;
		this.mainFrame = mainFrame;
		filters = new ArrayList<FilterPlugin>();
		loadAllFilters();
	}
	
	public void loadAllFilters() {
	    File folder = new File(filtersPath);
	    File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  String filename = listOfFiles[i].getName();
	    	  String ext = filename.substring(filename.lastIndexOf('.')+1, filename.length());
	    	  String name = filename.substring(0, filename.lastIndexOf('.'));
	    	  if (ext.equals("class") &&
	    			  !name.equals("PluginHelper") &&
	    			  !name.equals("FilterPlugin") &&
	    			  !name.equals("InputImage"))
	    		  reloadFilter(name);
	      } else if (listOfFiles[i].isDirectory()) {
	      }
	    }
	}
	
	public PluginHelperImpl process(FilterFw frame, BufferedImage image, String filterName) {
		for (FilterPlugin fp : filters) {
			if (fp.getClass().getName().equals(filterName)) {
				return process(frame, image, fp);
			}
		}
		return null;
	}
	
	public PluginHelperImpl process(FilterFw frame, BufferedImage image, FilterPlugin filter) {
		if (image == null) return null;
		PluginHelperImpl ph = new PluginHelperImpl(frame, image);
		InputImageImpl ii = new InputImageImpl(image);
		filter.process(ii, ph);
		lastUsed = filter;
		return ph;
	}
	
	public boolean reloadFilter(String name) {
	    ClassLoader parentClassLoader = DynamicClassLoader.class.getClassLoader();
	    DynamicClassLoader classLoader = new DynamicClassLoader(parentClassLoader);
	    try {
			Class<FilterPlugin> objClass = classLoader.loadClass(filtersPath, name);
			Class[] interfaces = objClass.getInterfaces();
			boolean doesImplement = false;
			for (Class intf : interfaces) {
				if (intf.getName().matches(".*?FilterPlugin")) { doesImplement = true; break; }
			}
			if (!doesImplement) throw new Exception("Main class of plugin \"" + name + "\" does not seem to implement the FilterPlugin interface.");
			
			FilterPlugin newFilter = (FilterPlugin) objClass.newInstance();
			System.out.println("Loading filter: " + objClass.getName());
			for (int i=0; i<filters.size(); i++) {
				FilterPlugin fp = filters.get(i);
				if (fp.getClass().getName() == objClass.getName()) {
					filters.remove(fp);
				}
			}
			filters.add(newFilter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	    return true;
	}
}

class DynamicClassLoader extends ClassLoader {

    public DynamicClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<FilterPlugin> loadClass(String path, String name) throws ClassNotFoundException {
        try {
            String url = path + "/" + name + ".class";
			/*URL myUrl = new URL(url);
			URLConnection connection = myUrl.openConnection();
			InputStream input = connection.getInputStream();*/
            FileInputStream input = new FileInputStream(new File(url));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while(data != -1){
                buffer.write(data);
                data = input.read();
            }

            input.close();

            byte[] classData = buffer.toByteArray();
            
            return (Class<FilterPlugin>) defineClass("filters." + name, classData, 0, classData.length);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace(); 
        }

        return null;
    }

}