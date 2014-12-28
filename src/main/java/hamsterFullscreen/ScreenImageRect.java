package hamsterFullscreen;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ScreenImageRect extends ScreenColoredCursoredRect {
	
	public static final String baseURL = "file:///E:/Programming/Java/EclipseADTWorkspace/Saturamini";
	
	protected ScreenImageRect(int screenWidth, int screenHeight, int width, int height) {
		super(screenWidth, screenHeight, width, height);
	}

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
	
	private static BufferedImage getImageFromURL(String filename) throws MalformedURLException, IOException {
		return ImageIO.read(new URL(baseURL + "/res/" + filename));
	}

	public static ScreenImageRect create(int screenWidth, int screenHeight, int width, int height,
			BufferedImage image, BufferedImage imageHover, BufferedImage imagePress) {
		ScreenImageRect rect = new ScreenImageRect(screenWidth, screenHeight, width, height);
		rect.image = image;
		rect.imageHover = imageHover;
		rect.imagePress = imagePress;
		return rect;
	}
	
	public static ScreenImageRect create(int screenWidth, int screenHeight, int width, int height,
			String imageURL, String imageHoverURL, String imagePressURL) throws MalformedURLException, IOException {
		BufferedImage image = getImageFromURL(imageURL);
		BufferedImage imageHover = getImageFromURL(imageHoverURL);
		BufferedImage imagePress = getImageFromURL(imagePressURL);
		return create(screenWidth, screenHeight, width, height, image, imageHover, imagePress);
	}
	
	public static ScreenImageRect create(int screenWidth, int screenHeight,	String imageURL) throws MalformedURLException, IOException {
		BufferedImage image = getImageFromURL(imageURL);
		return create(screenWidth, screenHeight, image.getWidth(), image.getHeight(), image, image, image);
	}

	public void setImage(BufferedImage image) {
		setImage(image, MouseStatus.NORMAL);
	}
	public void setImage(BufferedImage image, MouseStatus status) {
		if (status == MouseStatus.NORMAL) {
			setImage(image);
			setImageHover(image);
			setImagePress(image);
		} else if (status == MouseStatus.HOVER) {
			setImageHover(image);
			setImagePress(image);
		} else {
			setImagePress(image);
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
	public void registerHoverIn(GamePanel p) {
		super.registerHoverIn(p);
	}

	@Override
	public void registerHoverOut(GamePanel p) {
		super.registerHoverOut(p);
	}

	@Override
	public void registerClick() {
		super.registerClick();
	}

	@Override
	public void registerPressCosmetics() {
		super.registerPressCosmetics();
	}

	@Override
	public void paint(Graphics2D g) {
		super.paint(g);
		BufferedImage currentImage = getCurrentImage();
		g.drawImage(currentImage, this.upperX, this.upperY, this.upperX + width, this.upperY + height, 0, 0, currentImage.getWidth(), currentImage.getHeight(), null);
	}
	
}
