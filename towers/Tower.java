package com.TowerDefense.game.towers;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Queue;

import com.TowerDefense.game.display.Display;
import com.TowerDefense.game.entities.Enemy;
import com.TowerDefense.game.entities.EntityManager;
import com.TowerDefense.game.pathFinding.Point2;

public abstract class Tower {
	public Point2 point;
	public int w,h,range,buttonSize,price,sellPrice,stunTimer = 0,stunDur = 210;
	protected EntityManager entityManager;
	protected boolean isClicked,maxTier;
	public Point2[] uButtons;
	public boolean[] upgradePath;
	protected int margin,upgradeCount;
	public boolean auto = false, stunned = false;
	
	public Queue<Enemy> targets;
	
	public Tower(Point2 point, EntityManager entityManager,int width, int height, int shopWidth,int range,int price) {
		this.point = point;
		this.entityManager = entityManager;
		targets = new LinkedList<Enemy>();
		uButtons = new Point2[4];
		upgradePath = new boolean[15];
		w = width;
		h = height;
		this.range = range;
		this.price = price;
		buttonSize = shopWidth-50;
		margin = shopWidth/15;
		upgradeCount = 0;
		
		for (int i=0; i<3; i++) {
			uButtons[i] = new Point2(Display.WIDTH-shopWidth+margin*2,(int) (Display.HEIGHT/2.5)+buttonSize*i+margin*i);
		}
		uButtons[3] = new Point2(Display.WIDTH-shopWidth+margin*2+buttonSize/4,Display.HEIGHT-buttonSize/3-10);
	}
	
	public void addTarget(Enemy e) {
		for (Point2 p: e.colliPoints) {
			if (Point2.getDist(p, point) <= range/2 && !targets.contains(e)) {
					targets.add(e);
					return;
			}
		}
	}
	
	public boolean targetLeft() {
		for (Point2 p: targets.peek().colliPoints) {
			if (Point2.getDist(p, point) < range/2) {
				return false;
			}
		}
		return true;
	}
	
	public boolean mouseOver(int mouseX, int mouseY) {
		return mouseX > point.x-w/2 && mouseX < point.x+w/2 && mouseY > point.y-h/2 && mouseY < point.y+h/2;
	}
	
	public void renderRange(Graphics g) {
		g.setColor(Color.yellow);
		g.drawOval(point.intX()-range/2, point.intY()-range/2, range, range);
	}
	
	public boolean clicked() {
		return isClicked;
	}
	
	public void clicked(boolean b) {
		isClicked = b;
	}
	
	public boolean maxTier() {
		return maxTier;
	}
	
	public abstract int upgrade(int upgrade,int gold);
	public abstract void shoot(Enemy e);
	public abstract void tick();
	public abstract void render(Graphics g);
}
