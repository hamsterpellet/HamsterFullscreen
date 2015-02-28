package br.com.hamsterpellet.fullscreen;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import br.com.hamsterpellet.fullscreen.region.ScreenRegion;

public final class ScreenPage {
	
	/**************************************************/

	private final ArrayList<ScreenRegion> regions;
	private ScreenRegion[] regionArray;

	private boolean justBorn = true; // used by GamePanel to decide to call onInit() or not
	public void unsetJustBorn() {
		justBorn = false;
	}
	public boolean wasJustBorn() {
		return justBorn;
	}
	
	private final UserEventHandler ueHandler;
	public final UserEventHandler getUserEventHandler() {
		return ueHandler;
	}
	
	public ScreenPage(UserEventHandler ueHandler) {
		this.ueHandler = ueHandler;
		regions = new ArrayList<ScreenRegion>();
		regionArray = new ScreenRegion[0];
	}
	
	private void addRegionWithoutUpdatingArray(ScreenRegion r) {
		regions.add(r);
		for (ScreenRegion child : r.getChildren()) {
			addRegionWithoutUpdatingArray(child);
		}
	}
	public void addRegion(ScreenRegion r) {
		addRegionWithoutUpdatingArray(r);
		regionArray = regions.toArray(regionArray);
	}
	
	private void removeRegionWithoutUpdatingArray(ScreenRegion r) {
		regions.remove(r);
		for (ScreenRegion child : r.getChildren()) {
			removeRegionWithoutUpdatingArray(child);
		}
	}
	public void removeRegion(ScreenRegion r) {
		removeRegionWithoutUpdatingArray(r);
		regionArray = regions.toArray(regionArray);
	}
	
	public void registerHover(Point mouseNow) {
		if (mouseNow == null) throw new IllegalArgumentException("Point mouseNow can't be null!");
		// use mouseBefore == null to trigger all HoverIns possible
		
		// first register all hoverOuts, then, only then, the hoverIns
		boolean[] hoveredOut = new boolean[regionArray.length];
		
		// check hoverOuts
		for (int i = 0; i < regionArray.length; i++) {
			hoveredOut[i] = false;
			if (regionArray[i].isHovered() && !regionArray[i].containsOnScreen(mouseNow)) {
				regionArray[i].registerMouseOut();
				hoveredOut[i] = true;
			}
		}
		
		// check hoverIns
		for (int i = 0; i < regionArray.length; i++) {
			if (!hoveredOut[i] && !regionArray[i].isHovered() && regionArray[i].containsOnScreen(mouseNow)) {
				regionArray[i].registerMouseIn();
			}
		}
	}
	
	public void registerMouseUp(Point where) {
		for (int i = 0; i < regionArray.length; i++) {
			if (regionArray[i].containsOnScreen(where)) {
				regionArray[i].registerMouseUp();
			}
		}
	}
	
	public void registerMouseDown(Point where) {
		for (int i = 0; i < regionArray.length; i++) {
			if (regionArray[i].containsOnScreen(where)) {
				regionArray[i].registerMouseDown();
			}
		}
	}
	
	public void paint(Graphics2D g) {
		for (int i = 0; i < regionArray.length; i++) {
			regionArray[i].paint(g);
		}
	}
	
	/****************************************************/

	@SuppressWarnings("serial")
	public static final class OutOfParentException extends RuntimeException {}
	
	public static enum MouseStatus {NORMAL, HOVER, PRESSED};
	
	public static enum OneDimPosition {
		BEGIN(0, 0), BEFORE_CENTER(0.5, -1), CENTER(0.5, -0.5), AFTER_CENTER(0.5, 0), END(1, -1);
		
		// Example: BEFORE_CENTER is (0.5, -1) because:
		// - move UpperX to 0.5 of screen
		// - then move it to the right in -1*width units
		// (aka move it back to the left in an amount equal to the width
		//  in order for the rect to be right before the vertical center line)
		// this also works for horizontal (upperY)
		
		public final double screenPosition;
		public final double dimensionShift;
		
		private OneDimPosition(double screenPosition, double dimensionShift) {
			this.screenPosition = screenPosition;
			this.dimensionShift = dimensionShift;
		}
	}
	public static enum RelativePos {
		LEFT, RIGHT, ABOVE, BELOW;
	}
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT; 
	}
	
}
