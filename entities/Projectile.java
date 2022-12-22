package com.TowerDefense.game.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;

import com.TowerDefense.game.display.Display;
import com.TowerDefense.game.pathFinding.Point2;

public class Projectile extends Entity {
	
	public Point2 startingPoint;
	private Point2[] colliPoints;
	private double speed,direction;
	private double[] expansionDir;
	private int size,maxRadius,expandingSpd, lingeringTime;
	public int damage,travelDist,pierce,extraDmg,knockback,stunDur,slowDur,bossDmg;
	public HashSet<Enemy> prevHit;
	public boolean splash,lingering,disapate,penetrate,flame,recur,stun,slow,sum,bounce;

	public Projectile(Point2 point, double direction, int travelDist, int size, double speed, int damage, int pierce, boolean splash, boolean stun, int knockback) {
		super(point, size, size);
		this.size = size;
		this.speed = speed;
		this.splash = splash;
		this.damage = damage;
		this.pierce = pierce;
		this.direction = direction;
		this.travelDist = travelDist;
		this.knockback = knockback;
		this.stun = stun;
		extraDmg = 0;
		prevHit = new HashSet<Enemy>();
		//startingPoint = new Point2(point.x,point.y);
		lingering = false;
		if (splash)
			penetrate = true;
		else
			penetrate = false;
		flame = false;
		recur = false;
		sum = false;
		bossDmg = 0;
		getColliPoints(size);
	}
	
	public Projectile(Point2 point, int lingeringTime, int expandingSpd, int minRadius, int maxRadius,int damage, boolean slow) {
		super(point,minRadius,minRadius);
		this.maxRadius = maxRadius;
		this.lingeringTime = lingeringTime;
		this.expandingSpd = expandingSpd;
		this.damage = damage;
		this.slow = slow;
		lingering = true;
		size = minRadius;
		expansionDir = new double[8];
		disapate = false;
		penetrate = true;
		prevHit = new HashSet<Enemy>();
		recur = false;
		//getColliPoints(size);
	}
	
	public boolean checkCollide(Enemy enemy) {
		if (prevHit.contains(enemy))
			return false;
		
		if (enemy.circle || enemy.antiSplash) {
			if (lingering) {
				for (Point2 p: colliPoints) {
					if (Point2.getDist(p, enemy.point) <= enemy.size/2)
						return true;
				}
			} else if (Point2.getDist(point, enemy.point) <= enemy.size/2)
				return true;
		} else {
			double e_x = enemy.point.x;
			double e_y = enemy.point.y;
			for (Point2 p: colliPoints) {
				if (p.x > e_x-enemy.size/2 && p.x < e_x+enemy.size/2 && p.y > e_y-enemy.size/2 && p.y < e_y+enemy.size/2)
					return true;
			}
		}
		
		return false;
	}
	
	private void getColliPoints(int size) {
		if (lingering || size>=40) {
			colliPoints = new Point2[9];
			double dir = direction;
			for (int i=0; i<8; i++) {
				dir += Math.PI/4;
				if (dir > Math.PI)
					dir -= Math.PI*2;
				else if (dir <= -Math.PI)
					dir += Math.PI*2;
				
				colliPoints[i] = point.moveTowardsDir(dir, size/2);
				if (lingering) expansionDir[i] = dir;
			}
			colliPoints[8] = new Point2(point.x,point.y);
		} else {
			colliPoints = new Point2[5];
			colliPoints[0] = point.moveTowardsDir(direction, size/2);
			colliPoints[3] = point.moveTowardsDir(direction, -size/2);
			colliPoints[4] = new Point2(point.x,point.y);
			
			double dir = direction+Math.PI/2;
			double dir2 = direction-Math.PI/2;
			if (dir > Math.PI)
				dir -= Math.PI*2;
			if (dir2 <= -Math.PI)
				dir2 += Math.PI*2;
			
			colliPoints[1] = point.moveTowardsDir(dir, size/2);
			colliPoints[2] = point.moveTowardsDir(dir2, size/2);
		}
	}
	
	public Projectile explosion;
	public Projectile[] recursive;
	
	public void explosionColli() {
		getColliPoints(size);
	}
	
	public void setRecursive() {
		recursive = new Projectile[6];
		Point2 p = new Point2(point.x,point.y);
		double dir = 0;
		for (int i=0; i<recursive.length; i++) {
			dir += Math.PI/3;
			if (dir > Math.PI)
				dir -= Math.PI*2;
			else if (dir <= -Math.PI)
				dir += Math.PI*2;
			recursive[i] = new Projectile(p.moveTowardsDir(dir, maxRadius),0,expandingSpd,20,maxRadius,damage,false);
		}
	}
	
	public void extraDamage(boolean pen, boolean sum, int extraDmg) {
		penetrate = pen;
		this.sum = sum;
		this.extraDmg = extraDmg;
	}
	
	public void stun(int stunDur) {
		stun = true;
		this.stunDur = stunDur;
	}
	
	public double slowPer,travelingDist = 0;
	public void slow(double slowPer, int slowDur) {
		this.slowPer = slowPer;
		this.slowDur = slowDur;
		slow = true;
	}

	private int lingerTimer = 0;
	@Override
	public void tick() {
		if (lingering) {
			if (size > maxRadius && lingerTimer > lingeringTime)
				disapate = true;
			else {
				if (size < maxRadius) {
					size += expandingSpd;
					for (int i=0; i<colliPoints.length-1; i++) {
						colliPoints[i].moveTowardsDirection(expansionDir[i], expandingSpd/2D);
					}
				}
				lingerTimer++;
			}
				
		}
		else {
			point.moveTowardsDirWith(direction, colliPoints, speed);
			travelingDist += speed;
			if (bounce) {
				if (point.y > Display.HEIGHT || point.y < 0)
					direction*=-1;
				else if (point.x > Display.WIDTH-Display.SHOPWIDTH || point.x < 0) {
					direction*=-1;
					direction+=Math.PI;
					if (direction > Math.PI)
						direction -= Math.PI*2;
					else if (direction <= -Math.PI)
						direction += Math.PI*2;
				}
			}
		}
		
		
	}

	@Override
	public void render(Graphics g) {
		if (lingering) {
			g.setColor(Color.red);
			g.drawOval(point.intX()-size/2, point.intY()-size/2, size, size);
			g.setColor(Color.orange);
			g.drawOval(point.intX()-size/2+1, point.intY()-size/2+1, size-2, size-2);
			g.setColor(Color.red);
			g.drawOval(point.intX()-size/2+2, point.intY()-size/2+2, size-4, size-4);
		} else if (flame) {
			g.setColor(Color.red);
			g.fillOval(point.intX()-size/2, point.intY()-size/2, size, size);
			g.setColor(Color.orange);
			g.fillOval(point.intX()-size/2+3, point.intY()-size/2+2, size/3*2, size/3*2);
			g.setColor(Color.yellow);
			g.fillOval(point.intX()-size/2+5, point.intY()-size/2+4, size/3, size/3);
		}else {
			g.setColor(Color.black);
			g.fillOval(point.intX()-size/2, point.intY()-size/2, size, size);
		}
//		for (Point2 p: colliPoints) {
//			g.setColor(Color.black);
//			if (p != null)
//				g.fillOval(p.intX()-2, p.intY()-2, 4, 4);
//		}
	}

}
