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
	
	/** FIELDS AND CONSTRUCTOR **/
	
	private JFrame frame;
	private final JPanel panel;
	private final DisplayMode displayMode;
	private final ScreenManager screenManager;

	private Cursor transparentCursor;
	private final Color backgroundColor;
	private final int keyboardKeyToEndProgram;
	private final int FPS;
	
	private ScreenPage currentScreenPage;

	private final HashMap<Integer, KeyState> keyPressedHashMap;
	private Point mouseLastLoc;
	
	private boolean running;
	private final Thread thread;
	
	private static GamePanel singleton = null;
	
	private GamePanel(int FPS, java.awt.Color backgroundColor, int keyEventToClose) throws UnableToFullscreenException {		
		ScreenManager screenManager = new ScreenManager();
		this.displayMode = screenManager.findBestCompatibleDisplayMode(displayModesList);
		if (this.displayMode == null) {
			throw new UnableToFullscreenException();
		}
		
		this.panel = new JPanel();
		this.screenManager = screenManager;
		this.backgroundColor = backgroundColor;
		this.FPS = FPS;
		this.keyboardKeyToEndProgram = keyEventToClose;
		

		this.thread = new Thread(this);
		this.running = false;
		this.mouseLastLoc = null;
		this.keyPressedHashMap = new HashMap<Integer, KeyState>();
	}
	
	/** LAUNCH **/
	
	public static void prepareLaunch(int FPS, java.awt.Color backgroundColor, int keyEventToClose) throws UnableToFullscreenException {
		if (singleton != null) {
			throw new RuntimeException("Attempted to prepare launch more than once!");
		}
		/** TEMPORARY ONLY **/
		if (keyEventToClose == KeyEvent.VK_UNDEFINED /* a.k.a. zero */) {
			keyEventToClose = KeyEvent.VK_ESCAPE;
		}
		/** END TEMPORARY ONLY **/
		singleton = new GamePanel(FPS, backgroundColor, keyEventToClose);
	}
	public static void launch(ScreenPage firstPage) {
		if (singleton == null) throw new FullscreenNotPreparedException();
		singleton.frame = singleton.screenManager.setFullScreen(singleton.displayMode, singleton.panel);
		singleton.transparentCursor = singleton.frame.getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null);
		singleton.currentScreenPage = firstPage;
		singleton.thread.start();
	}
	
	public static void switchPage(ScreenPage newPage) {
		if (!isRunning()) throw new FullscreenNotLaunchedException();
		singleton.currentScreenPage = newPage;
	}
	
	public static boolean isRunning() {
		return singleton != null && singleton.running;
	}
	
	/** OTHER ACTIONS **/

	public static ScreenPage getActivePage() {
		if (!isRunning()) throw new FullscreenNotLaunchedException();		
		return singleton.currentScreenPage;
	}
	
	public static void hideCursor() {
		if (!isRunning()) throw new FullscreenNotLaunchedException();
		singleton.frame.setCursor(singleton.transparentCursor);
	}
	public static void setCursor(int whichCursor) { // int Cursor.SOMETHING
		/* Examples:
		 * Cursor.HAND_CURSOR
		 * Cursor.DEFAULT_CURSOR
		 * Cursor.CROSSHAIR_CURSOR
		 */
		if (!isRunning()) throw new FullscreenNotLaunchedException();
		singleton.frame.setCursor(Cursor.getPredefinedCursor(whichCursor));
	}
	public static void setCursor(Cursor whichCursor) {
		if (!isRunning()) throw new FullscreenNotLaunchedException();
		singleton.frame.setCursor(whichCursor);
	}
	
	public static DisplayMode getDisplayMode() {
		if (singleton == null) throw new FullscreenNotPreparedException();
		return singleton.displayMode;
	}
	
	public static Point getMouseLocation() {
		if (!isRunning()) throw new FullscreenNotLaunchedException();
		return singleton.mouseLastLoc;
	}
	
	public static int getScreenWidth() {
		if (singleton == null) throw new FullscreenNotPreparedException();
		return singleton.displayMode.getWidth();
	}
	public static int getScreenHeight() {
		if (singleton == null) throw new FullscreenNotPreparedException();
		return singleton.displayMode.getHeight();
	}
	
	public static Point percentToScreenPoint(double x, double y) {
		if (singleton == null) throw new FullscreenNotPreparedException();
		return new Point((int) (x * singleton.displayMode.getWidth()),
				(int) (y * singleton.displayMode.getHeight()));
	}
	
	public static void exit() {
		exit(false);
	}
	public static void exit(boolean lockThreadUntilDone) {
		// the call to exit(true) only returns after all UserEventHandlerInterface.onExit() events
		// are completely processed. If those are locked as well, this will remain locked.
		if (!isRunning()) throw new FullscreenNotLaunchedException();
		singleton.running = false;
		while (lockThreadUntilDone && singleton.thread.isAlive());
	}
	
	/** Everything from now on is forcefully public but consider them private to the outside **/
	
	/** ################################################# **/
	/** ################# THREAD THING ################## **/
	/** ################################################# **/
	
	public void run() {
		screenManager.getFullScreenWindow().addKeyListener(this);
		screenManager.getFullScreenWindow().addMouseListener(this);
		screenManager.getFullScreenWindow().addMouseMotionListener(this);
		screenManager.getFullScreenWindow().addMouseWheelListener(this);
		running = true;
		
		long startTimeNanos;
        long frameUpdateTimeMillis;
        long waitTime;

        boolean normalExit = true;
        try {
        	
	        while (running) {
	        	
	            startTimeNanos = System.nanoTime();
	            Graphics2D bufferGraphics = screenManager.getGraphics();
	            
	            // Update
	            if (currentScreenPage.wasJustBorn()) {
	            	currentScreenPage.getUserEventHandler()._onInit(bufferGraphics);
	            	currentScreenPage.unsetJustBorn();
	            } else {
	            	currentScreenPage.getUserEventHandler()._onUpdate();
	            }
	            
	            // Draw on buffer (a.k.a. render)
	            bufferGraphics.setColor(backgroundColor);
	            bufferGraphics.fillRect(0, 0, displayMode.getWidth(), displayMode.getHeight());
	            currentScreenPage.paint(bufferGraphics);
	            
	            currentScreenPage.getUserEventHandler()._onRender(bufferGraphics);
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
	            }
	            
	        }
	        // normalExit = true; // not needed anymore because added it in the variable declaration
        } catch (Exception e) {
        	normalExit = false;
        } finally {
        	screenManager.exitFullScreen();
        	if (normalExit) {
        		currentScreenPage.getUserEventHandler()._onNormalExit();        		
        	} else {
        		currentScreenPage.getUserEventHandler()._onErrorExit();        		
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
        	consume = currentScreenPage.getUserEventHandler()._onKeyPressed(String.valueOf(key.getKeyChar()), keyPressedCode); 
    	} else if (keyState == KeyState.PRESSED) {
       		//It was just re-pressed, aka held
   			keyPressedHashMap.put(keyPressedCode, KeyState.HELD);
       		consume = currentScreenPage.getUserEventHandler()._onKeyHeld(String.valueOf(key.getKeyChar()), keyPressedCode);
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
    	
    	boolean consume = currentScreenPage.getUserEventHandler()._onKeyReleased(String.valueOf(key.getKeyChar()), key.getKeyCode());
    	boolean newconsume;
    	
    	if (oldKeyState == KeyState.PRESSED) {
    		newconsume = currentScreenPage.getUserEventHandler()._onKeyClicked(String.valueOf(key.getKeyChar()), key.getKeyCode());
    	} else {
    		newconsume = currentScreenPage.getUserEventHandler()._onKeyUnheld(String.valueOf(key.getKeyChar()), key.getKeyCode());
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
			consume = currentScreenPage.getUserEventHandler()._onMouseLeftButtonPressed(panel.getMousePosition());
		}
		if (i == MouseEvent.BUTTON2) {
			consume = currentScreenPage.getUserEventHandler()._onMouseWheelPressed(panel.getMousePosition());
		}
		if (i == MouseEvent.BUTTON3) {
			consume = currentScreenPage.getUserEventHandler()._onMouseRightButtonPressed(panel.getMousePosition());
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
			consume = currentScreenPage.getUserEventHandler()._onMouseLeftButtonReleased(panel.getMousePosition());
		}
		if (i == MouseEvent.BUTTON2) {
			consume = currentScreenPage.getUserEventHandler()._onMouseWheelReleased(panel.getMousePosition());
		}
		if (i == MouseEvent.BUTTON3) {
			consume = currentScreenPage.getUserEventHandler()._onMouseRightButtonReleased(panel.getMousePosition());
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
		currentScreenPage.getUserEventHandler()._onMouseMoved(tempPoint, mouseLastLoc, distance, dragged);
		mouseLastLoc = tempPoint;
	}

	// MouseWheelListener
	public synchronized void mouseWheelMoved(MouseWheelEvent e) {
    	if (!running) {
    		return;
    	}
    	
		boolean consume = false;
		if (e.getWheelRotation() < 0) {
			consume = currentScreenPage.getUserEventHandler()._onMouseWheelMovedUp(e.getPoint());
		} else if (e.getWheelRotation() > 0) {
			consume = currentScreenPage.getUserEventHandler()._onMouseWheelMovedDown(e.getPoint());
		}
		if (consume) {
			e.consume();
		}
	}
	
	/** ################################################# **/

	public static final class UnableToFullscreenException extends Exception {}
	public static final class FullscreenNotPreparedException extends RuntimeException {}
	public static final class FullscreenNotLaunchedException extends RuntimeException {}
	
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
