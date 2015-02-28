package br.com.hamsterpellet.boxreflex;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JOptionPane;

import br.com.hamsterpellet.fullscreen.GamePanel;
import br.com.hamsterpellet.fullscreen.GamePanel.UnableToFullscreenException;
import br.com.hamsterpellet.fullscreen.ScreenPage;
import br.com.hamsterpellet.fullscreen.ScreenPage.MouseStatus;
import br.com.hamsterpellet.fullscreen.ScreenPage.OneDimPosition;
import br.com.hamsterpellet.fullscreen.UserEventHandler;
import br.com.hamsterpellet.fullscreen.region.Debugger;
import br.com.hamsterpellet.fullscreen.region.ScreenImageRect;
import br.com.hamsterpellet.fullscreen.region.ScreenRect;

public final class Main {
	
	private static UserEventHandler handler;
	private static ScreenPage firstPage;
	private static ScreenPage optionsPage;
	
	public static void main(String[] args) {
		
		JOptionPane.showMessageDialog(null, "Pressione OK quando estiver pronto para entrar no modo fullscreen.");

		try {
			GamePanel.prepareLaunch(30, Color.BLACK, KeyEvent.VK_ESCAPE);
		} catch (UnableToFullscreenException e) {
			JOptionPane.showMessageDialog(null, "Não foi possível entrar em modo fullscreen!");
			return;
		}
		
		try {
			Main.init();
		} catch (/*MalformedURLException |*/ IOException e) {
			String error = "Alguns arquivos necessários não foram encontrados! A mensagem de erro foi:\n\n";
			error += e.toString();
			JOptionPane.showMessageDialog(null, error);
			return;
		}
		
		GamePanel.launch(Main.firstPage);
			
		// remember that doing anything after launch() can mess the fullscreen focus!
		
	}
	
	public static void init() throws MalformedURLException, IOException {
		final UserEventHandler handler;
		/* CREATE HANDLER */ {
			handler = new MyUserEventHandler();
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
			d.setDebugMinimumLevel(1);
			d.disableAutoClear();
			handler.setDebugger(d);
		}
		
		final ScreenPage firstPage = new ScreenPage(handler);
		final ScreenPage optionsPage = new ScreenPage(handler);
		
		/* CREATE FIRST PAGE */ {
			ScreenImageRect title = ScreenImageRect.create(null, "images/saturamini.png", true);
			title.setPosition(OneDimPosition.CENTER, OneDimPosition.BEGIN);
			title.move(0, 0.05);
			firstPage.addRegion(title);
			
			ScreenImageRect[] menu = new ScreenImageRect[4];
			String[] menuSrcs = new String[4];
			menuSrcs[0] = "images/iniciar.png";
			menuSrcs[1] = "images/continuarjogosalvo.png";
			menuSrcs[2] = "images/opcoes.png";
			menuSrcs[3] = "images/sair.png";
			for (int i = 0; i < 4; i++) {
				menu[i] = ScreenImageRect.create(null, menuSrcs[i]);
				menu[i].setFillColor(Color.RED, MouseStatus.NORMAL);
				menu[i].setHoverCursor(Cursor.HAND_CURSOR);
				menu[i].setFillColor(Color.BLUE, MouseStatus.HOVER);
				menu[i].setFillColor(Color.WHITE, MouseStatus.PRESSED);
			}
			menu[2].setMouseClickListener(new Runnable() {
				@Override
				public void run() {
					GamePanel.switchPage(optionsPage);
				}
			});
			menu[3].setMouseClickListener(new Runnable() {
				@Override
				public void run() {
					GamePanel.exit();
				}
			});
			
			ScreenRect table = ScreenRect.makeColumnTable(null, 0.015, menu);
			table.setPosition(OneDimPosition.CENTER, OneDimPosition.CENTER);
			table.move(0, 0.1);
			firstPage.addRegion(table);
		}
		
		/* CREATE OPTIONS PAGE */ {			
			/* it would be possible to just use optionsPage.addRegion(title) directly,
			 * with the other 'title' rect made on the CREATE FIRST PAGE BLOCK, but
			 * I preferred to separate it for clarity and avoid messing up..
			 * For example, if I move it here, it will move there too :P
			 */
			ScreenImageRect title = ScreenImageRect.create(null, "images/saturamini.png", true);
			title.setPosition(OneDimPosition.CENTER, OneDimPosition.BEGIN);
			title.move(0, 0.05);
			optionsPage.addRegion(title);
			
			ScreenImageRect[] menu = new ScreenImageRect[4];
			String[] menuSrcs = new String[4];
			menuSrcs[0] = "images/notyet.png";
			menuSrcs[1] = "images/calma.png";
			menuSrcs[2] = "images/espera.png";
			menuSrcs[3] = "images/voltar.png";
			for (int i = 0; i < 4; i++) {
				menu[i] = ScreenImageRect.create(null, menuSrcs[i]);
				menu[i].setFillColor(Color.RED, MouseStatus.NORMAL);
				menu[i].setHoverCursor(Cursor.HAND_CURSOR);
				menu[i].setFillColor(Color.BLUE, MouseStatus.HOVER);
				menu[i].setFillColor(Color.WHITE, MouseStatus.PRESSED);
			}
			menu[3].setMouseClickListener(new Runnable() {
				@Override
				public void run() {
					GamePanel.switchPage(firstPage);
				}
			});
			
			ScreenRect table = ScreenRect.makeColumnTable(null, 0.015, menu);
			table.setPosition(OneDimPosition.CENTER, OneDimPosition.CENTER);
			table.move(0, 0.05);
			optionsPage.addRegion(table);
		}
		
		Main.handler = handler;
		Main.firstPage = firstPage;
		Main.optionsPage = optionsPage;
	}

}
