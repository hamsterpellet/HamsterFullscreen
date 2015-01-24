package br.com.hamsterpellet.fullscreen;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import br.com.hamsterpellet.fullscreen.ScreenPage.MouseStatus;

public class ScreenStringRect extends ScreenColoredCursoredRect {

	protected ScreenStringRect(int screenWidth, int screenHeight, int width, int height, ScreenRect parent, String text, Point base, Font f) {
		super(screenWidth, screenHeight, width, height, parent);
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
	
	public static ScreenStringRect create(int screenWidth, int screenHeight, ScreenRect parent, Graphics2D g, String text, Font font) {
		FontMetrics metrics = g.getFontMetrics(font);
		Point base = getBaselineStartPoint(metrics, text);
		return new ScreenStringRect(screenWidth, screenHeight, metrics.stringWidth(text), metrics.getHeight(), parent, text, base, font);
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
	public void paint(Graphics2D g) {
		super.paint(g);
		Color oldColor = g.getColor();
		Font oldFont = g.getFont();
		g.setColor(getCurrentFontColor());
		g.setFont(font);
		g.drawString(text, relativeUpperX + baselineStartPoint.x, relativeUpperY + baselineStartPoint.y);
		g.setColor(oldColor);
		g.setFont(oldFont);
	}
	

}
