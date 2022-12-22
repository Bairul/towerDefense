package com.TowerDefense.game.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import com.TowerDefense.game.Game;
import com.TowerDefense.game.pathFinding.Grid;
import com.TowerDefense.game.pathFinding.PathFind;
import com.TowerDefense.game.pathFinding.Point2;

public class EntityManager {
	
	private Game game;
	private Queue<Enemy> enemiesInQ;
	private Queue<Integer> enemiesInQ_interval;
	public ArrayList<Enemy> enemies;
	public ArrayList<Projectile> projectiles;
	public ArrayList<Enemy> dead;
	
	private Point2 startPoint,endPoint;
	private PathFind route;
	private Point2[] path;
	
	public EntityManager(Game game,Grid grid,Point2 startPoint, Point2 endPoint) {
		this.game = game;
		enemiesInQ = new LinkedList<>();
		enemiesInQ_interval = new LinkedList<>();
		enemies = new ArrayList<Enemy>();
		dead = new ArrayList<Enemy>();
		projectiles = new ArrayList<Projectile>();
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		route = new PathFind(grid);
		path = route.pathFind(startPoint, endPoint);
	}
	//minions
	public void addEnemy1(int numEnemy1, int interval) {//red
		for (int i=0; i<numEnemy1; i++) {
			//size, health, speed, gold, damage, isCircle
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 40, 3, 1, 5,1,true,false, false, path, new Color(200,40,40)));
			enemiesInQ_interval.add(interval);
		}
	}
	public void addEnemy2(int numEnemy2, int interval) {//orange
		for (int i=0; i<numEnemy2; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 40, 6, 1.1, 7,1,true, false, false, path, new Color(250,150,0)));
			enemiesInQ_interval.add(interval);
		}
	}
	public void addEnemy3(int numEnemy3, int interval) {//yellow
		for (int i=0; i<numEnemy3; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 43, 10, 1.2, 9,2,true, false, false, path, new Color(255,220,0)));
			enemiesInQ_interval.add(interval);
		}
	}
	public void addEnemy4(int numEnemy, int interval) {//green
		for (int i=0; i<numEnemy; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 45, 15, 1.3, 14,2,true, false, false, path, new Color(0,110,0)));
			enemiesInQ_interval.add(interval);
		}
	}
	public void addEnemy5(int numEnemy, int interval) {//blue
		for (int i=0; i<numEnemy; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 45, 20, 1.4, 19,2,true, false, false, path, new Color(20,20,250)));
			enemiesInQ_interval.add(interval);
		}
	}
	public void addEnemy6(int numEnemy, int interval) {//indigo
		for (int i=0; i<numEnemy; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 40, 27, 1.5, 21,3,true, false, false, path, new Color(70,0,160)));
			enemiesInQ_interval.add(interval);
		}
	}
	public void addEnemy7(int numEnemy, int interval) {//violet
		for (int i=0; i<numEnemy; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 40, 40, 1.6, 25,3,true, false, false, path, new Color(160,0,255)));
			enemiesInQ_interval.add(interval);
		}
	}
	//bulletProof
	public void addEnemyRect1(int numEnemy, int interval) {//white
		for (int i=0; i<numEnemy; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 40, 9, 1, 13,1,false, false, false, path, Color.white));
			enemiesInQ_interval.add(interval);
		}
	}
	public void addEnemyRect2(int numEnemy, int interval) {//light gray
		for (int i=0; i<numEnemy; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 43, 30, 1.2, 17,2,false, false, false, path, new Color(180,180,180)));
			enemiesInQ_interval.add(interval);
		}
	}
	public void addEnemyRect3(int numEnemy, int interval) {//gray
		for (int i=0; i<numEnemy; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 45, 50, 1.3, 20,3,false, false, false, path, new Color(120,120,120)));
			enemiesInQ_interval.add(interval);
		}
	}
	public void addEnemyRect4(int numEnemy, int interval) {//dark gray
		for (int i=0; i<numEnemy; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 47, 70, 1.3, 23,3,false, false, false, path, new Color(60,60,60)));
			enemiesInQ_interval.add(interval);
		}
	}
	public void addEnemyRect5(int numEnemy, int interval) {//black
		for (int i=0; i<numEnemy; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 50, 100, 1.5, 28,3,false, false, false, path, Color.black));
			enemiesInQ_interval.add(interval);
		}
	}
	
	public void addEnemySum1(int numEnemy, int interval) {
		for (int i=0; i<numEnemy; i++) {
			Enemy e = new Enemy(new Point2(startPoint.x,startPoint.y), 50, 60, 1, 15,3,false, true, false, path, Color.red);
			Enemy s = new Enemy(new Point2(startPoint.x,startPoint.y), 35, 9, 1.6, 3,1,true, false, false, path, Color.cyan);
			e.setSummoner(2, 200, 40,s);
			enemiesInQ.add(e);
			enemiesInQ_interval.add(interval);
		}
	}
	
	public void addEnemyDia1(int numEnemy, int interval) {
		for (int i=0; i<numEnemy; i++) {
			enemiesInQ.add(new Enemy(new Point2(startPoint.x,startPoint.y), 50, 26, 0.9, 13,2,false, false, true, path, Color.red));
			enemiesInQ_interval.add(interval);
		}
	}
	
	public void addBoss1(int numEnemy, int interval) {
		for (int i=0; i<numEnemy; i++) {
			Enemy e = new Enemy(new Point2(startPoint.x,startPoint.y), 150, 400, 0.5, 20,20,true, false, false, path, Color.red);
			e.setBoss(0, 150,600,300);
			enemiesInQ.add(e);
			enemiesInQ_interval.add(interval);
		}
	}
	
	public void addDelay(int interval) {
		Enemy e = new Enemy(new Point2(startPoint.x,startPoint.y), 0, 0, 0, 0,0,true, false, false, path, Color.red);
		e.isDead = true;
		enemiesInQ.add(e);
		enemiesInQ_interval.add(interval);
	}
	
	private int interval = 0;
	public void tick() {
		if (enemiesInQ.size() > 0) {
			interval++;
			
			if (interval >= enemiesInQ_interval.peek()) {
				enemies.add(enemiesInQ.poll());
				enemiesInQ_interval.poll();
				interval = 0;
			}
		}
		
		for (int i=projectiles.size()-1; i>=0; i--) {
			Projectile p = projectiles.get(i);
			p.tick();
			for (int k=0; k<enemies.size(); k++) {
				Enemy e = enemies.get(k);
				
				if (p.checkCollide(e)) {
					if (p.sum && e.summoner)
						e.health-=p.extraDmg;
					else if (e.antiSplash) {
						if (p.lingering) {
							p.disapate = true;
							break;
						} else if (p.penetrate)
							e.health-=p.extraDmg;
					} else if (e.isBoss)
						e.health-=p.bossDmg;
					
					if (e.bulletProof) {
						if (p.penetrate) {
							e.health-=p.extraDmg;
							p.pierce--;
						} else {
							p.pierce = 0;
							break;
						}
					} else if (!p.lingering)
						p.pierce--;
					if (e.isBoss) {
						
					} else {
						if (p.stun)
							e.stunned(p.stunDur);
						else if (p.slow)
							e.slowDown(p.slowPer, p.slowDur);
						if (p.knockback > 0)
							e.moveBack(p.knockback,p.point);
					}
					e.health -= p.damage;
					p.prevHit.add(e);
					
					if (e.health <= 0) {
						e.isDead = true;
						e.isKilled = true;
						game.gold+=e.gold;
					}
					break;
				}
			}
			
			if (projectiles.size() > 0) {
				if (p.lingering) {
					if (p.disapate) {
						if (p.recur) {
							p.setRecursive();
							for (Projectile r: p.recursive) {
								r.explosionColli();
								projectiles.add(r);
							}
						}
						projectiles.remove(i);
					}
				}
				else if (p.travelingDist >= p.travelDist || p.pierce <= 0) {
					projectiles.remove(i);
					if (p.splash) {
						p.explosion.explosionColli();
						projectiles.add(p.explosion);
					}
				}
			}
				
		}
		
		for (int i=enemies.size()-1; i>=0; i--) {
			Enemy e = enemies.get(i);
			e.tick();
			if (e.summoner && e.canSummon) {
				Enemy e_s = e.summons[0];
				Enemy s = new Enemy(new Point2(e.point.x,e.point.y),e_s.size, e_s.health, e_s.speed, e_s.gold,e_s.damage,e_s.circle, false, false, path, Color.cyan);
				s.pathIndex = e.pathIndex;
				enemies.add(s);
				e.canSummon = false;
				e.summonCount++;
			}
			else if (e.isDead) {
				if (!e.isKilled)
					game.lives-=e.damage;
				enemies.remove(i);
			}
		}
		//System.out.println(projectiles.size());
	}
	
	public void render(Graphics g) {
		if (enemies.size() > 0) {
			for (Enemy e: enemies) {
				e.render(g);
			}
		}
		
		if (projectiles.size() > 0) {
			for (Projectile p: projectiles) {
				p.render(g);
			}
		}
	}
}
