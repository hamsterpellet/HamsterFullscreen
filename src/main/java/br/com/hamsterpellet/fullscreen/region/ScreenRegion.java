package br.com.hamsterpellet.fullscreen.region;

import java.awt.Graphics2D;
import java.awt.Point;

import br.com.hamsterpellet.fullscreen.GamePanel;
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
	public final int id;
	protected MouseStatus currentMouseStatus;
	
	protected ScreenRegion() {
		id = idCounter++;
		currentMouseStatus = MouseStatus.NORMAL;
	}
	
	public final boolean isHovered() {
		return currentMouseStatus != MouseStatus.NORMAL;
	}
	
	/** SCREEN REGION STUFF **/
	
	public final boolean containsOnScreen(int x, int y) {
		return contains((double) x / GamePanel.getScreenWidth(), (double) y / GamePanel.getScreenHeight());
	}
	public final boolean containsOnScreen(Point p) {
		return containsOnScreen(p.x, p.y);
	}
	
	public abstract boolean contains(double x, double y);
	
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
	
	public final void registerMouseOut() {
		currentMouseStatus = MouseStatus.NORMAL;
		onMouseOut();
		if (onMouseOutListener != null) onMouseOutListener.run();
	}
	public void onMouseOut() {};
	
	public final void registerMouseIn() {
		currentMouseStatus = MouseStatus.HOVER;
		onMouseIn();
		if (onMouseInListener != null) onMouseInListener.run();
	}
	public void onMouseIn() {};
	
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
