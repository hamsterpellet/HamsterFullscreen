package br.com.hamsterpellet.fullscreen.region;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;

import br.com.hamsterpellet.fullscreen.GamePanel;
import br.com.hamsterpellet.fullscreen.ScreenPage.MouseStatus;

public class ScreenBasicRect extends ScreenRect {
	
	protected ScreenBasicRect(double width, double height, ScreenRect parent) {
		/** NEVER CALL THIS, CALL FACTORY() INSTEAD **/
		
		super(width, height, parent);
		hoverCursor = defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		fillColor = fillColorHover = fillColorPress = null;
	}
	private static ScreenBasicRect factory(double width, double height, ScreenRect parent) {
		ScreenBasicRect r = new ScreenBasicRect(width, height, parent);
		r.getParent().addChild(r);
		return r;
	}
	
	public static ScreenBasicRect create(double width, double height, ScreenRect parent) {
		return factory(width, height, parent);
	}
	
	public static ScreenBasicRect create(double width, double height,
			ScreenRect parent, Color color) {
		ScreenBasicRect rect = create(width, height, parent);
		rect.fillColor = rect.fillColorHover = rect.fillColorPress = color;
		return rect;
	}
	public static ScreenBasicRect create(double width, double height,
			ScreenRect parent, Color color, Color hoverColor, Color pressColor) {
		ScreenBasicRect rect = create(width, height, parent);
		rect.fillColor = color;
		rect.fillColorHover = hoverColor;
		rect.fillColorPress = pressColor;
		return rect;
	}
	
	private Cursor hoverCursor;
	private Cursor defaultCursor;
	
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
		if (whichStatus == MouseStatus.NORMAL) {
			fillColor = color;
		} else if (whichStatus == MouseStatus.HOVER) {
			fillColorHover = color;
		} else {
			fillColorPress = color;
		}
	}
	

	@Override
	public void onMouseIn() {
		if (hoverCursor != null) {
			GamePanel.setCursor(hoverCursor);
		}
	}

	@Override
	public void onMouseOut() {
		if (defaultCursor != null) {
			GamePanel.setCursor(defaultCursor);
		}
	}

	@Override
	public void paint(Graphics2D g) {
		super.paint(g);
		
		Color paintColor = getCurrentFillColor();
		
		if (paintColor != null) {
			Color oldColor = g.getColor();
			g.setColor(paintColor);
			g.fillRect(getUpperXOnScreen(), getUpperYOnScreen(), getWidthOnScreen(), getHeightOnScreen());
			g.setColor(oldColor);
		}
	}
	
}
