package br.com.hamsterpellet.fullscreen;

import java.awt.Graphics2D;
import java.awt.Point;

import br.com.hamsterpellet.fullscreen.ScreenPage.MouseStatus;

public abstract class ScreenRegion {

	/** LINKED OBJECT **/
	
	private Object linkedObject = null;
	public final Object getLinkedObject() {
		return linkedObject;
	}
	public final void setLinkedObject(Object linkedObject) {
		this.linkedObject = linkedObject;
	}
	
	public final boolean equals(Object obj) {
		return obj instanceof ScreenRegion && id == ((ScreenRegion)obj).id;
	}
	
	/** BASE STUFF **/
	
	private static int idCounter = 0;
	private final int id;
	protected final int screenWidth;
	protected final int screenHeight;
	protected MouseStatus currentMouseStatus;
	
	protected ScreenRegion(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		id = idCounter++;
		currentMouseStatus = MouseStatus.NORMAL;
	}
	
	public final int getId() {
		return id;
	}
	public final boolean isHovered() {
		return currentMouseStatus != MouseStatus.NORMAL;
	}
	
	/** SCREEN REGION STUFF **/
	
	public final boolean contains(Point p) {
		if (p == null) throw new IllegalArgumentException("Point can't be null.");
		return contains(p.x, p.y);
	}
	
	public abstract boolean contains(int x, int y);
	
	public void paint(Graphics2D g) {}
	
	/** MOUSE INTERACTION STUFF **/
	
	private Runnable onMouseInListener = null;
	private Runnable onMouseOutListener = null;
	private Runnable onMouseUpListener = null;
	private Runnable onMouseDownListener = null;
	
	public final void setMouseInListener(Runnable r) {
		onMouseInListener = r;
	}
	public final void setMouseOutListener(Runnable r) {
		onMouseOutListener = r;
	}
	public final void setMouseUpListener(Runnable r) {
		onMouseUpListener = r;
	}
	public final void setMouseDownListener(Runnable r) {
		onMouseDownListener = r;
	}
	
	public final void registerMouseOut(GamePanel panel) {
		currentMouseStatus = MouseStatus.NORMAL;
		onMouseOut(panel);
		if (onMouseOutListener != null) onMouseOutListener.run();
	}
	public void onMouseOut(GamePanel panel) {};
	
	public final void registerMouseIn(GamePanel panel) {
		currentMouseStatus = MouseStatus.HOVER;
		onMouseIn(panel);
		if (onMouseInListener != null) onMouseInListener.run();
	}
	public void onMouseIn(GamePanel panel) {};
	
	public final void registerMouseDown() {
		currentMouseStatus = MouseStatus.PRESSED;
		onMouseDown();
		if (onMouseDownListener != null) onMouseDownListener.run();
	}
	public void onMouseDown() {};
	
	public final void registerMouseUp() {
		currentMouseStatus = MouseStatus.HOVER;
		onMouseUp();
		if (onMouseUpListener != null) onMouseUpListener.run();
	}
	public void onMouseUp() {};
	
}
