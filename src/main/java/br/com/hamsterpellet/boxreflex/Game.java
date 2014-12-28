package br.com.hamsterpellet.boxreflex;


import java.awt.Color;
import java.util.HashSet;

import br.com.hamsterpellet.fullscreen.ScreenColoredCursoredRect;
import br.com.hamsterpellet.fullscreen.ScreenPage;
import br.com.hamsterpellet.fullscreen.ScreenRect;
import br.com.hamsterpellet.fullscreen.UserEventHandler.Debugger;

public class Game {

	private final Box[][] boxGrid;
	private final ScreenRect[][] rectGrid;
	private final int screenWidth;
	private final int screenHeight;
	private final ScreenPage page;
	private final Debugger debugger;
	
	private Box activeBox;
	public Box getActiveBox() {
		return activeBox;
	}
	
	private Game(int screenWidth, int screenHeight, Debugger debugger) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		boxGrid = new Box[5][5];
		rectGrid = new ScreenRect[5][5];
		page = new ScreenPage();
		this.debugger = debugger;
	}
	
	private int activePatternId = 0;
	public void changePattern() {
		activePatternId = (activePatternId + 1) % Pattern.allPatterns.length;
	}
	public Pattern getActivePattern() {
		return Pattern.allPatterns[activePatternId];
	}
	
	public void highlightOnBox(int x, int y) {
		debugger.addMessage("I(" + x + "," + y + ")", 1);
		activeBox = boxGrid[x][y];
		if (getActivePattern().fitsInSpace(4, 4, x, y)) {
			HashSet<Box> boxesAffected = getActivePattern().useAsFilterForBoxGrid(boxGrid, x, y);
			for (int k = 0; k < boxGrid.length; k++) {
				for (int l = 0; l < boxGrid[k].length; l++) {
					if (boxesAffected.contains(boxGrid[k][l])) {
						((ScreenColoredCursoredRect) rectGrid[k][l]).setFillColor(boxGrid[k][l].getHoverColor());
						((ScreenColoredCursoredRect) rectGrid[k][l]).setFillColor(boxGrid[k][l].getHoverColor(), ScreenRect.MouseStatus.HOVER);
					}
				}
			}
		}
	}
	public void unHighlightOnBox(int x, int y) {
		debugger.addMessage("O(" + x + "," + y + ")", 1);
		activeBox = null;
		if (getActivePattern().fitsInSpace(4, 4, x, y)) {
			HashSet<Box> boxesAffected = getActivePattern().useAsFilterForBoxGrid(boxGrid, x, y);
			for (int k = 0; k < boxGrid.length; k++) {
				for (int l = 0; l < boxGrid[k].length; l++) {
					if (boxesAffected.contains(boxGrid[k][l])) {
						((ScreenColoredCursoredRect) rectGrid[k][l]).setFillColor(boxGrid[k][l].getColor());
						((ScreenColoredCursoredRect) rectGrid[k][l]).setFillColor(boxGrid[k][l].getColor(), ScreenRect.MouseStatus.HOVER);
					}
				}
			}
		}
	}
	
	private void init() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				final int fi = i;
				final int fj = j;
				final Box activeBox = new Box(i, j);
				final ScreenColoredCursoredRect rect = ScreenColoredCursoredRect.create(screenWidth, screenHeight, 50, 50);
				rect.setPosition((int)(screenWidth / 2 - 125 + j * 50),(int)(screenHeight / 2 - 125 + i * 50));
				rect.setFillColor(activeBox.getColor());
				rect.setFillColor(Color.YELLOW, ScreenRect.MouseStatus.PRESSED);
				rect.setHoverInListener(new ScreenRect.RectListener() {
					@Override
					public void run(ScreenRect r) {
						highlightOnBox(fi, fj);
					}
				});
				rect.setHoverOutListener(new ScreenRect.RectListener() {
					@Override
					public void run(ScreenRect r) {
						unHighlightOnBox(fi, fj);
					}
				});
				rect.setClickListener(new ScreenRect.RectListener() {
					@Override
					public void run(ScreenRect r) {
						debugger.addMessage("HMM", 1);
						if (getActivePattern().fitsInSpace(4, 4, fi, fj)) {
							debugger.addMessage("INSIDE", 1);
							HashSet<Box> boxesAffected = getActivePattern().useAsFilterForBoxGrid(boxGrid, fi, fj);
							for (Box box : boxesAffected) {
								box.change();
								((ScreenColoredCursoredRect) rectGrid[box.x][box.y]).setFillColor(box.getColor());
								((ScreenColoredCursoredRect) rectGrid[box.x][box.y]).setFillColor(box.getHoverColor(), ScreenRect.MouseStatus.HOVER);
								((ScreenColoredCursoredRect) rectGrid[box.x][box.y]).setFillColor(Color.WHITE, ScreenRect.MouseStatus.PRESSED);				
							}
						}
					}
				});
				boxGrid[i][j] = activeBox;
				rectGrid[i][j] = rect;
				page.addRect(rect);
			}
		}		
	}
	
	public static Game start(int screenWidth, int screenHeight, Debugger debugger) {
		final Game game = new Game(screenWidth, screenHeight, debugger);
		game.init();
		return game;
	}
	
	public ScreenPage getPage() {
		return page;
	}
	
}
