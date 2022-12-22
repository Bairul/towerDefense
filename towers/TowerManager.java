package com.TowerDefense.game.towers;

import java.awt.Graphics;
import java.util.ArrayList;

import com.TowerDefense.game.Game;
import com.TowerDefense.game.display.Display;
import com.TowerDefense.game.entities.Enemy;
import com.TowerDefense.game.entities.EntityManager;
import com.TowerDefense.game.pathFinding.Point2;

public class TowerManager {
	
	private Game game;
	private ArrayList<Tower> towers;
	private EntityManager entityManager;
	private int nodeSize,shopWidth;
	public Tower clickedTower,overlayTower,soldTower;
	public boolean isSolded = false;
	
	public TowerManager(Game game, EntityManager entityManager,int size, int shopWidth) {
		towers = new ArrayList<Tower>();
		this.game = game;
		this.entityManager = entityManager;
		nodeSize = size;//12
		this.shopWidth = shopWidth;
	}
	
	public void addPistol(Point2 point, boolean isOverlay,int price) {
		if (game.gold < price)
			return;
		else if (isOverlay)
			overlayTower = new Pistol(point, entityManager, nodeSize*2, shopWidth,250,price);
		else {
			towers.add(new Pistol(point, entityManager, nodeSize*2,shopWidth,250,price,9,40,1,1,6,80));
			game.gold-=price;
		}
	}
	
	public void addAr(Point2 point, boolean isOverlay, int price) {
		if (game.gold < price)
			return;
		else if (isOverlay)
			overlayTower = new Ar(point, entityManager, nodeSize*3, shopWidth,220,price);
		else {//range,price,velocity,attackSpeed,damage,pierce,mag,reloadSpeed
			towers.add(new Ar(point, entityManager, nodeSize*3,shopWidth,230,price,8,15,1,1,12,150));
			game.gold-=price;
		}
	}
	
	public void addSniper(Point2 point, boolean isOverlay, int price) {
		if (game.gold < price)
			return;
		else if (isOverlay)
			overlayTower = new Sniper(point, entityManager, nodeSize*3, shopWidth,420,price);
		else {//range,price,velocity,attackSpeed,damage,pierce,mag,reloadSpeed
			towers.add(new Sniper(point, entityManager, nodeSize*3,shopWidth,420,price,14,70,2,1,6,160));
			game.gold-=price;
		}
	}
	
	public void addShotgun(Point2 point, boolean isOverlay, int price) {
		if (game.gold < price)
			return;
		else if (isOverlay)
			overlayTower = new Shotgun(point, entityManager, nodeSize*3, shopWidth,170,price);
		else {//range,price,velocity,attackSpeed,damage,pierce,mag,reloadSpeed
			towers.add(new Shotgun(point, entityManager, nodeSize*3,shopWidth,170,price,9,50,1,1,2,120));
			game.gold-=price;
		}
	}
	
	public void addRocket(Point2 point, boolean isOverlay, int price) {
		if (game.gold < price)
			return;
		else if (isOverlay)
			overlayTower = new Rocket(point, entityManager, nodeSize*4, shopWidth,290,price);
		else {//range,price,velocity,attackSpeed,damage,pierce,mag,reloadSpeed
			towers.add(new Rocket(point, entityManager, nodeSize*4,shopWidth,290,price,9,80,1,1,6,220));
			game.gold-=price;
		}
	}
	
	public void addGrenade(Point2 point, boolean isOverlay, int price) {
		if (game.gold < price)
			return;
		else if (isOverlay)
			overlayTower = new Grenade(point, entityManager, nodeSize*2, shopWidth,340,price);
		else {//range,price,velocity,attackSpeed,damage,pierce,mag,reloadSpeed
			towers.add(new Grenade(game,point, entityManager, nodeSize*2,shopWidth,340,price,9,0,1,9999,1,75));
			game.gold-=price;
		}
	}
	
	public void addCannon(Point2 point, boolean isOverlay, int price) {
		if (game.gold < price)
			return;
		else if (isOverlay)
			overlayTower = new Cannon(point, entityManager, nodeSize*4, shopWidth,400,price);
		else {//range,price,velocity,attackSpeed,damage,pierce,mag,reloadSpeed
			towers.add(new Cannon(point, entityManager, nodeSize*4,shopWidth,400,price,7,0,3,7,1,240));
			game.gold-=price;
		}
	}
	
	public void tick() {
		if (soldTower != null) {
			game.gold+=soldTower.sellPrice;
			towers.remove(soldTower);
			soldTower = null;
		}
		for (Tower t: towers) {
			for (Enemy e:entityManager.enemies) {
				t.addTarget(e);
				if (e.castingAbility)
					e.bossAbility(entityManager.enemies, towers);
			}
			if (t.targets.size() > 0 || t.auto) 
				t.tick();
			
			if (t.mouseOver(game.mouse.x, game.mouse.y) && game.mouse.clicked) {
				if (clickedTower != null)
					clickedTower.isClicked = false;
				t.isClicked = true;
				clickedTower = t;
				game.mouse.clicked = false;
			} else if (t.isClicked){
				for (int i=0; i<3; i++) {
					if (game.mouse.x > t.uButtons[i].x && game.mouse.x < t.uButtons[i].x+t.buttonSize && game.mouse.y > t.uButtons[i].y && game.mouse.y < t.uButtons[i].y+t.buttonSize) {
						if (game.mouse.clicked) {
							if (t.upgradePath[i]) {
								if (game.mouse.y < t.uButtons[i].y+t.buttonSize/2) {
									if (t.upgradePath[i+3])
										game.gold-=t.upgrade(i+3+6,game.gold);
									else
										game.gold-=t.upgrade(i+3,game.gold);
								} else {
									if (t.upgradePath[i+6])
										game.gold-=t.upgrade(i+6+6,game.gold);
									else
										game.gold-=t.upgrade(i+6,game.gold);
								}
							} else
								game.gold-=t.upgrade(i,game.gold);
							game.mouse.clicked = false;
						}
					}
				}
				//sell button
				if (game.mouse.x > t.uButtons[3].x && game.mouse.x < t.uButtons[3].x+t.buttonSize/2 && game.mouse.y > t.uButtons[3].y && game.mouse.y < t.uButtons[3].y+t.buttonSize/4) {
					if (game.mouse.clicked) {
						soldTower = t;
						game.mouse.clicked = false;
						isSolded = true;
					}
				}
			}
		}
		if (clickedTower != null && game.mouse.clicked && game.mouse.x < Display.WIDTH-shopWidth) {
			clickedTower.isClicked = false;
			game.mouse.clicked = false;
		}
	}
	
	public boolean clickedOnTower() {
		if (clickedTower != null && clickedTower.clicked())
			return true;
		return false;
	}
	
	public void render(Graphics g) {
		for (Tower t: towers) {
			t.render(g);
		}
	}
}
