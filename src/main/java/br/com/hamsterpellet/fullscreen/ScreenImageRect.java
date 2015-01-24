package br.com.hamsterpellet.fullscreen;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import br.com.hamsterpellet.fullscreen.ScreenPage.MouseStatus;

public class ScreenImageRect extends ScreenColoredCursoredRect {
	
	public static final String baseURL = "file:///E:/Programming/Java/EclipseADTWorkspace/Saturamini";
	
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
		return ImageIO.read(new URL(baseURL + "/res/" + filename));
	}
	
	protected ScreenImageRect(int screenWidth, int screenHeight, int width, int height, ScreenRect parent) {
		super(screenWidth, screenHeight, width, height, parent);
	}

	public static ScreenImageRect create(int screenWidth, int screenHeight, int width, int height,
			ScreenRect parent, BufferedImage image,
			BufferedImage imageHover, BufferedImage imagePress) {
		ScreenImageRect rect = new ScreenImageRect(screenWidth, screenHeight, width, height, parent);
		rect.image = image;
		rect.imageHover = imageHover;
		rect.imagePress = imagePress;
		return rect;
	}
	
	public static ScreenImageRect create(int screenWidth, int screenHeight, int width, int height,
			ScreenRect parent, String imageURL, String imageHoverURL,
			String imagePressURL) throws MalformedURLException, IOException {
		BufferedImage image = getImageFromURL(imageURL);
		BufferedImage imageHover = getImageFromURL(imageHoverURL);
		BufferedImage imagePress = getImageFromURL(imagePressURL);
		return create(screenWidth, screenHeight, width, height, parent, image, imageHover, imagePress);
	}
	
	public static ScreenImageRect create(int screenWidth, int screenHeight, ScreenRect parent,
			String imageURL, String imageHoverURL,
			String imagePressURL) throws MalformedURLException, IOException {
		BufferedImage image = getImageFromURL(imageURL);
		BufferedImage imageHover = getImageFromURL(imageHoverURL);
		BufferedImage imagePress = getImageFromURL(imagePressURL);
		return create(screenWidth, screenHeight, image.getWidth(), image.getHeight(), parent, image, imageHover, imagePress);
	}
	
	public static ScreenImageRect create(int screenWidth, int screenHeight, ScreenRect parent,
			String imageURL) throws MalformedURLException, IOException {
		BufferedImage image = getImageFromURL(imageURL);
		return create(screenWidth, screenHeight, image.getWidth(), image.getHeight(), parent, image, image, image);
	}
	
	public static ScreenImageRect create(int screenWidth, int screenHeight, ScreenRect parent,
			String imageURL, boolean autoSuffix) throws MalformedURLException, IOException {
		if (!autoSuffix) return create(screenWidth, screenHeight, parent, imageURL);
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
			imagePress = image;
		}
		return create(screenWidth, screenHeight, image.getWidth(), image.getHeight(), parent, image, imageHover, imagePress);
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

	public void setImageHover(BufferedImage imageHover) {
		this.imageHover = imageHover;
	}
	public void setImageHover(String imageHoverURL) throws MalformedURLException, IOException {
		setImageHover(getImageFromURL(imageHoverURL));
	}

	public void setImagePress(BufferedImage imagePress) {
		this.imagePress = imagePress;
	}
	public void setImagePress(String imagePressURL) throws MalformedURLException, IOException {
		setImagePress(getImageFromURL(imagePressURL));
	}
	
	@Override
	public void paint(Graphics2D g) {
		super.paint(g);
		BufferedImage currentImage = getCurrentImage();
		g.drawImage(currentImage, this.relativeUpperX, this.relativeUpperY, this.relativeUpperX + width, this.relativeUpperY + height, 0, 0, currentImage.getWidth(), currentImage.getHeight(), null);
	}
	
}
