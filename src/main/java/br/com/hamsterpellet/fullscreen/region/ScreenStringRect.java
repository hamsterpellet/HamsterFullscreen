package br.com.hamsterpellet.fullscreen.region;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import br.com.hamsterpellet.fullscreen.GamePanel;
import br.com.hamsterpellet.fullscreen.ScreenPage.MouseStatus;

public class ScreenStringRect extends ScreenColoredCursoredRect {
	
	private String text;
	private final Font font;
	private final FontMetrics metrics;
	
	private Color fontColor;
	private Color fontColorHover;
	private Color fontColorPress;

	protected ScreenStringRect(double width, double height, String text, Font font, FontMetrics metrics, ScreenRect parent) {
		/** NEVER CALL THIS, CALL FACTORY() INSTEAD **/
		
		super(width, height, parent);
		this.text = text;
		this.font = font;
		this.metrics = metrics;
		this.fontColor = Color.WHITE;
	}
	private static ScreenStringRect factory(double width, double height, String text, Font font, FontMetrics metrics, ScreenRect parent) {
		ScreenStringRect r = new ScreenStringRect(width, height, text, font, metrics, parent);
		r.getParent().addChild(r);
		return r;
	}
	
	public static ScreenStringRect create(double width, double height, String text,
			Font font, FontMetrics metrics, ScreenRect parent) {
		return factory(width, height, text, font, metrics, parent);
	}
	
	public static ScreenStringRect create(double width, double height, String text,
			Font font, Graphics2D g, ScreenRect parent) {
		FontMetrics metrics = g.getFontMetrics(font);
		return create(width, height, text, font, metrics, parent);
	}
	
	public static ScreenStringRect create(String text, Font font, Graphics2D g, ScreenRect parent) {
		FontMetrics metrics = g.getFontMetrics(font);

		double width = (double) metrics.stringWidth(text) / GamePanel.getScreenWidth();
		double height = (double) metrics.getHeight() / GamePanel.getScreenHeight();
		
		return create(width, height, text, font, metrics, parent);
	}
	
	/** COLOR **/
	
	private Color getCurrentFontColor() {
		if (currentMouseStatus == MouseStatus.NORMAL) {
			return fontColor;
		}
		if (currentMouseStatus == MouseStatus.HOVER) {
			return fontColorHover;
		}
		return fontColorPress;		
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
	
	/*********************************************/
	
	/*
	public static Point getBaselineStartPoint(FontMetrics metrics, String text, Point center) {
		return new Point(center.x - metrics.stringWidth(text) / 2, center.y + metrics.getHeight() / 2 - metrics.getDescent());
	}
	*/
	
	public static Point getBaselineStartPoint(FontMetrics metrics, String text) {
		return new Point(0, metrics.getHeight() - metrics.getDescent());
	}

	@Override
	public void paint(Graphics2D g) {
		super.paint(g);
		
		Color oldColor = g.getColor();
		Font oldFont = g.getFont();
		g.setColor(getCurrentFontColor());
		g.setFont(font);
		
		Point base = getBaselineStartPoint(metrics, text);
		g.drawString(text, getUpperXOnScreen() + base.x, getUpperYOnScreen() + base.y);
		
		g.setColor(oldColor);
		g.setFont(oldFont);
	}
	

}
