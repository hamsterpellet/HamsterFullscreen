

import hamsterFullscreen.GamePanel;
import hamsterFullscreen.UserEventHandler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

public class MyUserEventHandler extends UserEventHandler {

	/** ################################ **/
	/** #### PUBLIC STATIC VOID MAIN ### **/
	
	public static void main(String[] args) {
		JOptionPane.showMessageDialog(null, "Pressione OK quando estiver pronto para entrar no modo fullscreen.");
		try {
			final MyUserEventHandler handler = new MyUserEventHandler();
			
			Debugger d = new Debugger() {
				@Override
				protected void showMessage(String message, String messagePool) {
					System.out.println(message + " [[" + System.currentTimeMillis() % 200000 + "]]");
				}
				@Override
				protected void renderMessage(final Graphics2D g, String messagePool) {
					g.setColor(Color.RED);
					g.drawString(messagePool + " [[" + System.currentTimeMillis() % 200000 + "]]", 20, 20);					
				}
			};
			d.setDebugLevel(2);
			d.disableAutoClear();
			handler.setDebugger(d);
			
			GamePanel.launchGame(30, handler, Color.BLACK, KeyEvent.VK_ESCAPE);
			// please avoid doing anything after calling launchGame()
			//   because it might bug the focus of the fullscreened window
		} catch (GamePanel.FullscreenException e) {
			JOptionPane.showMessageDialog(null, "Não foi possível entrar em modo fullscreen!");
		}
		// please avoid doing anything after calling launchGame()
		//   because it might bug the focus of the fullscreened window
	}
	
	/** ################################ **/
	/** GAME CONTROLLING IMPLEMENTATIONS **/

	private Game game;
	
	@Override
	public void onInit(Graphics2D g) {
		game = Game.start(getScreenWidth(), getScreenHeight(), getDebugger());
		setScreenPage(game.getPage());
	}

	@Override
	public void onUpdate() {
		// do something before every screen render but the first (which is onInit() instead)
		// (which happens FPS times per second)
	}

	@Override
	public void onRender(final Graphics2D g) {
	}

	@Override
	public void onNormalExit() {
		JOptionPane.showMessageDialog(null, "Até mais!");
	}

	@Override
	public void onErrorExit() {
		JOptionPane.showMessageDialog(null, "Houve um erro ao fechar o programa!");
	}
	
	/** KEYBOARD EVENTS IMPLEMENTATIONS **/

	@Override
	public boolean onKeyPressed(String text, int keyCode) {
		return true;
	}

	@Override
	public boolean onKeyHeld(String text, int keyCode) {
		return true;
	}

	@Override
	public boolean onKeyReleased(String text, int keyCode) {
		return true;
	}
	
	@Override
	public boolean onKeyClicked(String text, int keyCode) {
		if (keyCode == KeyEvent.VK_SPACE) {
			Box activeBox = game.getActiveBox();
			if (activeBox != null) {
				game.unHighlightOnBox(activeBox.x, activeBox.y);
				game.changePattern();
				game.highlightOnBox(activeBox.x, activeBox.y);
			} else {
				game.changePattern();
			}
		}
		return true;
	}

	@Override
	public boolean onKeyUnheld(String text, int keyCode) {
		return true;
	}
	
	/** MOUSE EVENTS IMPLEMENTATIONS **/

	@Override
	public boolean onMouseLeftButtonPressed(final Point mouseLoc) {
		return false;
	}

	@Override
	public boolean onMouseLeftButtonReleased(final Point mouseLoc) {
		return false;
	}

	@Override
	public boolean onMouseRightButtonPressed(final Point mouseLoc) {
		return false;
	}

	@Override
	public boolean onMouseRightButtonReleased(final Point mouseLoc) {
		return false;
	}

	@Override
	public boolean onMouseWheelPressed(final Point mouseLoc) {
		return false;
	}

	@Override
	public boolean onMouseWheelReleased(final Point mouseLoc) {
		return false;
	}

	@Override
	public void onMouseMoved(final Point mouseCurrentLoc, final Point mouseLastLoc, double distance, boolean dragged) {}

	@Override
	public boolean onMouseWheelMovedUp() {
		return false;
	}

	@Override
	public boolean onMouseWheelMovedDown() {
		return false;
	}
	
}
