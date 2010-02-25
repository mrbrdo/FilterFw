import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class ImageFrame extends JInternalFrame implements MouseListener {
	private JScrollPane scrollPane = new JScrollPane();
	private ImagePanel imagePanel;
	private String imagePath;

	public ImageFrame(String name, BufferedImage image, String imagePath) {
		this.imagePath = imagePath;
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
			ImageHelper.saveImage(getImage(), filename);
			File f = new File(filename);
			setTitle(f.getName());
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
}