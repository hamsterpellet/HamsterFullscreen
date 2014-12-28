package br.com.hamsterpellet.fullscreen;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

public class ScreenStringRect extends ScreenColoredCursoredRect {

	protected ScreenStringRect(int screenWidth, int screenHeight, int width, int height, String text, Point base, Font f) {
		super(screenWidth, screenHeight, width, height);
		this.text = text;
		baselineStartPoint = base;
		font = f;
		fontColor = Color.WHITE;
	}
	
	private String text;
	private Point baselineStartPoint;
	private final Font font;
	
	private Color fontColor;
	private Color fontColorHover;
	private Color fontColorPress;
	
	private Color getCurrentFontColor() {
		if (currentMouseStatus == MouseStatus.NORMAL) {
			return fontColor;
		}
		if (currentMouseStatus == MouseStatus.HOVER) {
			return fontColorHover;
		}
		return fontColorPress;		
	}
	
	public Point getBaselineStartPoint() {
		return baselineStartPoint;
	}
	
	public static Point getBaselineStartPoint(FontMetrics metrics, String text, Point center) {
		return new Point(center.x - metrics.stringWidth(text) / 2, center.y + metrics.getHeight() / 2 - metrics.getDescent());
	}
	
	public static Point getBaselineStartPoint(FontMetrics metrics, String text) {
		return new Point(0, metrics.getHeight() - metrics.getDescent());
	}
	
	public static ScreenStringRect create(int screenWidth, int screenHeight, Graphics2D g, String text, Font font) {
		FontMetrics metrics = g.getFontMetrics(font);
		Point base = getBaselineStartPoint(metrics, text);
		return new ScreenStringRect(screenWidth, screenHeight, metrics.stringWidth(text), metrics.getHeight(), text, base, font);
	}
	

	public void setColor(Color color) {
		setFillColor(color, MouseStatus.NORMAL);
	}
	public void setColor(Color color, MouseStatus whichStatus) {
		if (whichStatus == MouseStatus.NORMAL) {
			fontColor = fontColorHover = fontColorPress = color;
		} else if (whichStatus == MouseStatus.HOVER) {
			fontColorHover = fontColorPress = color;
		} else {
			fontColorPress = color;		
		}
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
		Color oldColor = g.getColor();
		Font oldFont = g.getFont();
		g.setColor(getCurrentFontColor());
		g.setFont(font);
		g.drawString(text, upperX + baselineStartPoint.x, upperY + baselineStartPoint.y);
		g.setColor(oldColor);
		g.setFont(oldFont);
	}
	

}
