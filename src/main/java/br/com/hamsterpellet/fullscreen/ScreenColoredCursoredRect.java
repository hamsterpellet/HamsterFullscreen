package br.com.hamsterpellet.fullscreen;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;

public class ScreenColoredCursoredRect extends ScreenRect {

	public static final Color DEFAULT_FILL_COLOR = Color.PINK;
	
	protected ScreenColoredCursoredRect(int screenWidth, int screenHeight, int width, int height) {
		super(screenWidth, screenHeight, width, height);
		hoverCursor = defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		fillColor = fillColorHover = fillColorPress = DEFAULT_FILL_COLOR;
		isFillColorHoverSet = isFillColorPressSet = false;
	}
	
	public static ScreenColoredCursoredRect create(int screenWidth, int screenHeight, int width, int height) {
		return new ScreenColoredCursoredRect(screenWidth, screenHeight, width, height);
	}
	
	public static ScreenColoredCursoredRect create(int screenWidth, int screenHeight, int width, int height, Color color) {
		ScreenColoredCursoredRect rect = create(screenWidth, screenHeight, width, height);
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
		if (whichStatus == MouseStatus.NORMAL) {
			fillColor = color;
			if (!isFillColorHoverSet) setFillColor(color, MouseStatus.HOVER);
			if (!isFillColorPressSet) setFillColor(color, MouseStatus.PRESSED);
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
	public void registerHoverIn(GamePanel p) {
		if (p != null && hoverCursor != null) {
			p.setCursor(hoverCursor);
		}
	}

	@Override
	public void registerHoverOut(GamePanel p) {
		if (p != null && defaultCursor != null) {
			p.setCursor(defaultCursor);
		}
	}

	@Override
	public void registerClick() {}

	@Override
	public void registerPressCosmetics() {}

	@Override
	public void paint(Graphics2D g) {
		if (getCurrentFillColor() == null) return;
		Color oldColor = g.getColor();
		g.setColor(getCurrentFillColor());
		g.fillRect(upperX, upperY, width, height);
		g.setColor(oldColor);
	}
	

}
