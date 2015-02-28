package br.com.hamsterpellet.boxreflex;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import br.com.hamsterpellet.fullscreen.GamePanel;
import br.com.hamsterpellet.fullscreen.ScreenPage;
import br.com.hamsterpellet.fullscreen.ScreenPage.OneDimPosition;
import br.com.hamsterpellet.fullscreen.UserEventHandler;
import br.com.hamsterpellet.fullscreen.ScreenPage.MouseStatus;
import br.com.hamsterpellet.fullscreen.region.Debugger;
import br.com.hamsterpellet.fullscreen.region.ScreenColoredCursoredRect;
import br.com.hamsterpellet.fullscreen.region.ScreenRect;

public class MyUserEventHandler extends UserEventHandler {

	/** ################################ **/
	/** #### PUBLIC STATIC VOID MAIN ### **/
	
	public static void main(String[] args) {
		//JOptionPane.showMessageDialog(null, "NEEDS TO FIX ROOT - ROOT SCREEN RECT IS A PROPERTY OF SCREENPAGE!");
		//if (Math.random() < 2) return;
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
			
			ScreenColoredCursoredRect[][] cells = new ScreenColoredCursoredRect[5][5];
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					cells[i][j] = ScreenColoredCursoredRect.create(0.1, 0.1, null, Color.BLUE, Color.YELLOW, Color.RED);
					cells[i][j].setFillColor(new Color(25*(i+j), 25*(i+j), 25*(i+j)));
					cells[i][j].setFillColor(new Color(255, 0, 0), MouseStatus.HOVER);
				}
			}
			ScreenRect aaa = ScreenColoredCursoredRect.create(0.25, 0.1, null, Color.BLUE, Color.YELLOW, Color.YELLOW);
			ScreenRect bbb = ScreenColoredCursoredRect.create(0.25, 0.1, null, Color.RED, Color.YELLOW, Color.YELLOW);
			ScreenRect ccc = ScreenColoredCursoredRect.create(0.25, 0.1, null, Color.GREEN, Color.YELLOW, Color.YELLOW);
			ScreenRect ddd = ScreenColoredCursoredRect.create(0.25, 0.1, null, Color.CYAN, Color.YELLOW, Color.YELLOW);
			ScreenRect eee = ScreenColoredCursoredRect.create(0.25, 0.1, null, Color.ORANGE, Color.YELLOW, Color.YELLOW);
			
			ScreenRect table = ScreenRect.makeColumnTable(null, aaa, bbb, ccc, ddd, eee);
			index.addRegion(table);
			table.setPosition(OneDimPosition.CENTER, OneDimPosition.CENTER);
			
			GamePanel.launch(index);
			
			// please avoid doing anything after calling launchGame()
			//   because it might bug the focus of the fullscreened window
		} catch (GamePanel.UnableToFullscreenException e) {
			JOptionPane.showMessageDialog(null, "Não foi possível entrar em modo fullscreen!");
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
