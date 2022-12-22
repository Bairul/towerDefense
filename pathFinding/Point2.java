package com.TowerDefense.game.pathFinding;

public class Point2 {
	public double x,y;
	
	public Point2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public int intX() {
		return Math.round((float)x);
	}
	
	public int intY() {
		return Math.round((float)y);
	}
	
	public void setPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return "("+x+", "+y+")";
	}
	
	public static double getDist(Point2 p1, Point2 p2) {
		double dx = p2.x - p1.x;
		double dy = p2.y - p1.y;
		
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public double getDirection(Point2 p) {
		double dx = p.x - x;
		double dy = p.y - y;
		
		return Math.atan2(dy, dx);
	}
	
	public boolean approximates(Point2 p, double speed) {
		if (Point2.getDist(this, p) < speed/2+1) {
			x = p.x;
			y = p.y;
			return true;
		}
		return false;
	}
	
	public boolean moveTowardsWith(Point2 other, Point2[] withWho, double speed) {
		double dir = getDirection(other);
		double vx = Math.cos(dir);
		double vy = Math.sin(dir);
		
		if (approximates(other,speed))
			return false;
		
		x+=vx*speed;
		y+=vy*speed;
		
		for (int i=0; i<withWho.length; i++) {
			withWho[i].x+=vx*speed;
			withWho[i].y+=vy*speed;
		}
		return true;
	}
	
	public boolean moveTowardsDirWith(double dir, Point2[] withWho, double speed) {
		double vx = Math.cos(dir);
		double vy = Math.sin(dir);
		
		x+=vx*speed;
		y+=vy*speed;
		
		for (int i=0; i<withWho.length; i++) {
			withWho[i].x+=vx*speed;
			withWho[i].y+=vy*speed;
		}
		return true;
	}
	
	public void moveTowardsDirection(double direction, double speed) {
		double vx = Math.cos(direction);
		double vy = Math.sin(direction);
		
		x+=vx*speed;
		y+=vy*speed;
	}
	
	public Point2 moveTowardsDir(double direction, double distance) {
		Point2 movePoint = new Point2(x,y);
		double vx = Math.cos(direction);
		double vy = Math.sin(direction);
		
		movePoint.x+=vx*distance;
		movePoint.y+=vy*distance;
		
		return movePoint;
	}
}
