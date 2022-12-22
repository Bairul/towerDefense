package com.TowerDefense.game.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

import com.TowerDefense.game.pathFinding.Point2;
import com.TowerDefense.game.towers.Tower;

public class Enemy extends Entity{
	
	private Point2[] path;
	public int pathIndex = 0;
	public double speed,modSpeed;
	public int size,health,gold,damage,summonCount = 0;
	public boolean circle,bulletProof = false, summoner, canSummon = false, isBoss, antiSplash, castingAbility;
	public boolean isDead = false, isKilled = false, stunned = false, slowed = false;
	public Point2[] colliPoints;
	private Color color;
	private Polygon poly,polyOut;

	public Enemy(Point2 point, int size, int health, double speed, int gold, int damage, boolean isCircle, boolean summoner, boolean diamond,Point2[] path, Color c) {
		super(point, size, size);
		this.size = size;
		this.path = path;
		this.speed = speed;
		this.health = health;
		this.gold = gold;
		this.damage = damage;
		this.summoner = summoner;
		circle = isCircle;
		modSpeed = speed;
		color = c;
		isBoss = false;
		antiSplash = diamond;
		
		colliPoints = new Point2[8];
		getColliPoints(size);
		if (!isCircle && !summoner && !diamond)
			bulletProof = true;
	}
	
	private int sInterval = 0, sDelayInterval = 0,sDelay,summonSpeed,stunDuration,stunTimer = 0, slowDuration,slowTimer = 0;
	private int bossAbilityType, bossAbilitySpeed, bossAbilityMax, bossAbilityMin, bossAbilityTimer = 0, bossAbilityRange, castingDur, castingTimer = 0;
	public Enemy[] summons;
	
	public void setSummoner(int summonNum, int summonSpeed, int summonDelay, Enemy e) {
		this.summonSpeed = summonSpeed;
		sDelay = summonDelay;
		summons = new Enemy[summonNum];
		for (int i=0; i<summonNum; i++) {
			summons[i] = e;
		}
		sInterval = summonSpeed;
		sDelayInterval = sDelay;
	}
	
	public void setBoss(int abilityType,int minAbilitySpeed, int maxAbilitySpeed, int abilityRange) {
		isBoss = true;
		bossAbilityType = abilityType;
		bossAbilityMin = minAbilitySpeed;
		bossAbilityMax = maxAbilitySpeed;
		bossAbilityRange = abilityRange;
		int range = maxAbilitySpeed - minAbilitySpeed +1;
		bossAbilitySpeed = (int)(Math.random() *range)+minAbilitySpeed;
		castingDur = 20;
	}
	
	public void moveBack(int dist,Point2 p) {
		point.moveTowardsDirWith(point.getDirection(path[Math.max(0, pathIndex-1)]), colliPoints, dist);
	}
	
	public void stunned(int duration) {
		stunDuration = duration;
		stunned = true;
	}
	
	public void slowDown(double slowPer, int slowDur) {
		slowed = true;
		slowDuration = slowDur;
		modSpeed = speed*slowPer;
		slowTimer = 0;
	}
	
	public void getColliPoints(int size) {
		double dir = 0;
		double dist = size;
		for (int i=0; i<8; i++) {
			if (antiSplash) {
				if (i%2 == 1)
					dist = Math.sqrt(size*size*2)/2;
				else
					dist = size;
			} else {
				if (!circle && i%2 == 1)
					dist = Math.sqrt(size*size*2);
				else
					dist = size;
			}
				
			colliPoints[i] = point.moveTowardsDir(dir, dist/2);
			dir+=Math.PI/4;
			
			if (dir > Math.PI)
				dir -= Math.PI*2;
			else if (dir <= -Math.PI)
				dir += Math.PI*2;
		}
		if (antiSplash) {
			poly = new Polygon();
			polyOut = new Polygon();
			for (int i=0; i<colliPoints.length; i+=2) {
				poly.addPoint(colliPoints[i].intX(), colliPoints[i].intY());
				polyOut.addPoint(colliPoints[i].intX(), colliPoints[i].intY());
			}
		}
	}
	
	public void bossAbility(ArrayList<Enemy> enemies, ArrayList<Tower> towers) {
		if (bossAbilityType == 0) {
			for (Tower t: towers) {
				if (Point2.getDist(point, t.point) <= bossAbilityRange)
					t.stunned = true;
			}
		} else if (bossAbilityType == 1) {
			for (Enemy e: enemies) {
				if (Point2.getDist(point, e.point) <= bossAbilityRange)
					e.slowDown(1.5, 90);
			}
		}
	}


	@Override
	public void tick() {
		if (path != null ) {
			if (pathIndex < path.length) {
				if (stunned) {
					if (stunTimer < stunDuration)
						stunTimer++;
					else {
						stunned = false;
						stunTimer = 0;
					}
				} else {
					if (isBoss) {
						if (bossAbilityTimer < bossAbilitySpeed)
							bossAbilityTimer++;
						else {
							castingAbility = true;
							if (castingTimer < castingDur) {
								castingTimer++;
							} else {
								castingTimer = 0;
								bossAbilityTimer = 0;
								castingAbility = false;
								int range = bossAbilityMax - bossAbilityMin + 1;
								bossAbilitySpeed = (int)(Math.random()*range)+bossAbilityMin;
							}
						}
					} else {
						if (slowed) {
							if (slowTimer < slowDuration)
								slowTimer++;
							else {
								slowed = false;
								slowTimer = 0;
								modSpeed = speed;
							}
						}
						if (summoner) {
							if (sInterval >= summonSpeed) {
								if (sDelayInterval >= sDelay) {
									canSummon = true;
									sDelayInterval = 0;
								} else
									sDelayInterval++;
								
								if (summonCount/summons.length == 1) {
									summonCount = 0;
									sInterval = 0;
								}
							} else
								sInterval++;
						}
					}
					if (!castingAbility && !point.moveTowardsWith(path[pathIndex], colliPoints,modSpeed))
						pathIndex++;
				}
			}
			else
				isDead = true;
		}
	}

	@Override
	public void render(Graphics g) {
		g.setColor(color);
		if (!isDead) {
			if (isBoss) {
				g.fillOval(point.intX()-size/2, point.intY()-size/2, size, size);
				g.setColor(Color.black);
				g.drawOval(point.intX()-size/2, point.intY()-size/2, size, size);
				if (castingAbility) {
					g.setColor(Color.red);
					g.drawOval(point.intX()-bossAbilityRange/2, point.intY()-bossAbilityRange/2, bossAbilityRange, bossAbilityRange);
				}
			}
			else if (circle) {
				g.fillOval(point.intX()-size/2, point.intY()-size/2, size, size);
				g.setColor(Color.black);
				g.drawOval(point.intX()-size/2, point.intY()-size/2, size, size);
			} else if (summoner) {
				g.fillRoundRect(point.intX()-size/2, point.intY()-size/2, size, size,size/4,size/4);
				if (color == Color.black)
					g.setColor(Color.white);
				else
					g.setColor(Color.black);
				g.drawRoundRect(point.intX()-size/2, point.intY()-size/2, size, size,size/2,size/2);
			} else if (antiSplash) {
				g.fillPolygon(poly);
				g.setColor(Color.black);
				g.fillRect(colliPoints[5].intX(), colliPoints[5].intY(), colliPoints[7].intX()-colliPoints[5].intX(), colliPoints[3].intY()-colliPoints[5].intY());
				g.drawPolygon(polyOut);
				for (int i=0; i<colliPoints.length; i+=2) {
					poly.xpoints[i/2] = colliPoints[i].intX();
					poly.ypoints[i/2] = colliPoints[i].intY();
					polyOut.xpoints[i/2] = colliPoints[i].intX();
					polyOut.ypoints[i/2] = colliPoints[i].intY();
				}
			} else {
				g.fillRect(point.intX()-size/2, point.intY()-size/2, size, size);
				g.setColor(Color.black);
				g.drawRect(point.intX()-size/2, point.intY()-size/2, size, size);
			}
//			g.setColor(Color.black);
//			g.drawString(""+health, point.intX(), point.intY());
//			for (Point2 p: colliPoints) {
//				g.setColor(Color.black);
//				g.fillOval(p.intX()-2, p.intY()-2, 4, 4);
//			}
		}
	}

}
