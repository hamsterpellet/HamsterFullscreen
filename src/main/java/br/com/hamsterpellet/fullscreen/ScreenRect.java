package br.com.hamsterpellet.fullscreen;


import java.awt.Graphics2D;
import java.awt.Point;


public abstract class ScreenRect {
	
	public static interface RectListener {
		public void run(ScreenRect rect);
	}
	
	private RectListener hoverInListener;
	private RectListener hoverOutListener;
	private RectListener clickListener;
	
	public final void setHoverInListener(RectListener hoverInListener) {
		this.hoverInListener = hoverInListener;
	}
	public final void setHoverOutListener(RectListener hoverOutListener) {
		this.hoverOutListener = hoverOutListener;
	}
	public final void setClickListener(RectListener clickListener) {
		this.clickListener = clickListener;
	}
	
	public final void _registerHoverIn(GamePanel p) {
		if (!isLocked) throw new RectLockException();
		currentMouseStatus = MouseStatus.HOVER;
		registerHoverIn(p);
		if (hoverInListener != null) hoverInListener.run(this);
	}
	protected abstract void registerHoverIn(GamePanel p);
	
	public final void _registerHoverOut(GamePanel p) {
		if (!isLocked) throw new RectLockException();
		currentMouseStatus = MouseStatus.NORMAL;
		
		registerHoverOut(p);
		if (hoverOutListener != null) hoverOutListener.run(this);
	}
	protected abstract void registerHoverOut(GamePanel p);
	
	public final void _registerClick() {
		if (!isLocked) throw new RectLockException();
		currentMouseStatus = MouseStatus.HOVER;
		registerClick();
		if (clickListener != null) clickListener.run(this);
	}
	protected abstract void registerClick();
	
	public final void _registerPressCosmetics() {
		if (!isLocked) throw new RectLockException();
		currentMouseStatus = MouseStatus.PRESSED;
		registerPressCosmetics();
	}
	protected abstract void registerPressCosmetics();
	
	
	
	
	/***********************/

	@SuppressWarnings("serial")
	public static final class OutOfScreenException extends RuntimeException {}
	@SuppressWarnings("serial")
	public static final class RectLockException extends RuntimeException {}
	// nao muda a ordem senao buga o setPosition() ahsuashuhas
	public static enum BasePos { 
		BEGIN(0, 0), CENTER(1, 0), END(2, 0),
		CENTER_LEFT(1, -0.5), CENTER_RIGHT(1, 0.5), CENTER_UP(1, -0.5), CENTER_DOWN(1, 0.5);
		// LEFT & UP are the same
		// RIGHT & DOWN are the same too
		// this is just for readability
		
		public final int multiplier;
		public final double centerShift;
		
		private BasePos(int multiplier, double centerShift) {
			this.multiplier = multiplier;
			this.centerShift = centerShift;
		}
	}
	public static enum RelativePos {
		LEFT, RIGHT, ABOVE, BELOW;
	}
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT; 
	}
	
	public static enum MouseStatus {NORMAL, HOVER, PRESSED};
	
	/***********************/
	
	private Object linkedObject = null;
	public Object getLinkedObject() {
		return linkedObject;
	}
	public void setLinkedObject(Object linkedObject) {
		this.linkedObject = linkedObject;
	}

	protected int upperX;
	protected int upperY;
	protected MouseStatus currentMouseStatus = MouseStatus.NORMAL;
	private boolean isLocked;
	
	protected final int width;
	protected final int height;
	protected final int screenWidth;
	protected final int screenHeight;
	protected final int id;

	private static int idCounter = 0;
	
	protected ScreenRect(int screenWidth, int screenHeight, int width, int height) {
		this.screenHeight = screenHeight; // panel.getDisplayMode().getHeight();
		this.screenWidth = screenWidth; // panel.getDisplayMode().getWidth();
		if (width < 0 || width > screenWidth || height < 0 || height > screenHeight) {
			throw new OutOfScreenException();
		}
		this.width = width;
		this.height = height;
		
		id = idCounter++;
		clickListener = hoverInListener = hoverOutListener = null;
		isLocked = false;
	}
	
	/** GETTERS **/
	
	public final int getUpperX() {
		return upperX;
	}
	public final int getUpperY() {
		return upperY;
	}
	
	public final int getWidth() {
		return width;
	}
	public final int getHeight() {
		return height;
	}
	
	public final int getId() {
		return id;
	}

	
	public final int getCenterX() {
		return (upperX + width) / 2;
	}
	public final int getCenterY() {
		return (upperY + height) / 2;
	}
	public final Point getCenter() {
		return new Point(getCenterX(), getCenterY());
	}
	
	public final boolean equals(ScreenRect r) {
		return this.id == r.id;
	}
	
	public final boolean outOfScreen() {
		return upperX < 0 || upperY < 0 || upperX > screenWidth || upperY > screenHeight;
	}

	public final boolean contains(Point p) {
		if (p == null) throw new IllegalArgumentException("p can't be null!");
		return upperX < p.x && p.x < upperX + width && upperY < p.y && p.y < upperY + height;
	}
	
	public abstract void paint(Graphics2D g);
	
	/** SETTERS **/

	public final void lock() {
		isLocked = true;
	}
	
	public final void setPosition(BasePos screenXPos, BasePos screenYPos) {
		if (isLocked) throw new RectLockException();
		upperX = (int) (0.5 * screenXPos.multiplier * (screenWidth - width));
		upperX += (int) (width * screenXPos.centerShift);
		upperY = (int) (0.5 * screenYPos.multiplier * (screenHeight - height));
		upperX += (int) (height * screenYPos.centerShift);
		if (outOfScreen()) throw new OutOfScreenException();
	}
	
	public final void setPosition(int upperX, int upperY) {
		if (isLocked) throw new RectLockException();
		this.upperX = upperX;
		this.upperY = upperY;
		if (outOfScreen()) throw new OutOfScreenException();
	}
	
	public final void setPosition(ScreenRect reference, RelativePos relPos) {
		setPosition(reference, relPos, 0);
	}
	public final void setPosition(ScreenRect reference, RelativePos relPos, int padding) {
		if (isLocked || !reference.isLocked) throw new RectLockException();
		if (relPos == RelativePos.ABOVE) {
			upperX = reference.upperX;
			upperY = reference.upperY - padding - height;
		} else if (relPos == RelativePos.BELOW) {
			upperX = reference.upperX;
			upperY = reference.upperY + padding + reference.height;
		} else if (relPos == RelativePos.LEFT) {
			upperX = reference.upperX - padding - width;
			upperY = reference.upperY;
		} else /* if (relPos == RelativePos.RIGHT) */ {
			upperX = reference.upperX + padding + reference.width;
			upperY = reference.upperY;
		}
		if (outOfScreen()) throw new OutOfScreenException();
	}
	
	public final void move(Direction direction, int howMuch) {
		if (direction == Direction.UP) move(0, -howMuch);
		else if (direction == Direction.DOWN) move(0, howMuch);
		else if (direction == Direction.LEFT) move(-howMuch, 0);
		else /*if (direction == Direction.RIGHT)*/ move(howMuch, 0);
	}
	
	public final void move(int deltaX, int deltaY) {
		if (isLocked) throw new RectLockException();
		upperX += deltaX;
		upperY += deltaY;
		if (outOfScreen()) throw new OutOfScreenException();
	}
	
	public final void moveScreenPercent(Direction direction, double howMuchPercent) {
		if (direction == Direction.UP || direction == Direction.DOWN) {
			move(direction, (int) (howMuchPercent * (screenHeight - height)));
		} else {
			move(direction, (int) (howMuchPercent * (screenWidth - width)));
		}
	}
	
	public final void moveScreenPercent(double percentDeltaX, double percentDeltaY) {
		move((int)(percentDeltaX * (screenWidth - width)), (int)(percentDeltaY * (screenHeight - height)));
	}

}
