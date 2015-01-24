package br.com.hamsterpellet.fullscreen;

import java.awt.Point;
import java.util.ArrayList;

import br.com.hamsterpellet.fullscreen.ScreenPage.BasePos;
import br.com.hamsterpellet.fullscreen.ScreenPage.Direction;
import br.com.hamsterpellet.fullscreen.ScreenPage.OutOfScreenException;
import br.com.hamsterpellet.fullscreen.ScreenPage.RelativePos;


public class ScreenRect extends ScreenRegion {

	protected int relativeUpperX;
	protected int relativeUpperY;
	protected final int width;
	protected final int height;
	
	private ScreenRect parent; // parent can be reset by makeTable() method
	protected final ArrayList<ScreenRect> children;
	
	protected ScreenRect(int screenWidth, int screenHeight, int width, int height, ScreenRect parent) {
		super(screenWidth, screenHeight);
		if (width < 0 || width > screenWidth || height < 0 || height > screenHeight) {
			throw new OutOfScreenException();
		}
		this.width = width;
		this.height = height;
		
		this.parent = parent;
		this.children = new ArrayList<ScreenRect>();
	}
	
	public static ScreenRect create(int screenWidth, int screenHeight, int width, int height, ScreenRect parent) {
		return new ScreenRect(screenWidth, screenHeight, width, height, parent);
	}

	public final boolean contains(int x, int y) {
		return relativeUpperX < x && x < relativeUpperX + width && relativeUpperY < y && y < relativeUpperY + height;
	}
	
	/** FAMILY STUFF **/
	
	public final void addChild(ScreenRect child) {
		children.add(child);
	}
	public final ScreenRect getParent() {
		return parent;
	}

	public static final ScreenRect makeTable(ScreenRect[][] cells, int screenWidth, int screenHeight, ScreenRect parent) {
		cells[0][0].setPosition(0, 0);
		int totalHeight = 0;
		int totalWidth = 0;
		for (int i = 0; i < cells.length; i++) {
			if (i != 0) cells[i][0].setPosition(cells[i-1][0], RelativePos.BELOW);
			totalHeight += cells[i][0].height;
			int thisRowWidth = cells[i][0].width;
			for (int j = 1; j < cells[i].length; j++) {
				if (cells[i][j].height != cells[i][0].height) {
					throw new IllegalArgumentException("Cells from the same row must have equal heights");
				}
				cells[i][j].setPosition(cells[i][j-1], RelativePos.RIGHT);
				thisRowWidth += cells[i][j].width;
			}
			if (thisRowWidth > totalWidth) totalWidth = thisRowWidth;			
		}
		ScreenRect container = ScreenRect.create(screenWidth, screenHeight, totalWidth, totalHeight, parent);
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				container.addChild(cells[i][j]);
			}
		}
		return container;
	}
	
	/** MOVE & SETPOS STUFF **/
	
	private final boolean isOutOfScreen() {
		if (relativeUpperX < 0 || relativeUpperY < 0) return true;
		int parentWidth = screenWidth;
		int parentHeight = screenHeight;
		if (parent != null) {
			parentWidth = parent.width;
			parentHeight = parent.height;
		}
		return relativeUpperX + width > parentWidth || relativeUpperY + height > parentHeight;
	}
	
	public final void move(int deltaX, int deltaY) {
		relativeUpperX += deltaX;
		relativeUpperY += deltaY;
		if (isOutOfScreen()) throw new OutOfScreenException();
		for (ScreenRect child : children) {
			child.move(deltaX, deltaY);
		}
	}
	
	public final void move(Direction direction, int howMuch) {
		if (direction == Direction.UP) move(0, -howMuch);
		else if (direction == Direction.DOWN) move(0, howMuch);
		else if (direction == Direction.LEFT) move(-howMuch, 0);
		else /*if (direction == Direction.RIGHT)*/ move(howMuch, 0);
	}
	
	public final void movePercentOfScreen(Direction direction, double howMuchPercent) {
		if (direction == Direction.UP || direction == Direction.DOWN) {
			move(direction, (int) (howMuchPercent * (screenHeight - height)));
		} else {
			move(direction, (int) (howMuchPercent * (screenWidth - width)));
		}
	}
	
	public final void movePercentOfScreen(double percentDeltaX, double percentDeltaY) {
		move((int)(percentDeltaX * (screenWidth - width)), (int)(percentDeltaY * (screenHeight - height)));
	}
	
	public final void setPosition(int upperX, int upperY) {
		int deltaX = upperX - this.relativeUpperX;
		int deltaY = upperY - this.relativeUpperY;
		move(deltaX, deltaY);
	}
	
	public final void setPosition(BasePos screenXPos, BasePos screenYPos) {
		int newUpperX, newUpperY;
		newUpperX = (int) (0.5 * screenXPos.multiplier * (screenWidth - width));
		newUpperX += (int) (width * screenXPos.centerShift);
		newUpperY = (int) (0.5 * screenYPos.multiplier * (screenHeight - height));
		newUpperY += (int) (height * screenYPos.centerShift);
		setPosition(newUpperX, newUpperY);
	}
	
	public final void setPosition(ScreenRect reference, RelativePos relPos) {
		setPosition(reference, relPos, 0);
	}
	public final void setPosition(ScreenRect reference, RelativePos relPos, int padding) {
		int newUpperX,  newUpperY;
		if (relPos == RelativePos.ABOVE) {
			newUpperX = reference.relativeUpperX;
			newUpperY = reference.relativeUpperY - padding - height;
		} else if (relPos == RelativePos.BELOW) {
			newUpperX = reference.relativeUpperX;
			newUpperY = reference.relativeUpperY + padding + reference.height;
		} else if (relPos == RelativePos.LEFT) {
			newUpperX = reference.relativeUpperX - padding - width;
			newUpperY = reference.relativeUpperY;
		} else /* if (relPos == RelativePos.RIGHT) */ {
			newUpperX = reference.relativeUpperX + padding + reference.width;
			newUpperY = reference.relativeUpperY;
		}
		setPosition(newUpperX, newUpperY);
	}

	/** PUBLIC USAGE GETTERS **/
	
	public final int getRelativeUpperX() {
		return relativeUpperX;
	}
	public final int getRelativeUpperY() {
		return relativeUpperY;
	}
	public final Point getRelativeCenter() {
		return new Point((getRelativeUpperX() + width) / 2, (getRelativeUpperY() + height) / 2);
	}
	
	public final int getUpperX() {
		if (parent == null) return relativeUpperX;
		return parent.getUpperX() + relativeUpperX;
	}
	public final int getUpperY() {
		if (parent == null) return relativeUpperY;
		return parent.getUpperY() + relativeUpperY;
	}
	public final Point getCenter() {
		return new Point((getUpperX() + width) / 2, (getUpperY() + height) / 2);
	}
	
	public final int getWidth() {
		return width;
	}
	public final int getHeight() {
		return height;
	}
}
