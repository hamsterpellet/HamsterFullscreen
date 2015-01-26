package br.com.hamsterpellet.fullscreen.region;

import java.awt.Graphics2D;

public abstract class Debugger {
	
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
	
	public void onRender(final Graphics2D g) {
		renderMessage(g, messagePool);
	}
	
	public void setDebugLevel(int level) {
		debugLevel = level;
	}
	
	public void clear(int clearLevel) {
		if (clearLevel >= debugLevel) mustClear = true;
	}
	
}