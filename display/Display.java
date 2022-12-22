package com.TowerDefense.game.display;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Display {
	private JFrame frame;
	private Canvas canvas;
	public static int WIDTH,HEIGHT,SCALE,SHOPWIDTH;
	public static String TITLE;
	
	public Display(String title, int width, int scale) {
		TITLE = title;
		SCALE = scale;
		WIDTH = width*scale;
		HEIGHT = WIDTH*3/4;
		
		init();
	}
	
	private void init() {
		frame = new JFrame(TITLE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		canvas = new Canvas();
		canvas.setMaximumSize(new Dimension(WIDTH,HEIGHT));
		canvas.setMinimumSize(new Dimension(WIDTH,HEIGHT));
		canvas.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		
		frame.add(canvas);
		frame.setVisible(true);
		frame.pack();
	}
	
	public Canvas canvas() {
		return canvas;
	}
	
	public JFrame frame() {
		return frame;
	}
}
