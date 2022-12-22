package com.TowerDefense.game.entities;

import java.awt.Graphics;

import com.TowerDefense.game.pathFinding.Point2;

public abstract class Entity {
	public Point2 point;
	protected int w,h;
	
	public Entity(Point2 point, int width, int height) {
		this.point = point;
		w = width;
		h = height;
	}
	
	public abstract void tick();
	public abstract void render(Graphics g);
}
