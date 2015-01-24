package br.com.hamsterpellet.fullscreen;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	
	private final JFrame frame;
	private final JPanel panel;
	private final DisplayMode displayMode;
	private final ScreenManager screenManager;
	
	private final UserEventHandler userEventHandler;

	private final Cursor transparentCursor;
	private final Color backgroundColor;
	private final int keyboardKeyToEndProgram;
	private final int FPS;

	private final HashMap<Integer, KeyState> keyPressedHashMap;
	private Point mouseLastLoc;
	
	private boolean running;
	private final Thread thread;
	
	public static void launchGame(int FPS, UserEventHandler ueHandler, java.awt.Color backgroundColor, int keyEventToClose) throws FullscreenException {
		/** TEMPORARY ONLY **/
		if (keyEventToClose == KeyEvent.VK_UNDEFINED /* a.k.a. zero */) {
			keyEventToClose = KeyEvent.VK_ESCAPE;
		}
		/** END TEMPORARY ONLY **/
		GamePanel g = new GamePanel(new JPanel(), FPS, ueHandler, backgroundColor, keyEventToClose);
		ueHandler.setGamePanel(g);
		g.thread.start();
	}
	
	private GamePanel(JPanel panel, int FPS, UserEventHandler ueHandler, java.awt.Color backgroundColor, int keyEventToClose) throws FullscreenException {
		if (FPS <= 0) {
			throw new InvalidFPSException();
		}
		
		ScreenManager screenManager = new ScreenManager();
		this.displayMode = screenManager.findBestCompatibleDisplayMode(displayModesList);
		if (this.displayMode == null) {
			throw new FullscreenException();
		}
		
		
		this.frame = screenManager.setFullScreen(displayMode, panel);
		this.panel = panel;
		this.userEventHandler = ueHandler;
		this.screenManager = screenManager;
		this.backgroundColor = backgroundColor;
		this.FPS = FPS;
		this.transparentCursor = frame.getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null);
		this.keyboardKeyToEndProgram = keyEventToClose;
		

		this.thread = new Thread(this);
		this.running = false;
		this.mouseLastLoc = null;
		this.keyPressedHashMap = new HashMap<Integer, KeyState>();
	}
	
	public void exit() {
		exit(false);
	}
	public void exit(boolean lockThreadUntilDone) {
		// the call to exit(true) only returns after all UserEventHandlerInterface.onExit() events
		// are completely processed. If those are locked as well, this will remain locked.
		this.running = false;
		while (lockThreadUntilDone && this.thread.isAlive());
	}
	
	/** CURSOR STUFF **/

	public void hideCursor() {
		frame.setCursor(transparentCursor);
	}
	public void setCursor(int whichCursor) { // int Cursor.SOMETHING
		/* Examples:
		 * Cursor.HAND_CURSOR
		 * Cursor.DEFAULT_CURSOR
		 * Cursor.CROSSHAIR_CURSOR
		 */
		frame.setCursor(Cursor.getPredefinedCursor(whichCursor));
	}
	public void setCursor(Cursor whichCursor) {
		frame.setCursor(whichCursor);
	}
	
	/** GETTERS **/
	
	public int getScreenCoordinateX(double percentageX) {
		return (int) (percentageX * displayMode.getWidth());
	}
	public int getScreenCoordinateY(double percentageY) {
		return (int) (percentageY * displayMode.getHeight());
	}
	public Point getScreenCoordinates(double percentageX, double percentageY) {
		return new Point(getScreenCoordinateX(percentageX), getScreenCoordinateY(percentageY));
	}
	
	public DisplayMode getDisplayMode() {
		return this.displayMode;
	}
	public Point getMouseLocation() {
		return this.mouseLastLoc;
	}
	public boolean isRunning() {
		return this.running;
	}
	
	/** Everything from now on is forcefully public but consider them private to the outside **/
	
	/** ################################################# **/
	/** ################# THREAD THING ################## **/
	/** ################################################# **/
	
	public void run() {
		if (!userEventHandler.isReady()) {
			throw new UserEventHandler.UEHandlerNotReadyException();
		}
		
		screenManager.getFullScreenWindow().addKeyListener(this);
		screenManager.getFullScreenWindow().addMouseListener(this);
		screenManager.getFullScreenWindow().addMouseMotionListener(this);
		screenManager.getFullScreenWindow().addMouseWheelListener(this);
		running = true;
		boolean firstTime = true;
		
		long startTimeNanos;
        long frameUpdateTimeMillis;
        long waitTime;

        boolean normalExit = true;
        try {
        	
	        while (running) {
	        	
	            startTimeNanos = System.nanoTime();
	            Graphics2D bufferGraphics = screenManager.getGraphics();
	            
	            // Update
	            if (firstTime) {
	        		userEventHandler._onInit(bufferGraphics);
	            } else {
	            	userEventHandler._onUpdate();
	            }
	            
	            // Draw on buffer (a.k.a. render)
	            bufferGraphics.setColor(backgroundColor);
	            bufferGraphics.fillRect(0, 0, displayMode.getWidth(), displayMode.getHeight());
	            userEventHandler.getScreenPage().paint(bufferGraphics);
	            
	            userEventHandler._onRender(bufferGraphics);
	            // Actually draw
	            screenManager.drawFromBuffer();
	            bufferGraphics.dispose();

	            frameUpdateTimeMillis = (System.nanoTime() - startTimeNanos) / 1000000;
	            waitTime = (1000 / this.FPS) - frameUpdateTimeMillis;
	
	            try {
	            	if (waitTime > 0) {
	            		Thread.sleep(waitTime);
	            	}
	            } catch (Exception e) {
	            	e.printStackTrace();
	            } finally {
	            	firstTime = false;
	            }
	            
	        }
	        // normalExit = true; // not needed anymore because added it in the variable declaration
        } catch (Exception e) {
        	normalExit = false;
        } finally {
        	screenManager.exitFullScreen();
        	if (normalExit) {
    	        userEventHandler._onNormalExit();        		
        	} else {
    	        userEventHandler._onErrorExit();        		
        	}
        }
	}
	
	/** ################################################# **/
	/** ####### LISTENERS DIRECT IMPLEMENTATIONS ######## **/
	/** ################################################# **/
	
	// KeyListener
	public void keyTyped(KeyEvent key) {}
	public synchronized void keyPressed(KeyEvent key) {
		if (!running) return;
    	
    	int keyPressedCode = key.getKeyCode();
    	if (keyPressedCode == keyboardKeyToEndProgram) {
    		// End Program!
    		key.consume();
    		exit();
    		return;
    	}
    	
    	boolean consume = true;
    	KeyState keyState = keyPressedHashMap.get(keyPressedCode);
    	if (keyState == null || keyState == KeyState.RELEASED) {
    		//It was pressed now!
        	keyPressedHashMap.put(keyPressedCode, KeyState.PRESSED);
        	consume = userEventHandler._onKeyPressed(String.valueOf(key.getKeyChar()), keyPressedCode); 
    	} else if (keyState == KeyState.PRESSED) {
       		//It was just re-pressed, aka held
   			keyPressedHashMap.put(keyPressedCode, KeyState.HELD);
       		consume = userEventHandler._onKeyHeld(String.valueOf(key.getKeyChar()), keyPressedCode);
    	} // else if it's held, do nothing (just consume) -- no need for the else because it's obvious
    	if (consume) {
    		key.consume();
    	}
    }
    public synchronized void keyReleased(KeyEvent key) {
    	if (!running) return;
    	
    	int keyPressedCode = key.getKeyCode();
    	
    	KeyState oldKeyState = keyPressedHashMap.get(keyPressedCode);
    	keyPressedHashMap.put(keyPressedCode, KeyState.RELEASED);
    	
    	if (oldKeyState == KeyState.RELEASED) {
    		key.consume();
    		throw new RuntimeException("There must be some keyboard bug because how can a key be released twice in a row?");
    	}
    	

    	// there are two methods to tell if must consume or not.
    	// if one of them says to consume, then consume.
    	
    	boolean consume = userEventHandler._onKeyReleased(String.valueOf(key.getKeyChar()), key.getKeyCode());
    	boolean newconsume;
    	
    	if (oldKeyState == KeyState.PRESSED) {
    		newconsume = userEventHandler._onKeyClicked(String.valueOf(key.getKeyChar()), key.getKeyCode());
    	} else {
    		newconsume = userEventHandler._onKeyUnheld(String.valueOf(key.getKeyChar()), key.getKeyCode());
    	}
    	
    	if (consume || newconsume) {
    		key.consume();
    	}
    }
    // Helper Enum
    private static enum KeyState {
    	PRESSED, HELD, RELEASED;
    }
	
    // MouseListener
	public synchronized void mousePressed(MouseEvent e) {
    	if (!running) {
    		return;
    	}
    	
		int i = e.getButton();
		boolean consume = false;
		if (i == MouseEvent.BUTTON1) {
			consume = userEventHandler._onMouseLeftButtonPressed(panel.getMousePosition());
		}
		if (i == MouseEvent.BUTTON2) {
			consume = userEventHandler._onMouseWheelPressed(panel.getMousePosition());
		}
		if (i == MouseEvent.BUTTON3) {
			consume = userEventHandler._onMouseRightButtonPressed(panel.getMousePosition());
		}
		if (consume) {
			e.consume();
		}
	}
	public synchronized void mouseReleased(MouseEvent e) {
    	if (!running) {
    		return;
    	}
		int i = e.getButton();
		boolean consume = false;
		if (i == MouseEvent.BUTTON1) {
			consume = userEventHandler._onMouseLeftButtonReleased(panel.getMousePosition());
		}
		if (i == MouseEvent.BUTTON2) {
			consume = userEventHandler._onMouseWheelReleased(panel.getMousePosition());
		}
		if (i == MouseEvent.BUTTON3) {
			consume = userEventHandler._onMouseRightButtonReleased(panel.getMousePosition());
		}
		if (consume) {
			e.consume();
		}
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

    // MouseMotionListener
	public synchronized void mouseDragged(MouseEvent e) {
		mouseMoved(e, true);
	}
	public synchronized void mouseMoved(MouseEvent e) {
		mouseMoved(e, false);
	}
	// Helper Function
	public synchronized void mouseMoved(MouseEvent e, boolean dragged) {
    	if (!running) return;
    	
		Point tempPoint = e.getPoint();
		double distance = 0;
		if (mouseLastLoc == null) {
			mouseLastLoc = tempPoint;
		} else {
			distance = Point.distance(tempPoint.x, tempPoint.y, mouseLastLoc.x, mouseLastLoc.y);
		}
		userEventHandler._onMouseMoved(tempPoint, mouseLastLoc, distance, dragged);
		mouseLastLoc = tempPoint;
	}

	// MouseWheelListener
	public synchronized void mouseWheelMoved(MouseWheelEvent e) {
    	if (!running) {
    		return;
    	}
    	
		boolean consume = false;
		if (e.getWheelRotation() < 0) {
			consume = userEventHandler._onMouseWheelMovedUp(e.getPoint());
		} else if (e.getWheelRotation() > 0) {
			consume = userEventHandler._onMouseWheelMovedDown(e.getPoint());
		}
		if (consume) {
			e.consume();
		}
	}
	
	/** ################################################# **/

	public static final class FullscreenException extends Exception {}
	public static final class InvalidFPSException extends RuntimeException {}
	
	private static final DisplayMode[] displayModesList = {
		new DisplayMode(1366, 768, 32, 0),
		new DisplayMode(1366, 768, 24, 0),
		new DisplayMode(1366, 768, 16, 0),
		
		new DisplayMode(1280, 600, 32, 0),
		new DisplayMode(1280, 600, 16, 0),
		
		new DisplayMode(1280, 720, 32, 0),
		new DisplayMode(1280, 720, 16, 0),
		
		new DisplayMode(1280, 768, 32, 0),
		new DisplayMode(1280, 768, 24, 0),
		new DisplayMode(1280, 768, 16, 0),
		
		new DisplayMode(1024, 768, 32, 0),
		new DisplayMode(1024, 768, 24, 0),
		new DisplayMode(1024, 768, 16, 0),
		
		new DisplayMode(800, 600, 32, 0),
		new DisplayMode(800, 600, 24, 0),
		new DisplayMode(800, 600, 16, 0),
		
		new DisplayMode(640, 480, 32, 0),
		new DisplayMode(640, 480, 16, 0),
		
		new DisplayMode(640, 400, 32, 0),
		new DisplayMode(640, 400, 16, 0)
	};
	
}
