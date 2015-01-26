package br.com.hamsterpellet.boxreflex;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JOptionPane;

import br.com.hamsterpellet.fullscreen.GamePanel;
import br.com.hamsterpellet.fullscreen.ScreenPage;
import br.com.hamsterpellet.fullscreen.ScreenPage.OneDimPosition;
import br.com.hamsterpellet.fullscreen.UserEventHandler;
import br.com.hamsterpellet.fullscreen.region.Debugger;
import br.com.hamsterpellet.fullscreen.region.ScreenColoredCursoredRect;
import br.com.hamsterpellet.fullscreen.region.ScreenImageRect;

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

			GamePanel.prepareLaunch(30, Color.BLACK, KeyEvent.VK_ESCAPE);
			
			ScreenPage index = new ScreenPage(handler);
			
			ScreenImageRect exitButton = ScreenImageRect.create(null, "alea_jacta_est.bmp", true);
			exitButton.setMouseClickListener(new Runnable() {
				@Override
				public void run() {
					GamePanel.exit();
				}
			});
			exitButton.setHoverCursor(Cursor.HAND_CURSOR);
			exitButton.setPosition(OneDimPosition.BEFORE_CENTER, OneDimPosition.BEFORE_CENTER);
			index.addRegion(exitButton);
			
			ScreenImageRect exitButton2 = ScreenImageRect.create(null, "alea_jacta_est.bmp", true);
			exitButton2.setHoverCursor(Cursor.CROSSHAIR_CURSOR);
			exitButton2.setPosition(OneDimPosition.AFTER_CENTER, OneDimPosition.AFTER_CENTER);
			index.addRegion(exitButton2);
	
			final ScreenPage newp = new ScreenPage(handler);
			ScreenColoredCursoredRect tt = ScreenColoredCursoredRect.create(0.5, 0.5, null, Color.BLUE, Color.GREEN, Color.WHITE);
			tt.setPosition(0.1, 0.1);
			tt.setMouseClickListener(new Runnable() {
				@Override
				public void run() {
					handler.getDebugger().addMessage("YAYY", 3);
				}
			});
			newp.addRegion(tt);

			exitButton2.setMouseClickListener(new Runnable() {
				@Override
				public void run() {
					GamePanel.switchPage(newp);
				}
			});
			
			GamePanel.launch(index);
			
			// please avoid doing anything after calling launchGame()
			//   because it might bug the focus of the fullscreened window
		} catch (GamePanel.UnableToFullscreenException e) {
			JOptionPane.showMessageDialog(null, "Não foi possível entrar em modo fullscreen!");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// please avoid doing anything after calling launchGame()
		//   because it might bug the focus of the fullscreened window
	}
	
	/** ################################ **/
	/** GAME CONTROLLING IMPLEMENTATIONS **/
	
	@Override
	public void onInit(Graphics2D g) {
		getDebugger().addMessage("yay", 3);
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
