package br.com.hamsterpellet.fullscreen;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;

import br.com.hamsterpellet.fullscreen.ScreenPage.MouseStatus;

public class ScreenColoredCursoredRect extends ScreenRect {

	public static final Color DEFAULT_FILL_COLOR = Color.PINK;
	
	protected ScreenColoredCursoredRect(int screenWidth, int screenHeight, int width, int height, ScreenRect parent) {
		super(screenWidth, screenHeight, width, height, parent);
		hoverCursor = defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		fillColor = fillColorHover = fillColorPress = DEFAULT_FILL_COLOR;
		isFillColorHoverSet = isFillColorPressSet = false;
	}
	
	public static ScreenColoredCursoredRect create(int screenWidth, int screenHeight, int width, int height, ScreenRect parent) {
		return new ScreenColoredCursoredRect(screenWidth, screenHeight, width, height, parent);
	}
	
	public static ScreenColoredCursoredRect create(int screenWidth, int screenHeight, int width, int height, ScreenRect parent, Color color) {
		ScreenColoredCursoredRect rect = create(screenWidth, screenHeight, width, height, parent);
		rect.fillColor = rect.fillColorHover = rect.fillColorPress = color;
		return rect;
	}
	
	private Cursor hoverCursor;
	private Cursor defaultCursor;
	
	private boolean isFillColorHoverSet;
	private boolean isFillColorPressSet;
	
	private Color fillColor;
	private Color fillColorHover;
	private Color fillColorPress;
	
	private Color getCurrentFillColor() {
		if (currentMouseStatus == MouseStatus.NORMAL) {
			return fillColor;
		}
		if (currentMouseStatus == MouseStatus.HOVER) {
			return fillColorHover;
		}
		return fillColorPress;
	}
	
	/** SETTERS **/
	
	public void setHoverCursor(int whichCursor) {
		setHoverCursor(Cursor.getPredefinedCursor(whichCursor));
	}
	public void setHoverCursor(Cursor whichCursor) {
		hoverCursor = whichCursor;
	}
	public void setDefaultCursor(int whichCursor) {
		setDefaultCursor(Cursor.getPredefinedCursor(whichCursor));
	}
	public void setDefaultCursor(Cursor whichCursor) {
		defaultCursor = whichCursor;
	}

	public void setFillColor(Color color) {
		setFillColor(color, MouseStatus.NORMAL);
	}
	public void setFillColor(Color color, MouseStatus whichStatus) {
		if (color == null) throw new IllegalArgumentException("Color can't be null!");
		if (whichStatus == MouseStatus.NORMAL) {
			fillColor = color;
			if (!isFillColorHoverSet) setFillColor(color, MouseStatus.HOVER);
			if (!isFillColorPressSet) setFillColor(color, MouseStatus.PRESSED); // this won't ever run but ..readability..!
		} else if (whichStatus == MouseStatus.HOVER) {
			fillColorHover = color;
			isFillColorHoverSet = true;
			if (!isFillColorPressSet) setFillColor(color, MouseStatus.PRESSED);
		} else {
			fillColorPress = color;
			isFillColorPressSet = true;
		}
	}
	

	@Override
	public void onMouseIn(GamePanel panel) {
		if (panel != null && hoverCursor != null) {
			panel.setCursor(hoverCursor);
		}
	}

	@Override
	public void onMouseOut(GamePanel panel) {
		if (panel != null && defaultCursor != null) {
			panel.setCursor(defaultCursor);
		}
	}

	@Override
	public void paint(Graphics2D g) {
		Color oldColor = g.getColor();
		g.setColor(getCurrentFillColor());
		g.fillRect(relativeUpperX, relativeUpperY, width, height);
		g.setColor(oldColor);
	}
	

}
