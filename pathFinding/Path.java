package com.TowerDefense.game.pathFinding;

import java.awt.Color;
import java.awt.Graphics;

public class Path {
	public int x,y,l,h;
	
	public Path(Node node, int nodeSize, int length, int height) {
		x = node.x();
		y = node.y();
		l = length*nodeSize;
		h = height*nodeSize;
	}
	
	public Path(int x, int y, int nodeSize, int length, int height) {
		this.x = x;
		this.y = y;
		l = length*nodeSize;
		h = height*nodeSize;
	}
	
	public static boolean contains(Point2 p, Path path) {
		return p.x >= path.x && p.x < path.x+path.l && p.y >= path.y && p.y < path.y+path.h;
	}
	
	public void render(Graphics g) {
		g.setColor(new Color(70,180,70));
		g.fillRect(x, y, l, h);
	}
}
