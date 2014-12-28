

import java.awt.Color;

public class Box {

	public int state;
	public boolean isHovered = false;
	public boolean isInRange = false;
	public final int x;
	public final int y;
	
	public Box(int x, int y) {
		state = 0;
		this.x = x;
		this.y = y;
	}
	
	public static Color getColorByState(int state) {
		if (state == 0) return Color.decode("#FF0000");
		if (state == 1) return Color.decode("#009900");
		return Color.decode("#0000DD");
	}
	public static Color getHoverColorByState(int state) {
		if (state == 0) return Color.decode("#FF5050");
		if (state == 1) return Color.decode("#50BB50");
		return Color.decode("#5050FF");
	}
	
	public void change() {
		state = (state + 1) % 3;
	}
	
	public Color getColor() {
		return getColorByState(state);
	}
	public Color getHoverColor() {
		return getHoverColorByState(state);
	}
	
}
