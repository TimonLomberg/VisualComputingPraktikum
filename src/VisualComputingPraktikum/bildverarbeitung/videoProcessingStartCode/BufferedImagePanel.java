package VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * A BufferdImagePanel is a Swing component that can display a BufferdImage.
 * It is constructed as a subclass of JComponent with the added functionality
 * of setting a BufferdImage that will be displayed on the surface of this
 * component.
 * 
 * @author Michael K�lling and David J. Barnes.
 * @version 1.0
 * 
 * Modified by Karsten Lehn, 31.8.2016 from "ImgagePanel" 
 * 
 */
public class BufferedImagePanel extends JComponent {

	// The current width and height of this panel
	private int width, height;

	// An internal image buffer that is used for painting. For
	// actual display, this image buffer is then copied to screen.
	private BufferedImage panelImage;

	/**
	 * Create a new, empty ImagePanel.
	 */
	public BufferedImagePanel()
	{
		width = 640;    // arbitrary size for empty panel
	    height = 480;
	    panelImage = null;
	}

	/**
	 * Set the image that this panel should show.
	 * 
	 * @param image  The image to be displayed.
	 */
	public void setImage(BufferedImage image)
	{
		if(image != null) {
			width = image.getWidth();
			height = image.getHeight();
	        panelImage = image;
	        repaint();
	        }
	 }
	    
	/**
	 * Clear the image on this panel.
	 */
	public void clearImage()
	{
		Graphics imageGraphics = panelImage.getGraphics();
		imageGraphics.setColor(Color.LIGHT_GRAY);
		imageGraphics.fillRect(0, 0, width, height);
		repaint();
	}
	    
	    
	/**
	 * Tell the layout manager how big we would like to be.
	 * (This method gets called by layout managers for placing
	 * the components.)
	 * 
	 * @return The preferred dimension for this component.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}	    	
	
	/**
	 * This component needs to be redisplayed. Copy the internal image 
	 * to screen. (This method gets called by the Swing screen painter 
	 * every time it want this component displayed.)
	 * 
	 * @param g The graphics context that can be used to draw on this component.
	 */
	@Override
	public void paintComponent(Graphics g)
	    {
	        Dimension size = getSize();
	        g.clearRect(0, 0, size.width, size.height);
	        if(panelImage != null) {
	            g.drawImage(panelImage, 0, 0, null);
	        }
	    }	
}
