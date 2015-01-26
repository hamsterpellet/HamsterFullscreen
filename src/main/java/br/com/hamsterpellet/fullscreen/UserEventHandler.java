package br.com.hamsterpellet.fullscreen;


import java.awt.Graphics2D;
import java.awt.Point;

public abstract class UserEventHandler {
	
	/** NEVER CALL THESE METHODS OUTSIDE THE CLASS!! **/
	/** ONLY OVERRIDE THEM. CALL THE UNDERLINED METHODS INSTEAD! **/
	
	// Game Controlling
	protected abstract void onInit(final Graphics2D g);
	protected abstract void onUpdate();
	protected abstract void onRender(final Graphics2D g);
	protected abstract void onNormalExit();
	protected abstract void onErrorExit();
	
	// All the boolean methods should return true if the event is to be consumed

	// Keyboard Events
	protected abstract boolean onKeyPressed(String text, int keyCode);
	protected abstract boolean onKeyHeld(String text, int keyCode);
	protected abstract boolean onKeyReleased(String text, int keyCode);
	protected abstract boolean onKeyClicked(String text, int keyCode);
	protected abstract boolean onKeyUnheld(String text, int keyCode);
	
	// Mouse Events
	protected abstract boolean onMouseLeftButtonPressed(final Point mouseLoc);	
	protected abstract boolean onMouseLeftButtonReleased(final Point mouseLoc);	
	protected abstract boolean onMouseRightButtonPressed(final Point mouseLoc);	
	protected abstract boolean onMouseRightButtonReleased(final Point mouseLoc);	
	protected abstract boolean onMouseWheelPressed(final Point mouseLoc);	
	protected abstract boolean onMouseWheelReleased(final Point mouseLoc);	
	protected abstract boolean onMouseWheelMovedUp();
	protected abstract boolean onMouseWheelMovedDown();
	protected abstract void onMouseMoved(final Point mouseCurrentLoc, final Point mouseLastLoc, double distance, boolean dragged);
	
	/** Debugger **/
	
	public abstract static class Debugger {
		
		/** AUTO_CLEAR **/
		private boolean autoClear = true;
		private int autoClearPoolSize = 5;
		private int autoClearCounter = 0;
		public final void disableAutoClear() {
			autoClear = false;
		}
		public final void setAutoClearPoolSize(int size) {
			autoClearPoolSize = size;
		}
		/** END AUTO_CLEAR **/

		private String messagePool = "";
		private boolean mustClear = false;
		private int debugLevel = 0;
		
		public final void addMessage(String message, int level) {
			if (level >= debugLevel) {
				if (mustClear) {
					messagePool = message;
					mustClear = false;
				} else if (level > 0 && autoClear && autoClearCounter >= autoClearPoolSize) {
					// >= here to be safe against setAutoClearPoolSize() in the middle of the thing
					messagePool = message;
					autoClearCounter = 0;
				} else {
					messagePool += " | " + message;
					if (level > 0 && autoClear) autoClearCounter++;
				}
				showMessage(message, messagePool);
			}
		}
		
		protected abstract void showMessage(String message, String messagePool);
		
		protected abstract void renderMessage(final Graphics2D g, String messagePool);
		
		private void onRender(final Graphics2D g) {
			renderMessage(g, messagePool);
		}
		
		public void setDebugLevel(int level) {
			debugLevel = level;
		}
		
		private void clear(int clearLevel) {
			if (clearLevel >= debugLevel) mustClear = true;
		}
		
	}
	
	private Debugger debugger;
	protected final Debugger getDebugger() {
		return debugger;
	}
	protected final void setDebugger(Debugger debugger) {
		if (usingDefaultDebugger) {
			this.debugger = debugger;
			usingDefaultDebugger = false;
		}
	}
	
	/** CONSTRUCTOR AND STUFF **/

	private boolean usingDefaultDebugger;
	private ScreenPage page;
	public final void setScreenPage(ScreenPage page) {
		if (this.page == null) this.page = page;
	}
	
	/*
	protected final int getScreenWidth() {
		return getGamePanel().getDisplayMode().getWidth();
	}
	protected final int getScreenHeight() {
		return getGamePanel().getDisplayMode().getHeight();
	}
	
	@SuppressWarnings("serial")
	public static final class UEHandlerNotReadyException extends RuntimeException {}
	protected final boolean isReady() {
		return gamePanel != null;
	}
	*/
	
	/** CONSTRUCTOR **/
	
	protected UserEventHandler() {
		debugger = new Debugger() {
			@Override
			public final void showMessage(String message, String messagePool) { /* do nothing! */ }
			public final void renderMessage(final Graphics2D g, String messagePool) { /* do nothing! */ }
		};
		usingDefaultDebugger = true;
	}
	
	/** ############################################################ **/
	
	// Game Controlling
	protected final void _onInit(Graphics2D g) {
		debugger.addMessage("onInit()", 0);
		onInit(g);
		Point mouseLoc = GamePanel.getMouseLocation();
		if (mouseLoc == null) {
			mouseLoc = new Point(GamePanel.getScreenWidth() / 2, GamePanel.getScreenHeight() / 2);
		}
		page.registerHover(mouseLoc);
		debugger.clear(0);
	}
	protected final void _onUpdate() {
		onUpdate();
	}
	protected final void _onRender(final Graphics2D g) {
		page.paint(g);
		debugger.onRender(g);
		onRender(g);
	}
	protected final void _onNormalExit() {
		onNormalExit();
	}
	protected final void _onErrorExit() {
		onErrorExit();
	}
	
	// All the boolean methods should return true if the event is to be consumed

	// Keyboard Events
	protected final boolean _onKeyPressed(String text, int keyCode) {
		debugger.addMessage("onKeyPressed(" + text + ", " + keyCode + ")", 0);
		boolean b = onKeyPressed(text, keyCode);
		debugger.clear(0);
		return b;
	}
	protected final boolean _onKeyHeld(String text, int keyCode) {
		debugger.addMessage("onKeyHeld(" + text + ", " + keyCode + ")", 0);
		boolean b = onKeyHeld(text, keyCode);
		debugger.clear(0);
		return b;
	}
	protected final boolean _onKeyReleased(String text, int keyCode) {
		debugger.addMessage("onKeyReleased(" + text + ", " + keyCode + ")", 0);
		boolean b = onKeyReleased(text, keyCode);
		// keyClicked or keyHeld always follow this
		// debugger.showAndClear();
		return b;
	}
	protected final boolean _onKeyClicked(String text, int keyCode) {
		debugger.addMessage("onKeyClicked(" + text + ", " + keyCode + ")", 0);
		boolean b = onKeyClicked(text, keyCode);
		debugger.clear(0);
		return b;
	}
	protected final boolean _onKeyUnheld(String text, int keyCode) {
		debugger.addMessage("onKeyUnheld(" + text + ", " + keyCode + ")", 0);
		boolean b = onKeyUnheld(text, keyCode);
		debugger.clear(0);
		return b;
	}
	
	// Mouse Events
	protected final boolean _onMouseLeftButtonPressed(final Point mouseLoc) {
		debugger.addMessage("onMouseLeftButtonPressed(Point(" + mouseLoc.x + ", " + mouseLoc.y + "))", 0);
		boolean b = onMouseLeftButtonPressed(mouseLoc);
		page.registerMouseDown(mouseLoc);
		debugger.clear(0);
		return b;
	}
	protected final boolean _onMouseLeftButtonReleased(final Point mouseLoc) {
		debugger.addMessage("onMouseLeftButtonReleased(Point(" + mouseLoc.x + ", " + mouseLoc.y + "))", 0);
		boolean b = onMouseLeftButtonReleased(mouseLoc);
		page.registerMouseUp(mouseLoc);
		debugger.clear(0);
		return b;
	}
	protected final boolean _onMouseRightButtonPressed(final Point mouseLoc) {
		debugger.addMessage("onMouseRightButtonPressed(Point(" + mouseLoc.x + ", " + mouseLoc.y + "))", 0);
		boolean b = onMouseRightButtonPressed(mouseLoc);
		debugger.clear(0);
		return b;
	}
	protected final boolean _onMouseRightButtonReleased(final Point mouseLoc) {
		debugger.addMessage("onMouseRightButtonReleased(Point(" + mouseLoc.x + ", " + mouseLoc.y + "))", 0);
		boolean b = onMouseRightButtonReleased(mouseLoc);
		debugger.clear(0);
		return b;
	}
	protected final boolean _onMouseWheelPressed(final Point mouseLoc) {
		debugger.addMessage("onMouseWheelPressed(Point(" + mouseLoc.x + ", " + mouseLoc.y + "))", 0);
		boolean b = onMouseWheelPressed(mouseLoc);
		debugger.clear(0);
		return b;
	}
	protected final boolean _onMouseWheelReleased(final Point mouseLoc) {
		debugger.addMessage("onMouseWheelReleased(Point(" + mouseLoc.x + ", " + mouseLoc.y + "))", 0);
		boolean b = onMouseWheelReleased(mouseLoc);
		debugger.clear(0);
		return b;
	}
	protected final boolean _onMouseWheelMovedUp(final Point mouseLoc) {
		debugger.addMessage("onMouseWheelMovedUp(Point(" + mouseLoc.x + ", " + mouseLoc.y + "))", 0);
		boolean b = onMouseWheelMovedUp();
		debugger.clear(0);
		return b;
	}
	protected final boolean _onMouseWheelMovedDown(final Point mouseLoc) {
		debugger.addMessage("onMouseWheelMovedDown(Point(" + mouseLoc.x + ", " + mouseLoc.y + "))", 0);
		boolean b = onMouseWheelMovedDown();
		debugger.clear(0);
		return b;
	}
	protected final void _onMouseMoved(final Point mouseCurrentLoc, final Point mouseLastLoc, double distance, boolean dragged) {
		debugger.addMessage("onMouseMoved(Point(" + mouseCurrentLoc.x + ", " + mouseCurrentLoc.y + "), Point(" +
				mouseLastLoc.x + ", " + mouseLastLoc.y + "), " + distance + ", " + dragged + ")", 0);
		page.registerHover(mouseCurrentLoc);
		onMouseMoved(mouseCurrentLoc, mouseLastLoc, distance, dragged);
		debugger.clear(0);
	}
	
}
