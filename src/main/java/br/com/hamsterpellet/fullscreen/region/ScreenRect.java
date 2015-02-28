package br.com.hamsterpellet.fullscreen.region;

import java.awt.Graphics2D;

import br.com.hamsterpellet.fullscreen.GamePanel;
import br.com.hamsterpellet.fullscreen.ScreenPage.Direction;
import br.com.hamsterpellet.fullscreen.ScreenPage.OneDimPosition;
import br.com.hamsterpellet.fullscreen.ScreenPage.OutOfParentException;
import br.com.hamsterpellet.fullscreen.ScreenPage.RelativePos;


public class ScreenRect extends ScreenRegion {

	protected double relativeUpperX;
	protected double relativeUpperY;
	protected final double width;
	protected final double height;
	
	protected ScreenRect(double width, double height, ScreenRect parent) {
		/** NEVER CALL THIS, CALL FACTORY() INSTEAD **/
		super(parent);
		
		// pass NULL as parent for the parent to be auto-set to the root.
		if (width < 0 || width > 1 || height < 0 || height > 1) {
			throw new OutOfParentException();
		}
		this.width = width;
		this.height = height;
	}
	private static ScreenRect factory(double width, double height, ScreenRect parent) {
		ScreenRect r = new ScreenRect(width, height, parent);
		r.getParent().addChild(r);
		return r;
	}
	
	public static ScreenRect create(double width, double height, ScreenRect parent) {
		return factory(width, height, parent);
	}

	public final boolean contains(double x, double y) {
		return relativeUpperX < x && x < relativeUpperX + width && relativeUpperY < y && y < relativeUpperY + height;
	}
	
	/** FAMILY STUFF **/

	public static final ScreenRect makeTable(ScreenRect parentForContainer, ScreenRect[][] cells) {
		// CELLS IN THE SAME ROW MUST HAVE THE SAME HEIGHT, OTHERWISE ILLEGALARGUMENTEXCEPTION
		cells[0][0].setPosition(0, 0);
		double totalHeight = 0;
		double totalWidth = 0;
		for (int i = 0; i < cells.length; i++) {
			if (i != 0) cells[i][0].setPosition(cells[i-1][0], RelativePos.BELOW);
			totalHeight += cells[i][0].height;
			double thisRowWidth = cells[i][0].width;
			for (int j = 1; j < cells[i].length; j++) {
				if (cells[i][j].height != cells[i][0].height) {
					throw new IllegalArgumentException("Cells from the same row must have equal heights");
				}
				cells[i][j].setPosition(cells[i][j-1], RelativePos.RIGHT);
				thisRowWidth += cells[i][j].width;
			}
			if (thisRowWidth > totalWidth) totalWidth = thisRowWidth;
		}
		ScreenRect container = ScreenRect.create(totalWidth, totalHeight, parentForContainer);
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				container.addChild(cells[i][j]);
			}
		}
		return container;
	}
	
	public static final ScreenRect makeColumnTable(ScreenRect parentForContainer, ScreenRect... cells) {
		ScreenRect[][] tableCells = new ScreenRect[cells.length][1];
		for (int i = 0; i < cells.length; i++) {
			tableCells[i][0] = cells[i];
		}
		return makeTable(parentForContainer, tableCells);
	}
	
	public static final ScreenRect makeRowTable(ScreenRect parentForContainer, ScreenRect... cells) {
		ScreenRect[][] tableCells = new ScreenRect[1][cells.length];
		for (int i = 0; i < cells.length; i++) {
			tableCells[0][i] = cells[i];
		}
		return makeTable(parentForContainer, tableCells);
	}
	
		
	/** MOVE & SETPOS STUFF **/
	
	private final boolean isOutOfParent() {
		try {
			if (relativeUpperX < 0 || relativeUpperY < 0) return true;
			if (relativeUpperX + width > ((ScreenRect)getParent()).width) return true;
			return relativeUpperY + height > ((ScreenRect)getParent()).height;
		} catch (ClassCastException e) {
			return true;
		}
	}
	
	public final void move(double deltaX, double deltaY) {
		if (deltaX == 0 && deltaY == 0) return;
		relativeUpperX += deltaX;
		relativeUpperY += deltaY;
		if (isOutOfParent()) throw new OutOfParentException();
		for (ScreenRegion child : children) {
			if (child instanceof ScreenRect) {
				((ScreenRect) child).move(deltaX, deltaY);
			}
		}
	}
	
	public final void move(Direction direction, double howMuch) {
		if (direction == Direction.UP) move(0, -howMuch);
		else if (direction == Direction.DOWN) move(0, howMuch);
		else if (direction == Direction.LEFT) move(-howMuch, 0);
		else /*if (direction == Direction.RIGHT)*/ move(howMuch, 0);
	}
	
	public final void setPosition(double upperX, double upperY) {
		move(upperX - this.relativeUpperX, upperY - this.relativeUpperY);
	}
	
	public final void setPosition(OneDimPosition xPos, OneDimPosition yPos) {
		setPosition(xPos.screenPosition + xPos.dimensionShift * width,
				yPos.screenPosition + yPos.dimensionShift * height);
	}
	
	public final void setPosition(ScreenRect reference, RelativePos relPos) {
		setPosition(reference, relPos, 0);
	}
	public final void setPosition(ScreenRect reference, RelativePos relPos, int padding) {
		double newUpperX,  newUpperY;
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
	
	public final double getRelativeUpperX() {
		return relativeUpperX;
	}
	public final double getRelativeUpperY() {
		return relativeUpperY;
	}
	
	public final double getUpperX() throws ClassCastException {
		if (isRoot()) return relativeUpperX; // which is probably be zero I think
		return ((ScreenRect)getParent()).getUpperX() + relativeUpperX;
	}
	public final double getUpperY() throws ClassCastException {
		if (isRoot()) return relativeUpperY; // which is probably be zero I think
		return ((ScreenRect)getParent()).getUpperY() + relativeUpperY;
	}
	
	public final int getUpperXOnScreen() {
		return (int) (getUpperX() * GamePanel.getScreenWidth());
	}
	public final int getUpperYOnScreen() {
		return (int) (getUpperY() * GamePanel.getScreenHeight());
	}
	
	public final double getWidth() {
		return width;
	}
	public final double getHeight() {
		return height;
	}
	
	public final int getWidthOnScreen() {
		return (int) (width * GamePanel.getScreenWidth());
	}
	public final int getHeightOnScreen() {
		return (int) (height * GamePanel.getScreenHeight());
	}
	
	public final int getLowerXOnScreen() {
		return getUpperXOnScreen() + getWidthOnScreen();
	}
	public final int getLowerYOnScreen() {
		return getUpperYOnScreen() + getHeightOnScreen();
	}
	
	@Override
	public void paint(Graphics2D g) {
		super.paint(g);
	}
}
