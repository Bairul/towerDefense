package com.TowerDefense.game.inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseMotionListener,MouseListener {
	
	public int x,y;
	public boolean pressed = false,up = true, clicked = false;
	
	public void offClicked() {
		clicked = false;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		//clicked = false;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		pressed = true;
		up = false;
		//clicked = false;
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		up = true;
		clicked = true;
		pressed = false;
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
