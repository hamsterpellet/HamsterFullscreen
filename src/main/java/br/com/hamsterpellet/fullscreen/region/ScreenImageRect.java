package br.com.hamsterpellet.fullscreen.region;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import br.com.hamsterpellet.fullscreen.GamePanel;
import br.com.hamsterpellet.fullscreen.ScreenPage.MouseStatus;

public class ScreenImageRect extends ScreenColoredCursoredRect {
	
	/*********************************************/
	
	protected ScreenImageRect(double width, double height, ScreenRect parent) {
		/** NEVER CALL THIS, CALL FACTORY() INSTEAD **/
		
		super(width, height, parent);
	}
	private static ScreenImageRect factory(double width, double height, ScreenRect parent) {
		ScreenImageRect r = new ScreenImageRect(width, height, parent);
		r.getParent().addChild(r);
		return r;
	}

	public static ScreenImageRect create(double width, double height, ScreenRect parent,
			BufferedImage image, BufferedImage imageHover, BufferedImage imagePress) {
		ScreenImageRect rect = factory(width, height, parent);
		rect.image = image;
		rect.imageHover = imageHover;
		rect.imagePress = imagePress;
		return rect;
	}
	
	public static ScreenImageRect create(double width, double height, ScreenRect parent,
			String imageURL, String imageHoverURL,
			String imagePressURL) throws MalformedURLException, IOException {
		BufferedImage image = getImageFromURL(imageURL);
		BufferedImage imageHover = getImageFromURL(imageHoverURL);
		BufferedImage imagePress = getImageFromURL(imagePressURL);
		return create(width, height, parent, image, imageHover, imagePress);
	}
	
	public static ScreenImageRect create(ScreenRect parent,
			String imageURL, String imageHoverURL,
			String imagePressURL) throws MalformedURLException, IOException {
		BufferedImage image = getImageFromURL(imageURL);
		BufferedImage imageHover = getImageFromURL(imageHoverURL);
		BufferedImage imagePress = getImageFromURL(imagePressURL);
		
		double width = (double) image.getWidth() / GamePanel.getScreenWidth();
		double height = (double) image.getHeight() / GamePanel.getScreenHeight();
		
		return create(width, height, parent, image, imageHover, imagePress);
	}
	
	public static ScreenImageRect create(ScreenRect parent,	String imageURL)
			throws MalformedURLException, IOException {
		BufferedImage image = getImageFromURL(imageURL);
		
		double width = (double) image.getWidth() / GamePanel.getScreenWidth();
		double height = (double) image.getHeight() / GamePanel.getScreenHeight();
		
		return create(width, height, parent, image, image, image);
	}
	
	public static ScreenImageRect create(ScreenRect parent,	String imageURL,
			boolean autoSuffix) throws MalformedURLException, IOException {
		if (!autoSuffix) return create(parent, imageURL);
		BufferedImage image = getImageFromURL(imageURL);
		BufferedImage imageHover;
		BufferedImage imagePress;
		try {
			imageHover = getImageFromURL(addSuffix(imageURL, hoverSuffix));
		} catch (Exception e) {
			imageHover = image;
		}
		try {
			imagePress = getImageFromURL(addSuffix(imageURL, pressSuffix));
		} catch (Exception e) {
			imagePress = imageHover;
		}
		
		double width = (double) image.getWidth() / GamePanel.getScreenWidth();
		double height = (double) image.getHeight() / GamePanel.getScreenHeight();
		
		return create(width, height, parent, image, imageHover, imagePress);
	}
	
	/*********************************************/
	
	public static final String baseURL = "file:///E:/Programming/Java/Repositories/BoxReflex";
	
	public static final String hoverSuffix = "_hover";
	public static final String pressSuffix = "_press";
	
	public static final String addSuffix(String string, String suffix) {
		int dotPos = string.lastIndexOf(".");
		if (dotPos == -1) return string + suffix;
		String firstPart = string.substring(0, dotPos);
		String lastPart = string.substring(dotPos);
		return firstPart + suffix + lastPart;
	}
	
	private static BufferedImage getImageFromURL(String filename) throws MalformedURLException, IOException {
		// return ImageIO.read(new URL(baseURL + "/res/" + filename));
		// String path = (new File("res/" + "omg.jpg")).toURI().toString().replace("file:/", "file:///");
		return ImageIO.read((new File(filename)).toURI().toURL());
	}
	
	/*********************************************/
	
	private BufferedImage image;
	private BufferedImage imageHover;
	private BufferedImage imagePress;
	
	private BufferedImage getCurrentImage() {
		if (currentMouseStatus == MouseStatus.NORMAL) {
			return image;
		}
		if (currentMouseStatus == MouseStatus.HOVER) {
			return imageHover;
		}
		return imagePress;		
	}

	public void setImage(BufferedImage image) {
		setImage(image, MouseStatus.NORMAL);
	}
	public void setImage(BufferedImage image, MouseStatus status) {
		if (status == MouseStatus.NORMAL) {
			this.image = image;
		} else if (status == MouseStatus.HOVER) {
			this.imageHover = image;
		} else {
			this.imagePress = image;
		}
	}
	public void setImage(String imageURL) throws MalformedURLException, IOException {
		setImage(getImageFromURL(imageURL), MouseStatus.NORMAL);
	}
	public void setImage(String imageURL, MouseStatus status) throws MalformedURLException, IOException {
		setImage(getImageFromURL(imageURL), status);
	}
	
	/*********************************************/
	
	@Override
	public void paint(Graphics2D g) {
		super.paint(g);
		
		BufferedImage currentImage = getCurrentImage();
		g.drawImage(currentImage, getUpperXOnScreen(), getUpperYOnScreen(), getLowerXOnScreen(), getLowerYOnScreen(), 0, 0, currentImage.getWidth(), currentImage.getHeight(), null);
	}
	
}
