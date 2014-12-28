import java.awt.Point;
import java.util.HashSet;


public class Pattern {

	private HashSet<Point> allPoints = new HashSet<Point>();
	
	public static final Pattern[] allPatterns;
	
	static {
		allPatterns = new Pattern[5];
		allPatterns[0] = new Pattern();
		allPatterns[0].addPoint(0, 0);
		allPatterns[0].addPoint(1, 0);
		allPatterns[0].addPoint(0, 1);
		allPatterns[0].addPoint(1, 1);
		allPatterns[1] = new Pattern();
		allPatterns[1].addPoint(0, 0);
		allPatterns[1].addPoint(1, 0);
		allPatterns[1].addPoint(0, 1);
		allPatterns[1].addPoint(1, 1);
		allPatterns[1].addPoint(1, 2);
		allPatterns[2] = new Pattern();
		allPatterns[2].addPoint(1, -1);
		allPatterns[2].addPoint(-1, 1);
		allPatterns[3] = new Pattern();
		allPatterns[3].addPoint(-1, -1);
		allPatterns[3].addPoint(1, 1);
		allPatterns[3].addPoint(0, 0);
		allPatterns[3].addPoint(1, 0);
		allPatterns[4] = new Pattern();
		allPatterns[4].addPoint(-1, -1);
		allPatterns[4].addPoint(1, 1);
	}
	
	private void addPoint(int x, int y) {
		allPoints.add(new Point(x, y));
	}
	
	public boolean fitsInSpace(int maxX, int maxY, int posX, int posY) {
		for (Point p : allPoints) {
			if (0 > p.x + posX || p.x + posX > maxX || 0 > p.y + posY || p.y + posY > maxY) return false;
		}
		return true;
	}
	
	public HashSet<Box> useAsFilterForBoxGrid(Box[][] grid, int posX, int posY) {
		HashSet<Box> ans = new HashSet<Box>();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (allPoints.contains(new Point(i - posX, j - posY))) {
					ans.add(grid[i][j]);
				}
			}
		}
		return ans;
	}
	
}
