package com.TowerDefense.game.towers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.TowerDefense.game.Game;
import com.TowerDefense.game.pathFinding.Point2;

public class TowerBuyer {
	private int x,y,size,id;
	private Game game;
	private TowerManager towerManager;
	public boolean isClicked = false;
	private Point2 dropPoint;
	private String name;
	public int price;
	private float fontSize = 13F;
	
	public TowerBuyer(Game game, TowerManager towerManager, int x, int y, int size,int id) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.size = size;
		this.id = id;
		this.towerManager = towerManager;
		dropPoint = new Point2(x,y);
		switch (id) {
		case 0:
			name = "Pistol";
			price = 110;
			break;
		case 1:
			name = "AR";
			price = 160;
			break;
		case 2:
			name = "Sniper";
			price = 240;
			break;
		case 3:
			name = "Shotgun";
			price = 320;
			break;
		case 4:
			name = "Rocket";
			price = 540;
			break;
		case 5:
			name = "Grenade";
			price = 450;
			break;
		case 6:
			name = "Cannon";
			price = 700;
			break;
		default:
			name = "N/A";
			price = 0;
		}
	}
	
	public void dropTower(Point2 p) {
		dropPoint = new Point2(p.x,p.y);
		switch (id) {
		case 0:
			towerManager.addPistol(dropPoint, false, price);
			break;
		case 1:
			towerManager.addAr(dropPoint, false, price);
			break;
		case 2:
			towerManager.addSniper(dropPoint, false, price);
			break;
		case 3:
			towerManager.addShotgun(dropPoint, false, price);
			break;
		case 4:
			towerManager.addRocket(dropPoint, false, price);
			break;
		case 5:
			towerManager.addGrenade(dropPoint, false, price);
			break;
		case 6:
			towerManager.addCannon(dropPoint, false, price);
			break;
		default:
			return;
		}
	}
	
	public void tick() {
		if (game.mouse.x > x && game.mouse.x < x+size && game.mouse.y > y && game.mouse.y < y+size) {
			if (game.mouse.pressed) {
				switch (id) {
				case 0:
					towerManager.addPistol(new Point2(game.mouse.x,game.mouse.y),true, price);
					break;
				case 1:
					towerManager.addAr(new Point2(game.mouse.x,game.mouse.y),true, price);
					break;
				case 2:
					towerManager.addSniper(new Point2(game.mouse.x,game.mouse.y),true, price);
					break;
				case 3:
					towerManager.addShotgun(new Point2(game.mouse.x,game.mouse.y),true, price);
					break;
				case 4:
					towerManager.addRocket(new Point2(game.mouse.x,game.mouse.y),true, price);
					break;
				case 5:
					towerManager.addGrenade(new Point2(game.mouse.x,game.mouse.y),true, price);
					break;
				case 6:
					towerManager.addCannon(new Point2(game.mouse.x,game.mouse.y),true, price);
					break;
				default:
					return;
				}
				isClicked = true;
			}
		} else if (isClicked && !game.mouse.pressed) {
			isClicked = false;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(x,y,size,size);
		
		Font font = g.getFont();
		if (font.getSize() != fontSize) {
			Font newFont = font.deriveFont(fontSize);
			g.setFont(newFont);
		}
		
		g.setColor(Color.black);
		g.drawString("$"+price, x+2, y+size-(int)fontSize/5-1);
		g.drawString(name, x+2, y+(int)fontSize);
	}
}
