package br.com.hamsterpellet.fullscreen.region;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

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
	
	/** FAMILY STUFF **/
	
	private final ScreenRegion parent;
	protected final ArrayList<ScreenRegion> children;
	
	private static boolean rootAlreadyCreated;
	private static final ScreenRect root;
	static {
		rootAlreadyCreated = false;
		root = new ScreenRect(1, 1, null);
		root.setPosition(0, 0);
		rootAlreadyCreated = true;
	}
	public boolean isRoot() {
		return this == root;
	}
	
	public final void addChild(ScreenRegion child) {
		children.add(child);
	}
	public final ArrayList<ScreenRegion> getChildren() {
		return children;
	}
	public final ScreenRegion getParent() {
		if (this == root) throw new RuntimeException("Do NOT ask for the root's parent!");
		return parent;
	}
	
	/** BASE STUFF **/
	
	private static int idCounter = 0;
	public final int id;
	protected MouseStatus currentMouseStatus;
	
	protected ScreenRegion(ScreenRegion parent) {
		id = idCounter++;
		currentMouseStatus = MouseStatus.NORMAL;
		
		if (rootAlreadyCreated && parent == null) {
			this.parent = root;
		} else {
			this.parent = parent;
		}
		this.children = new ArrayList<ScreenRegion>();
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
	
	public void paint(Graphics2D g) {
		for (ScreenRegion child : children) {
			child.paint(g);
		}
	}
	
	/** MOUSE INTERACTION STUFF **/
	
	private Runnable onMouseInListener = null;
	private Runnable onMouseOutListener = null;
	private Runnable onMouseUpListener = null;
	private Runnable onMouseDownListener = null;
	private Runnable onMouseClickListener = null;
	
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
	public final void setMouseClickListener(Runnable r) {
		onMouseClickListener = r;
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
		boolean isThisAClick = currentMouseStatus == MouseStatus.PRESSED;
		currentMouseStatus = MouseStatus.HOVER;
		onMouseUp();
		if (onMouseUpListener != null) onMouseUpListener.run();
		
		if (isThisAClick) {
			onMouseClick();
			if (onMouseClickListener != null) onMouseClickListener.run();
		}
	}
	public void onMouseUp() {};
	public void onMouseClick() {};
	
}
