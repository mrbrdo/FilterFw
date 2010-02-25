import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class ImagePanel extends JPanel implements MouseWheelListener {
	private BufferedImage image;
	private float scaleFactor = 1.0f;

	public ImagePanel(BufferedImage image) {  
        super();
        addMouseWheelListener(this);
        setImage(image);
    }
    
    public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
    	if (image == null) return;
    	//int prefWidth = image.getWidth();
    	//if (3*256 > prefWidth) prefWidth = 3*256;
    	//setPreferredSize(new Dimension(prefWidth, image.getHeight()));
    	//this.repaint();
    	zoom(1.0f, false);
	}

	public void zoom(float value, boolean relative) {
		if (!relative)
			scaleFactor = value;
		else
			scaleFactor += value;
    	this.setPreferredSize(new Dimension((int) (image.getWidth() * scaleFactor), (int) (image.getHeight() * scaleFactor)));
    	this.revalidate();
		this.repaint();
	}
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(new Color(100, 100, 100, 255));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
    	if (image != null) {
    		g.drawImage(image.getScaledInstance((int) (image.getWidth() * scaleFactor), (int) (image.getHeight() * scaleFactor), Image.SCALE_FAST), 0, 0, null);
    	}
    }

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoom(e.getWheelRotation() * -0.1f, true);
	}
}