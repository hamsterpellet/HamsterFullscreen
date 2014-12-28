package br.com.hamsterpellet.fullscreen;


import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ScreenManager {

	private final GraphicsDevice videoCard;
	
	public ScreenManager() {
		videoCard = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	}
	
	public Window getFullScreenWindow() {
		// returns the window which is in full screen mode right now
		// if no screen is doing this, this returns null
		return videoCard.getFullScreenWindow();
	}
	
	public Graphics2D getGraphics() {
		Window w = videoCard.getFullScreenWindow();
		if (w != null) {
			BufferStrategy s = w.getBufferStrategy();
			return (Graphics2D)s.getDrawGraphics();
		}
		return null;
	}
	
	public JFrame setFullScreen(DisplayMode dm, JPanel panel) {
		JFrame f = new JFrame();
		f.setUndecorated(true);
		//f.setIgnoreRepaint(true);
		f.setResizable(false);
		f.setContentPane(panel);
		f.setFocusable(true);
		f.setFocusTraversalKeysEnabled(false);
		videoCard.setFullScreenWindow(f);
		
		if (dm != null && videoCard.isDisplayChangeSupported()) {
			videoCard.setDisplayMode(dm);
		}
		
		f.createBufferStrategy(2);
		f.requestFocus();
		return f;
	}
	
	public void exitFullScreen() {
		Window window = videoCard.getFullScreenWindow();
		if (window != null) {
			window.dispose();
		}
		videoCard.setFullScreenWindow(null);
	}
	
	public void drawFromBuffer() {
		Window window = videoCard.getFullScreenWindow();
		if (window != null) {
			BufferStrategy buffer = window.getBufferStrategy();
			if (!buffer.contentsLost()) {
				// if the buffer gets lost, we don't wanna show the nothingness.
				buffer.show();
			}
		}
	}
	
	/** BORING STUFF **/
	
	public DisplayMode[] getCompatibleDisplayModes() {
		return videoCard.getDisplayModes();
	}
	
	public DisplayMode findBestCompatibleDisplayMode(DisplayMode[] modes) {
		// returns null if none is found
		DisplayMode[] modesOnVC = videoCard.getDisplayModes();
		DisplayMode currentBest = null;
		for (int i = 0; i<modes.length; i++) {
			for (int j = 0; j<modesOnVC.length; j++) {
				if (displayModesMatch(modes[i],modesOnVC[j])) {
					currentBest = getBetterDisplayMode(currentBest, modes[i]);
				}
			}
		}
		return currentBest;
	}
	
	//  This is subjective but whatever
	private DisplayMode getBetterDisplayMode(DisplayMode a, DisplayMode b) {
		if (a == null) return b;
		if (b == null) return a;
		int aw = a.getWidth();
		int ah = a.getHeight();
		int bw = b.getWidth();
		int bh = b.getHeight();
		if (aw > bw) return a;
		if (aw < bw) return b;
		if (ah > bh) return a;
		if (ah < bh) return b;
		if (a.getBitDepth() > b.getBitDepth()) return a;
		if (a.getBitDepth() < b.getBitDepth()) return b;
		if (a.getRefreshRate() > b.getRefreshRate()) return a;
		// or b is better or they are identical
		return b;
	}
	
	private boolean displayModesMatch(DisplayMode dm1, DisplayMode dm2) {
		if (dm1.getWidth() != dm2.getWidth() || dm1.getHeight() != dm2.getHeight()) {
			return false;
		}
		if (dm1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && dm2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && dm1.getBitDepth() != dm2.getBitDepth()) {
			return false;
		}
		if (dm1.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN && dm2.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN && dm1.getRefreshRate() != dm2.getRefreshRate()) {
			return false;
		}
		return true;
	}
	
	/** EVEN BORINGER STUFF **/

	/*
	public unused DisplayMode getCurrentDisplayMode() {
		return videoCard.getDisplayMode();
	}
	*/
	
	/*
	//create image compatible with monitor
	public BufferedImage createCompatibleImage(int width, int height, int transparency) {
		Window w = videoCard.getFullScreenWindow();	
		if (w != null) {
			GraphicsConfiguration gc = w.getGraphicsConfiguration(); // current monitor settings
			return gc.createCompatibleImage(width, height, transparency);
		}
		return null;
	}
	*/
	
}

