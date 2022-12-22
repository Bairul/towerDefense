package com.TowerDefense.game;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import com.TowerDefense.game.display.Display;
import com.TowerDefense.game.inputs.Mouse;
import com.TowerDefense.game.states.GameState;
import com.TowerDefense.game.states.State;

public class Game implements Runnable {
	private Thread thread;
	private boolean running = false;
	
	private Display display;
	private Graphics g;
	private BufferStrategy bs;
	
	private State gameState;
	
	public Mouse mouse;
	
	public int gold = 250, lives = 100;
	
	public synchronized void start() {
		if (running) return;
		
		thread = new Thread(this);
		running = true;
		thread.start();
	}
	
	public synchronized void stop() {
		if (!running) return;
		
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		display = new Display("Tower def", 128,8);
		mouse = new Mouse();
		
		gameState = new GameState(this);
		State.setState(gameState);
		
		display.frame().addMouseListener(mouse);
		display.frame().addMouseMotionListener(mouse);
		display.canvas().addMouseListener(mouse);
		display.canvas().addMouseMotionListener(mouse);
	}

	@Override
	public void run() {
		init();
		
		int fps = 60;
		double nsPerFrame = 1000000000/fps;
		double delta = 0;
		long present;
		long past = System.nanoTime();
		
		while (running) {
			present = System.nanoTime();
			delta += (present - past)/nsPerFrame;
			past = present;
			
			while (delta >= 1) {
				tick();				
				delta--;
			}
			render();
			try {
				thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void tick() {
		if (State.getState() != null)
			State.getState().tick();
	}

	private void render() {
		bs = display.canvas().getBufferStrategy();
		
		if (bs == null) {
			display.canvas().createBufferStrategy(3);
			return;
		}
		
		g = bs.getDrawGraphics();
		g.clearRect(0, 0, Display.WIDTH, Display.HEIGHT);		
		
		if (State.getState() != null)
			State.getState().render(g);
		
		bs.show();
		g.dispose();
	}
	
	public void setHandCursor() {
		display.frame().setCursor(new Cursor(Cursor.HAND_CURSOR));
	}
	
	public void setDefCursor() {
		display.frame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
}
