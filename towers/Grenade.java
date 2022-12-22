package com.TowerDefense.game.towers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.TowerDefense.game.Game;
import com.TowerDefense.game.display.Display;
import com.TowerDefense.game.entities.Enemy;
import com.TowerDefense.game.entities.EntityManager;
import com.TowerDefense.game.entities.Projectile;
import com.TowerDefense.game.pathFinding.Point2;

public class Grenade extends Tower {
	
	public int atkSpd,damage,pierce,rldSpd,mag;
	public double velocity;
	private int interval, ammo = 0, rInterval = 0;
	private float fontSize = 15F;
	private boolean unlimited = false,reloading = false, aPen = false, lingering = false, recur = false;
	private int[] uPrices = {60,100,140,210,230,250,300,380,380,1000,2380,3410,960,3040,1890};
	private String[] upgradeNames = {"+50 rg","+10 AOE","+1 AOEdmg","Dmg to Armor","+20% Atkspd","+15 AOE","+Velocity","+2 AOEdmg","+2 AOEdmg","Ability","Speed","Recusive","Auto Aim","Linger","Damager"};
	private Point2 firingPoint;
	private Game game;

	public Grenade(Game game,Point2 point, EntityManager entityManager, int size, int shopWidth, int range, int price, double velocity, int attackSpeed, int damage, int pierce, int mag, int reloadSpeed) {
		super(point, entityManager, size, size, shopWidth, range, price);
		atkSpd = attackSpeed;
		rldSpd = reloadSpeed;
		interval = attackSpeed;
		this.game = game;
		this.mag = mag;
		this.pierce = pierce;
		this.velocity = velocity;
		this.damage = damage;
		sellPrice = Math.round((float)(price*0.6));
		firingPoint = new Point2(point.x,point.y+range/2);
		auto = true;
	}
	
	public Grenade(Point2 point, EntityManager entityManager, int size, int shopWidth, int range, int price) {
		super(point, entityManager, size, size, shopWidth, range, price);
		mag = 1;
	}

	@Override
	public int upgrade(int upgrade, int gold) {
		if (upgradeCount >= 6 || ((maxTier || upgradeCount>4) && upgrade>=9))
			return 0;
		else if (gold < uPrices[upgrade])
			return 0;
		
		switch (upgrade) {
		case 0:
			range+=50;
			upgradePath[0] = true;
			break;
		case 1:
			aoeSize+=10;
			upgradePath[1] = true;
			break;
		case 2:
			damage++;
			upgradePath[2] = true;
			break;
		case 3:
			upgradePath[3] = true;
			aPen = true;
			break;
		case 4:
			upgradePath[4] = true;
			rldSpd-=15;
			break;
		case 5:
			upgradePath[5] = true;
			aoeSize+=15;
			aoeSpd++;
			break;
		case 6:
			upgradePath[6] = true;
			velocity+=2;
			damage++;
			break;
		case 7:
			upgradePath[7] = true;
			damage+=2;
			break;
		case 8:
			upgradePath[8] = true;
			damage+=2;
			break;
		case 9:
			//ability
			range+=100;
			upgradePath[9] = true;
			break;
		case 10:
			rldSpd-=25;
			damage++;
			upgradePath[9] = true;
			break;
		case 11:
			recur = true;
			upgradePath[9] = true;
			break;
		case 12:
			auto = false;
			upgradeCount--;
			pierce=1;
			reloading = false;
			upgradePath[9] = true;
			break;
		case 13:
			lingering = true;
			upgradePath[9] = true;
			break;
		case 14:
			damage+=5;
			upgradePath[9] = true;
			break;
		}
		sellPrice += Math.round((float)(uPrices[upgrade]*0.6));
		if (upgrade >= 9) {
			upgradeCount++;
			maxTier = true;
		}
		upgradeCount++;
		return uPrices[upgrade];
	}

	@Override
	public void shoot(Enemy e) {
		double dir = point.getDirection(e.point);
		Projectile p = new Projectile(new Point2(point.x,point.y), dir,range/2,10,velocity,0,pierce,true,false,0);
		if (lingering)
			p.explosion = new Projectile(p.point,300,7,aoeSize/5,aoeSize,damage,false);
		else
			p.explosion = new Projectile(p.point,0,7,aoeSize/5,aoeSize,damage,false);
		entityManager.projectiles.add(p);
	}
	
	private int aoeSize = 50,aoeSpd = 6;
	
	public void shoot() {
		double dir = point.getDirection(firingPoint);
		int dist = (int) Point2.getDist(point,firingPoint);
		dist = Math.min(dist, range/2);
		
		Projectile p = new Projectile(new Point2(point.x,point.y), dir,dist,10,velocity,0,pierce,true,false,0);
		
		if (lingering)
			p.explosion = new Projectile(p.point,300,7,aoeSize/5,aoeSize,damage,false);
		else
			p.explosion = new Projectile(p.point,0,7,aoeSize/5,aoeSize,damage,false);
		if (aPen)
			p.extraDamage(true,false,damage);
		if (recur)
			p.explosion.recur = true;
		
		entityManager.projectiles.add(p);
	}

	@Override
	public void tick() {
		if (stunned) {
			if (stunTimer < stunDur)
				stunTimer++;
			else {
				stunTimer = 0;
				stunned = false;
			}
			return;
		}
		if (isClicked) {
			firingPoint.setPoint(game.mouse.x, game.mouse.y);
		}
		if (ammo >= mag) {
			reloading = true;
			if (rInterval < rldSpd) {
				rInterval++;
				return;
			} else {
				reloading = false;
				ammo = 0;
				rInterval = 0;
				interval = atkSpd;
			}
		}
		if (interval >= atkSpd) {
			if (!auto && (targets.peek().isDead || this.targetLeft())) {
				targets.poll();
				return;
			}
			if (auto)
				shoot();
			else
				shoot(targets.peek());
			interval = 0;
			ammo++;
		}
		else
			interval++;
	}

	@Override
	public void render(Graphics g) {
		if (isClicked) {
			Font font = g.getFont();
			if (font.getSize() != fontSize) {
				Font newFont = font.deriveFont(fontSize);
				g.setFont(newFont);
			}
			g.setColor(Color.black);
			g.drawString("Upgrades: "+(6-upgradeCount), Display.WIDTH-buttonSize-margin, uButtons[0].intY()-(int)fontSize);
			//sell button
			g.setColor(Color.yellow);
			g.fillRect(uButtons[3].intX(), uButtons[3].intY(), buttonSize/2, buttonSize/4+margin/2);
			g.setColor(Color.black);
			g.drawString("Sell", uButtons[3].intX()+margin*3/2, uButtons[3].intY()+buttonSize/8);
			g.drawString("$"+sellPrice, uButtons[3].intX()+margin, uButtons[3].intY()+buttonSize/8+(int)fontSize);
			
			for (int i=0; i<3; i++) {
				if (upgradePath[i]) {
					if (upgradeCount >= 6)
						g.setColor(Color.LIGHT_GRAY);
					else
						g.setColor(Color.white);
					g.fillRect(uButtons[i].intX(), uButtons[i].intY(), buttonSize, buttonSize/2-2);
					g.fillRect(uButtons[i].intX(), uButtons[i].intY()+buttonSize/2+2, buttonSize, buttonSize/2-1);
					g.setColor(Color.black);
					if (upgradePath[i+3]) {
						if (maxTier)
							g.drawString("Closed", uButtons[i].intX()+10, uButtons[i].intY()+buttonSize/4+(int)fontSize/2);
						else {
							g.drawString("$"+uPrices[i+3+6], uButtons[i].intX()+10, uButtons[i].intY()+buttonSize/2-(int)fontSize/2);
							g.drawString(upgradeNames[i+3+6], uButtons[i].intX()+10, uButtons[i].intY()+buttonSize/4-(int)fontSize/2);
						}
					} else {
						g.drawString("$"+uPrices[i+3], uButtons[i].intX()+10, uButtons[i].intY()+buttonSize/2-(int)fontSize/2);
						g.drawString(upgradeNames[i+3], uButtons[i].intX()+10, uButtons[i].intY()+buttonSize/4-(int)fontSize/2);
					}
					if (upgradePath[i+6]) {
						if (maxTier)
							g.drawString("Closed", uButtons[i].intX()+10, uButtons[i].intY()+buttonSize/4*3+(int)fontSize/2);
						else {
							g.drawString("$"+uPrices[i+6+6], uButtons[i].intX()+10, uButtons[i].intY()+buttonSize-(int)fontSize/2);
							g.drawString(upgradeNames[i+6+6], uButtons[i].intX()+10, uButtons[i].intY()+buttonSize-buttonSize/4-(int)fontSize/2);
						}
					} else {
						g.drawString("$"+uPrices[i+6], uButtons[i].intX()+10, uButtons[i].intY()+buttonSize-(int)fontSize/2);
						g.drawString(upgradeNames[i+6], uButtons[i].intX()+10, uButtons[i].intY()+buttonSize-buttonSize/4-(int)fontSize/2);
					}
				} 
				else {
					if (upgradeCount >= 6)
						g.setColor(Color.LIGHT_GRAY);
					else
						g.setColor(Color.white);
					g.fillRect(uButtons[i].intX(), uButtons[i].intY(), buttonSize, buttonSize);
					g.setColor(Color.black);
					g.drawString("$"+uPrices[i], uButtons[i].intX()+10, uButtons[i].intY()+buttonSize-(int)fontSize/2);
					g.drawString(upgradeNames[i], uButtons[i].intX()+10, uButtons[i].intY()+buttonSize-buttonSize/2+(int)fontSize/2);
				}
			}
			renderRange(g);
		}
		g.setColor(Color.orange);
		g.fillRect(point.intX()-w/2,point.intY()-h/2,w,h);
		if (reloading) {
			g.setColor(Color.gray);
			g.fillRect(point.intX()-w/2, point.intY()-h/2, w, 10);
			g.setColor(Color.darkGray);
			g.fillRect(point.intX()-w/2, point.intY()-h/2, (int) (w*rInterval/rldSpd), 10);
		} else {
			g.setColor(Color.gray);
			g.fillRect(point.intX()-w/2, point.intY()-h/2, w, 10);
			g.setColor(Color.red);
			g.fillRect(point.intX()-w/2, point.intY()-h/2, (int) (w-w/mag*ammo), 10);
		}
	}
}
