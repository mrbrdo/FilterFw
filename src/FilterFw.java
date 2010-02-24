import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

import filters.FilterPlugin;

// Panel for drawing images
class JImagePanel extends JPanel {
	private static final long serialVersionUID = 4292464394145538564L;
	private BufferedImage image;  
    int x, y;
    private static final int histogramHeight = 100;
    
    public JImagePanel(BufferedImage image, int x, int y) {  
        super();
        this.x = x;  
        this.y = y;
        setImage(image);
    }
    
    public BufferedImage getImage() {
    	return image;
    }
    
    public void setImage(BufferedImage _image) {
    	image = _image;
    	if (image == null) return;
    	int prefWidth = image.getWidth();
    	if (3*256 > prefWidth) prefWidth = 3*256;
    	setPreferredSize(new Dimension(prefWidth, image.getHeight() + histogramHeight));
    	this.repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(new Color(100, 100, 100, 255));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
    	if (image != null) {
			int y_start = this.getHeight() - histogramHeight;
			
    		g.drawImage(image.getScaledInstance(-1, y_start, Image.SCALE_FAST), x, y, null);
    	
	    	// Histogram
			int[][] freq = new int[3][256];
			for (int i=0; i<3; i++) {
				for (int j=0; j<256; j++) {
					freq[i][j] = 0;
				}
			}
	    	for (int x=0; x<image.getWidth(); x++) {
	    		for (int y=0; y<image.getHeight(); y++) {
	    			Color pixel = ImagePixelsImpl.int2Color(image.getRGB(x, y));
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
			
			for (int i=0; i<3; i++) {
				for (int j=0; j<256; j++) {
					Color c = Color.red;
					switch (i) {
					case 1: { c = Color.green; break; }
					case 2: { c = Color.blue; break; }
					}
					g.setColor(c);
					g.drawLine(i*256+j, y_start + histogramHeight - (int) (freq[i][j] * mul), i*256+j, y_start + histogramHeight);
				}
			}
    	}
    }
}  

// Filter for open file dialog
class CustomFileFilter extends javax.swing.filechooser.FileFilter {
	private String[] fileExts;
	public CustomFileFilter(String[] _fileExts) {
		fileExts = _fileExts;
	}
    public boolean accept(File file) {
    	if (file.isDirectory()) return true;
        String filename = file.getName();
        for (String s : fileExts) {
        	if (filename.endsWith("." + s))
        		return true;
        }
        return false;
    }
    public String getDescription() {
    	String desc = "";
        for (String s : fileExts) {
        	desc += ", *." + s;
        }
        return desc.substring(2);
    }
}

// Generic action listener that accepts a frame parameter
abstract class FrameActionListener implements ActionListener {
	FilterFw frame;
	public FrameActionListener(FilterFw _frame) {
		frame = _frame;
	}
}

// Action listener for filter menu buttons
class FilterActionListener extends FrameActionListener {
	public FilterActionListener(FilterFw _frame) {
		super(_frame);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		frame.lastFilter = ((JMenuItem)event.getSource()).getName();
		frame.filterManager.process(frame.imagePanel.getImage(), frame.lastFilter);
		frame.imagePanel.repaint();
		frame.filtersMenu.getItem(0).setEnabled(true);
	}
	
}

// Main class (JFrame)
public class FilterFw extends JFrame {
	// Evil public vars
	public JImagePanel imagePanel;
	public FilterManager filterManager;
	public JMenu filtersMenu;
	public String lastFilter = null;
	public String currentImageFilename = null;
	
	public FilterFw() {
		setTitle("FilterFw");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		// Menu
        JMenuBar menubar = new JMenuBar();
        
        // File
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        // Open
        JMenuItem fileOpen = new JMenuItem("Open");
		fileOpen.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        fileOpen.setMnemonic(KeyEvent.VK_O);
        fileOpen.addActionListener(new FrameActionListener(this) {
            public void actionPerformed(ActionEvent event) {
            	JFileChooser fc = new JFileChooser();
            	addImageFilters(fc);
            	if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            		frame.currentImageFilename = fc.getSelectedFile().getAbsolutePath();
            		loadImage(frame.currentImageFilename);
            	}
            }
        });
        file.add(fileOpen);
        // ReOpen
        JMenuItem fileReopen = new JMenuItem("Reopen");
        fileReopen.setMnemonic(KeyEvent.VK_R);
        fileReopen.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
        fileReopen.addActionListener(new FrameActionListener(this) {
            public void actionPerformed(ActionEvent event) {
            	loadImage(frame.currentImageFilename);
            }
        });
        file.add(fileReopen);
        // Save
        JMenuItem fileSave = new JMenuItem("Save");
        fileSave.setMnemonic(KeyEvent.VK_S);
		fileSave.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        fileSave.addActionListener(new FrameActionListener(this) {
            public void actionPerformed(ActionEvent event) {
            	JFileChooser fc = new JFileChooser();
            	//addImageFilters(fc);
            	if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
            		saveImage(fc.getSelectedFile().getAbsolutePath());
            }
        });
        file.add(fileSave);
        // Exit
        JMenuItem fileClose = new JMenuItem("Exit");
        fileClose.setMnemonic(KeyEvent.VK_E);
        fileClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        file.add(fileClose);

        filtersMenu = new JMenu("Filters");
        filtersMenu.setMnemonic(KeyEvent.VK_I);

        menubar.add(file);
        menubar.add(filtersMenu);
        setJMenuBar(menubar);

        // ImagePanel
        imagePanel = new JImagePanel(null, 0, 0);
        add(imagePanel, BorderLayout.CENTER);

		setSize(300, 300);
		setVisible(true);
		
		filterManager = new FilterManager(getClass().getProtectionDomain().getCodeSource().getLocation().getFile() + "/filters");
		refreshFiltersMenu();
	}
	
	public void refreshFiltersMenu() {
		JMenuItem mi;
		filtersMenu.removeAll();

		mi = new JMenuItem("Repeat last filter");
		mi.addActionListener(new FrameActionListener(this) {
            public void actionPerformed(ActionEvent event) {
            	if (frame.lastFilter != null) {
	        		frame.filterManager.process(frame.imagePanel.getImage(), frame.lastFilter);
	        		frame.imagePanel.repaint();
            	}
            }
		});
		mi.setAccelerator(KeyStroke.getKeyStroke("ctrl F"));
		mi.setEnabled(false);
		filtersMenu.add(mi);
		
		// Filters
		for (FilterPlugin filter : filterManager.filters) {
			mi = new JMenuItem(filter.name());
			mi.setName(filter.getClass().getName());
			mi.addActionListener(new FilterActionListener(this));
			filtersMenu.add(mi);
		}
		
		mi = new JMenuItem("Reload filters");
		mi.addActionListener(new FrameActionListener(this) {
            public void actionPerformed(ActionEvent event) {
                frame.filterManager.loadAllFilters();
                frame.refreshFiltersMenu();
            }
		});
		filtersMenu.add(mi);
	}
	
	public static void addImageFilters(JFileChooser fc) {
    	fc.addChoosableFileFilter(new CustomFileFilter(new String[] {"jpeg", "jpg", "png", "gif"}));
	}
	
	public void saveImage(String ref) {  
	    try {
	    	if (ref.indexOf(".") == -1) ref += ".jpg"; // default ext, .jpg
	        String format = (ref.endsWith(".png")) ? "png" : "jpg";  
	        ImageIO.write(imagePanel.getImage(), format, new File(ref));  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	}  
	
	public void loadImage(String ref) {  
		if (ref == null || ref == "") return;
        BufferedImage bimg = null;  
        try {
            bimg = ImageIO.read(new File(ref));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        imagePanel.setImage(bimg);
		pack();
    }
	
	public static void main(String[] args) {
		FilterFw ui = new FilterFw();
		ui.setVisible(true);
	}
}