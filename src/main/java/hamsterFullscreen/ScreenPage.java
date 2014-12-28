package hamsterFullscreen;


import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;


public class ScreenPage {

	private final ArrayList<ScreenRect> rects;
	private ScreenRect[] rectArray;

	public ScreenPage() {
		rects = new ArrayList<ScreenRect>();
		rectArray = new ScreenRect[0];
	}
	
	public ArrayList<ScreenRect> getRectArrayList() {
		return rects;
	}
	
	public ScreenRect[] getRectArray() {
		return rectArray;
	}
	
	public void addRect(ScreenRect r) {
		r.lock();
		rects.add(r);
		rectArray = rects.toArray(rectArray);
	}
	
	public void removeRect(ScreenRect r) {
		for (int i = 0; i < rects.size(); i++)
			if (rects.get(i).equals(r))
				rects.remove(i);
		rectArray = rects.toArray(rectArray);
	}
	
	public void registerHover(Point mouseNow, Point mouseBefore, GamePanel panel) {
		if (mouseNow == null) throw new IllegalArgumentException("mouseNow can't be null!");
		// use mouseBefore == null to trigger all HoverIns possible
		
		// first register all hoverOuts, then, only then, the hoverIns
		boolean[] hoveredOut = new boolean[rectArray.length];
		for (int i = 0; i < rectArray.length; i++) {
			hoveredOut[i] = false;
			if (!rectArray[i].contains(mouseNow) && mouseBefore != null && rectArray[i].contains(mouseBefore)) {
				rectArray[i]._registerHoverOut(panel);
				hoveredOut[i] = true;
			}
		}
		for (int i = 0; i < rectArray.length; i++) {
			if (!hoveredOut[i] && rectArray[i].contains(mouseNow) && (mouseBefore == null || !rectArray[i].contains(mouseBefore))) {
				rectArray[i]._registerHoverIn(panel);
			}
		}
	}
	
	/*
	public void registerHover(Point mouseNow, Point mouseBefore, GamePanel panel) {
		if (mouseNow == null) throw new IllegalArgumentException("mouseNow can't be null!");
		// use mouseBefore == null to trigger all HoverIns possible

		for (int i = 0; i < rects.size(); i++) {
			ScreenRect r = rects.get(i);
			if (!r.contains(mouseNow) && (mouseBefore == null || r.contains(mouseBefore))) {
				r._registerHoverOut(panel);
			} else if (r.contains(mouseNow) && (mouseBefore == null || !r.contains(mouseBefore))) {
				rects.get(i)._registerHoverIn(panel);
			}
		}
	}
	*/
	
	public void registerMouseReleased(Point where) {
		for (int i = 0; i < rects.size(); i++)
			if (rects.get(i).contains(where))
				rects.get(i)._registerClick();
	}
	
	public void registerMouseDown(Point where) {
		for (int i = 0; i < rects.size(); i++)
			if (rects.get(i).contains(where))
				rects.get(i)._registerPressCosmetics();
	}
	
	public void paintAllRects(Graphics2D g) {
		for (int i = 0; i < rects.size(); i++)
			rects.get(i).paint(g);
	}
	
}
