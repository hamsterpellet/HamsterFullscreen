package br.com.hamsterpellet.boxreflex;


import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JOptionPane;

import br.com.hamsterpellet.fullscreen.UserEventHandler;

public class MyUserEventHandler extends UserEventHandler {
	
	/** ################################ **/
	/** GAME CONTROLLING IMPLEMENTATIONS **/
	
	@Override
	public void onInit(Graphics2D g) {
		getDebugger().addMessage("onInit()", 1);
	}

	@Override
	public void onUpdate() {
		// do something before every screen render but the first (which is onInit() instead)
		// (which happens FPS times per second)
	}

	@Override
	public void onBeforeRender(final Graphics2D g) {
		
	}
	
	@Override
	public void onAfterRender(final Graphics2D g) {
		
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
