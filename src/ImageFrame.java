import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class ImageFrame extends JInternalFrame implements MouseListener, InternalFrameListener {
	private JScrollPane scrollPane = new JScrollPane();
	private ImagePanel imagePanel;
	private String imagePath;
	public String getImagePath() {
		return imagePath;
	}

	private boolean invalidated;
	private FilterFw frame;

	public ImageFrame(FilterFw frame, String name, BufferedImage image, String imagePath) {
		this.imagePath = imagePath;
		this.frame = frame;
		setSize(200, 300);
		setTitle(name);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setResizable(true);
		imagePanel = new ImagePanel(image);
		scrollPane.getViewport().add(imagePanel);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		Component[] cc = getComponents();
        for (int i=0; i<cc.length ; i++) {
            cc[i].addMouseListener(this);
        }
        imagePanel.addMouseListener(this);
        scrollPane.addMouseListener(this);
        invalidated = (imagePath == null || imagePath == "" || image == null);
        
		addInternalFrameListener(this);
	}
	
	public void invalidateImage() {
		invalidated = true;
	}
	
	public void setImage(BufferedImage image) {
		imagePanel.setImage(image);
	}
	
	public BufferedImage getImage() {
		return imagePanel.getImage();
	}
	
	public void zoom(float value, boolean relative) {
		imagePanel.zoom(value, relative);
	}
	
	public void reloadImage() {
		if (imagePath != null && imagePath != "") {
			imagePanel.setImage(ImageHelper.loadImage(imagePath));
		} else {
			JOptionPane.showMessageDialog(this, "This image does not exist on disk and thus cannot be reopened.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void saveImage(String filename) {
		if (filename != null && filename != "") {
			String newFilename = ImageHelper.saveImage(getImage(), filename);
			File f = new File(newFilename);
			setTitle(f.getName());
			imagePath = newFilename;
			invalidated = false;
		}
	}
	
	public void saveImageAs() {
    	JFileChooser fc = new JFileChooser();
    	if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
       		saveImage(fc.getSelectedFile().getAbsolutePath());
    	}
	}
	
	public void trySaveImage() {
		if (imagePath != null && imagePath != "") {
			saveImage(imagePath);
		} else {
			saveImageAs();
		}
	}
	
	public void askToSaveImage() {
		if (invalidated) {
			Object[] options = {"Save",
                    "Don't save"};
			int n = JOptionPane.showOptionDialog(frame,
			    "Would you like to save \"" + getTitle() + "\"?",
			    "Save Image",
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,
			    options,
			    options[0]);
			if (n == 0) {
				trySaveImage();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent evt) {
		// TODO Auto-generated method stub
        moveToFront();
        try {
          setSelected(true);
        } catch (PropertyVetoException e) {
          e.printStackTrace();
        }
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		askToSaveImage();
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}