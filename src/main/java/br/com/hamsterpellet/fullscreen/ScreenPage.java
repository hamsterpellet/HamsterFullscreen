package br.com.hamsterpellet.fullscreen;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class ScreenPage {
	
	/**************************************************/

	private final ArrayList<ScreenRegion> regions;
	private ScreenRegion[] regionArray;

	public ScreenPage() {
		regions = new ArrayList<ScreenRegion>();
		regionArray = new ScreenRegion[0];
	}
	
	public void addRegion(ScreenRegion r) {
		regions.add(r);
		regionArray = regions.toArray(regionArray);
	}
	
	public void removeRegion(ScreenRegion r) {
		regions.remove(r);
		regionArray = regions.toArray(regionArray);
	}
	
	public void registerHover(Point mouseNow, GamePanel panel) {
		if (mouseNow == null) throw new IllegalArgumentException("Point mouseNow can't be null!");
		// use mouseBefore == null to trigger all HoverIns possible
		
		// first register all hoverOuts, then, only then, the hoverIns
		boolean[] hoveredOut = new boolean[regionArray.length];
		
		// check hoverOuts
		for (int i = 0; i < regionArray.length; i++) {
			hoveredOut[i] = false;
			if (regionArray[i].isHovered() && !regionArray[i].contains(mouseNow)) {
				regionArray[i].registerMouseOut(panel);
				hoveredOut[i] = true;
			}
		}
		
		// check hoverIns
		for (int i = 0; i < regionArray.length; i++) {
			if (!hoveredOut[i] && !regionArray[i].isHovered() && regionArray[i].contains(mouseNow)) {
				regionArray[i].registerMouseIn(panel);
			}
		}
	}
	
	public void registerMouseUp(Point where) {
		for (int i = 0; i < regionArray.length; i++) {
			if (regionArray[i].contains(where)) {
				regionArray[i].registerMouseUp();
			}
		}
	}
	
	public void registerMouseDown(Point where) {
		for (int i = 0; i < regionArray.length; i++) {
			if (regionArray[i].contains(where)) {
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
	public static final class OutOfScreenException extends RuntimeException {}
	
	public static enum MouseStatus {NORMAL, HOVER, PRESSED};
	
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
	
}
