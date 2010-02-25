import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import filters.FilterPlugin;

public class FilterFw extends JFrame {
	private MDIDesktopPane desktop = new MDIDesktopPane();
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu editMenu = new JMenu("Edit");
	private JMenu viewMenu = new JMenu("View");
	private JMenu filtersMenu = new JMenu("Filters");
	private JScrollPane scrollPane = new JScrollPane();
	public FilterManager filterManager;

	public FilterFw() {
		// =================================
		// BUILD MENU
		// =================================
		JMenuItem mi;
		fileMenu.setMnemonic(KeyEvent.VK_F);
		// File -> Open
        mi = new JMenuItem("Open");
		mi.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        mi.setMnemonic(KeyEvent.VK_O);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	JFileChooser fc = new JFileChooser();
            	addFileFilters(fc);
            	if (fc.showOpenDialog(getSelf()) == JFileChooser.APPROVE_OPTION) {
            		getSelf().openImage(fc.getSelectedFile().getAbsolutePath());
            	}
            }
        });
        fileMenu.add(mi);
		// File -> Reopen
        mi = new JMenuItem("Reopen");
		mi.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
        mi.setMnemonic(KeyEvent.VK_R);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ImageFrame f = getSelf().getSelectedFrame();
            	if (f != null) {
            		f.reloadImage();
            	}
            }
        });
        fileMenu.add(mi);
        // Save
        mi = new JMenuItem("Save");
        mi.setMnemonic(KeyEvent.VK_S);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ImageFrame f = getSelf().getSelectedFrame();
            	f.trySaveImage();
            }
        });
        fileMenu.add(mi);
        // Save As
        mi = new JMenuItem("Save As");
		mi.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        mi.setMnemonic(KeyEvent.VK_S);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ImageFrame f = getSelf().getSelectedFrame();
            	f.saveImageAs();
            }
        });
        fileMenu.add(mi);
        // Open project
        mi = new JMenuItem("Open Project");
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
				try {
			    	JFileChooser fc = new JFileChooser();
			    	if (fc.showOpenDialog(getSelf()) == JFileChooser.APPROVE_OPTION) {
		            	FileInputStream underlyingStream = new FileInputStream(fc.getSelectedFile().getAbsolutePath());
		            	ObjectInputStream serializer;
						serializer = new ObjectInputStream(underlyingStream);
		            	ArrayList<String> filenames = (ArrayList<String>) serializer.readObject();
		            	serializer.close();
		            	underlyingStream.close();
		            	if (filenames != null) {
		            		for (String filename : filenames) {
		            			openImage(filename);
		            		}
		            	}
			    	}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
        fileMenu.add(mi);
        // Save project
        mi = new JMenuItem("Save Project");
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	JOptionPane.showMessageDialog(getSelf(),
            		    "All images must now be saved unless they already exist on disk, or they will not be added to the project!",
            		    "Warning",
            		    JOptionPane.WARNING_MESSAGE);
            	saveAllImages();
            	JOptionPane.showMessageDialog(getSelf(),
            		    "Now choose the filename for the project (no file extension required).", 
            		    "Message", JOptionPane.INFORMATION_MESSAGE);
				try {
			    	JFileChooser fc = new JFileChooser();
			    	if (fc.showSaveDialog(getSelf()) == JFileChooser.APPROVE_OPTION) {
		            	FileOutputStream underlyingStream = new FileOutputStream(fc.getSelectedFile().getAbsolutePath());
		            	ObjectOutputStream serializer;
						serializer = new ObjectOutputStream(underlyingStream);
		            	ArrayList<String> filenames = new ArrayList<String>();
		            	for (ImageFrame f : getImageFrames()) {
		            		String path = f.getImagePath();
		            		if (path != null && path != "") filenames.add(path);
		            	}
		            	serializer.writeObject(filenames);
		            	serializer.close();
		            	underlyingStream.close();
			    	}
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        });
        fileMenu.add(mi);
        // File -> Exit
        mi = new JMenuItem("Exit");
        mi.setMnemonic(KeyEvent.VK_E);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        fileMenu.add(mi);
		// View -> Zoom In
        mi = new JMenuItem("Zoom in");
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_MASK));
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ImageFrame f = getSelf().getSelectedFrame();
            	if (f != null) {
            		f.zoom(0.1f, true);
            	}
            }
        });
        viewMenu.add(mi);
		// View -> Zoom Out
        mi = new JMenuItem("Zoom out");
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_MASK));
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ImageFrame f = getSelf().getSelectedFrame();
            	if (f != null) {
            		f.zoom(-0.1f, true);
            	}
            }
        });
        viewMenu.add(mi);
		// View -> Zoom 100%
        mi = new JMenuItem("Zoom to 100%");
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ImageFrame f = getSelf().getSelectedFrame();
            	if (f != null) {
            		f.zoom(1.0f, false);
            	}
            }
        });
        viewMenu.add(mi);
		// Edit -> Duplicate
        mi = new JMenuItem("Duplicate image");
		mi.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ImageFrame f = getSelf().getSelectedFrame();
            	if (f != null) {
            		BufferedImage bi = f.getImage();
            		WritableRaster raster = bi.copyData( null );
            		BufferedImage copy = new BufferedImage( bi.getColorModel(), raster, bi.isAlphaPremultiplied(), null );
            		desktop.add(new ImageFrame(getSelf(), "New image", copy, null));
            	}
            }
        });
        editMenu.add(mi);
        
        // Menus
        filtersMenu.setMnemonic(KeyEvent.VK_I);
        viewMenu.setMnemonic(KeyEvent.VK_V);
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        // Set up menuBar
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		menuBar.add(filtersMenu);
		menuBar.add(new WindowMenu(desktop));
		setJMenuBar(menuBar);
		
		// =================================
		// UI
		// =================================
		setTitle("FilterFw");
		
		scrollPane.getViewport().add(desktop);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		addWindowListener(new WindowAdapter() {
		  public void windowClosing(WindowEvent e) {
			  saveAllImages();
			  System.exit(0);
		  }
		});
		
		// =========================
		// NON-UI
		// =========================
		String filtersPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile() + "/filters";
		filtersPath = filtersPath.replaceAll("%20", " ");
		filterManager = new FilterManager(this, filtersPath);
		refreshFiltersMenu();
	}
	
	public void saveAllImages() {
		  for (ImageFrame f : getImageFrames()) {
			  f.askToSaveImage();
		  }
	}
	
	public FilterFw getSelf() {
		return this;
	}
	
	public ImageFrame getSelectedFrame() {
    	JInternalFrame f = getDesktop().getSelectedFrame();
    	if (f != null && f.getClass().getName().matches(".*?ImageFrame")) {
    		ImageFrame ff = (ImageFrame) f;
    		return ff;
    	}
    	return null;
	}
	
	public ArrayList<ImageFrame> getImageFrames() {
		ArrayList<ImageFrame> result = new ArrayList<ImageFrame>();
		for (JInternalFrame f : getDesktop().getAllFrames()) {
			if (f != null && f.getClass().getName().matches(".*?ImageFrame")) {
				result.add((ImageFrame) f);
			}
		}
		return result;
	}
	
	public void refreshFilteredImages(PluginHelperImpl ph) {
		ArrayList<BufferedImage> list = ph.getUsedImages();
		for (ImageFrame imageFrame : getImageFrames()) {
			if (list.indexOf(imageFrame.getImage()) > -1) {
				imageFrame.repaint();
				imageFrame.invalidateImage();
			}
		}
	}
	
	public void refreshFiltersMenu() {
		JMenuItem mi;
		filtersMenu.removeAll();

		mi = new JMenuItem("Repeat last filter");
		mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	ImageFrame f = getSelf().getSelectedFrame();
            	if (f == null) return;
            	if (getSelf().filterManager.getLastUsed() != null) {
	        		PluginHelperImpl ph = getSelf().filterManager.process(getSelf(), f.getImage(), getSelf().filterManager.getLastUsed());
	        		getSelf().refreshFilteredImages(ph);
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
			mi.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent event) {
	            	ImageFrame f = getSelf().getSelectedFrame();
	            	if (f == null) return;
	            	PluginHelperImpl ph = getSelf().filterManager.process(getSelf(), f.getImage(), ((JMenuItem)event.getSource()).getName());
	            	getSelf().refreshFilteredImages(ph);
	            	getSelf().filtersMenu.getItem(0).setEnabled(true);
	            }
			});
			filtersMenu.add(mi);
		}
		
		mi = new JMenuItem("Reload filters");
		mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	getSelf().filterManager.loadAllFilters();
            	getSelf().refreshFiltersMenu();
            }
		});
		filtersMenu.add(mi);
	}
	
	public MDIDesktopPane getDesktop() {
		return desktop;
	}
	
	public void openImageEditor(String name, BufferedImage image, String originalPath) {
		desktop.add(new ImageFrame(this, name, image, originalPath));
	}
	
	public void openImage(String path) {
		File f = new File(path);
		BufferedImage bimg = ImageHelper.loadImage(path);
		openImageEditor(f.getName(), bimg, path);
	}
	
	public static void addFileFilters(JFileChooser fc) {
    	fc.addChoosableFileFilter(new CustomFileFilter(new String[] {"jpeg", "jpg", "png", "gif"}));
	}

	public static void main(String[] args) {
		FilterFw gui = new FilterFw();
	    gui.setSize(600, 400);
	    gui.setVisible(true);
	}

}

//Filter for open file dialog
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

/**
 * Menu component that handles the functionality expected of a standard
 * "Windows" menu for MDI applications.
 */
class WindowMenu extends JMenu {
  private MDIDesktopPane desktop;

  private JMenuItem cascade = new JMenuItem("Cascade");

  private JMenuItem tile = new JMenuItem("Tile");

  public WindowMenu(MDIDesktopPane desktop) {
    this.desktop = desktop;
    setText("Window");
    cascade.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        WindowMenu.this.desktop.cascadeFrames();
      }
    });
    tile.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        WindowMenu.this.desktop.tileFrames();
      }
    });
    addMenuListener(new MenuListener() {
      public void menuCanceled(MenuEvent e) {
      }

      public void menuDeselected(MenuEvent e) {
        removeAll();
      }

      public void menuSelected(MenuEvent e) {
        buildChildMenus();
      }
    });
  }

  /* Sets up the children menus depending on the current desktop state */
  private void buildChildMenus() {
    int i;
    ChildMenuItem menu;
    JInternalFrame[] array = desktop.getAllFrames();

    add(cascade);
    add(tile);
    if (array.length > 0)
      addSeparator();
    cascade.setEnabled(array.length > 0);
    tile.setEnabled(array.length > 0);

    for (i = 0; i < array.length; i++) {
      menu = new ChildMenuItem(array[i]);
      menu.setState(i == 0);
      menu.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          JInternalFrame frame = ((ChildMenuItem) ae.getSource()).getFrame();
          frame.moveToFront();
          try {
            frame.setSelected(true);
          } catch (PropertyVetoException e) {
            e.printStackTrace();
          }
        }
      });
      menu.setIcon(array[i].getFrameIcon());
      add(menu);
    }
  }

  /*
   * This JCheckBoxMenuItem descendant is used to track the child frame that
   * corresponds to a give menu.
   */
  class ChildMenuItem extends JCheckBoxMenuItem {
    private JInternalFrame frame;

    public ChildMenuItem(JInternalFrame frame) {
      super(frame.getTitle());
      this.frame = frame;
    }

    public JInternalFrame getFrame() {
      return frame;
    }
  }
}