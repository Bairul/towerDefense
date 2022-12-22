package com.TowerDefense.game.towers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.TowerDefense.game.display.Display;
import com.TowerDefense.game.entities.Enemy;
import com.TowerDefense.game.entities.EntityManager;
import com.TowerDefense.game.entities.Projectile;
import com.TowerDefense.game.pathFinding.Point2;

public class Shotgun extends Tower {
	
	public int atkSpd,damage,pierce,rldSpd,mag;
	public double velocity;
	private int interval, ammo = 0, rInterval = 0;
	private float fontSize = 15F;
	private boolean unlimited = false,reloading = false, flame = false, kb = false;
	private int[] uPrices = {70,120,120,90,160,330,290,400,540,870,1000,2840,1790,3450,2320};
	private String[] upgradeNames = {"+20% Rldspd","+1 Pierce","+1 Dmg","+50 rg","+2 Mag","+1 Spread","+30% Atkspd","+1 Pierce","+2 Dmg","Ranger","Ability","Spreader","Speeder","Flame","Damager"};

	public Shotgun(Point2 point, EntityManager entityManager, int size, int shopWidth, int range, int price, double velocity, int attackSpeed, int damage, int pierce, int mag, int reloadSpeed) {
		super(point, entityManager, size, size, shopWidth, range, price);
		atkSpd = attackSpeed;
		rldSpd = reloadSpeed;
		interval = attackSpeed;
		this.mag = mag;
		this.pierce = pierce;
		this.velocity = velocity;
		this.damage = damage;
		sellPrice = Math.round((float)(price*0.6));
	}
	public Shotgun(Point2 point, EntityManager entityManager, int size, int shopWidth, int range, int price) {
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
			rldSpd-=24;
			upgradePath[0] = true;
			break;
		case 1:
			pierce++;
			upgradePath[1] = true;
			break;
		case 2:
			if (flame)
				pierce++;
			else 
				damage++;
			upgradePath[2] = true;
			break;
		case 3:
			upgradePath[3] = true;
			range+=50;
			spread = Math.PI/24;
			travel += 50;
			break;
		case 4:
			upgradePath[4] = true;
			mag+=2;
			break;
		case 5:
			upgradePath[5] = true;
			scatter++;
			break;
		case 6:
			upgradePath[6] = true;
			if (flame)
				damage++;
			else
				atkSpd-=15;
			break;
		case 7:
			upgradePath[7] = true;
			pierce++;
			break;
		case 8:
			upgradePath[8] = true;
			if (flame)
				pierce+=2;
			else
				damage+=2;
			break;
		case 9:
			range+=100;
			spread = Math.PI/30;
			travel += 150;
			pierce++;
			damage++;
			velocity+=1;
			upgradePath[9] = true;
			break;
		case 10:
			//ability
			upgradePath[9] = true;
			break;
		case 11:
			scatter++;
			spread = Math.PI/20;
			damage+=2;
			upgradePath[9] = true;
			break;
		case 12:
			atkSpd = 5;
			rldSpd = 55;
			damage++;
			pierce++;
			upgradePath[9] = true;
			break;
		case 13:
			flame = true;
			atkSpd = 2;
			mag += 24;
			rldSpd += 100;
			pierce++;
			damage--;
			travel+=50;
			pSize+=5;
			velocity-=2;
			damage = Math.max(damage, 1);
			upgradePath[9] = true;
			break;
		case 14:
			damage+=2;
			kb = true;
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
	private double spread = Math.PI/16;
	private int scatter = 3, travel = 100, pSize = 10;
	@Override
	public void shoot(Enemy e) {
		if (scatter%2 == 1) {
			double dir = point.getDirection(e.point);
			double dir1 = point.getDirection(e.point)+spread;
			double dir2 = point.getDirection(e.point)-spread;
			Projectile p = new Projectile(new Point2(point.x,point.y), dir,travel,pSize,velocity,damage,pierce,false,false,0);
			Projectile p1 = new Projectile(new Point2(point.x,point.y), dir1,travel,pSize,velocity,damage,pierce,false,false,0);
			Projectile p2 = new Projectile(new Point2(point.x,point.y), dir2,travel,pSize,velocity,damage,pierce,false,false,0);
			if (flame) {
				p.flame = true;
				p1.flame = true;
				p2.flame = true;
				p.penetrate = true;
				p1.penetrate = true;
				p2.penetrate = true;
			} else if (kb) {
				p.knockback = 5;
				p1.knockback = 5;
				p2.knockback = 5;
			}
			entityManager.projectiles.add(p);
			entityManager.projectiles.add(p1);
			entityManager.projectiles.add(p2);
			if (scatter > 4) {
				double dir3 = point.getDirection(e.point)+spread*2;
				double dir4 = point.getDirection(e.point)-spread*2;
				entityManager.projectiles.add(new Projectile(new Point2(point.x,point.y), dir3,travel,pSize,velocity,damage,pierce,false,false,0));
				entityManager.projectiles.add(new Projectile(new Point2(point.x,point.y), dir4,travel,pSize,velocity,damage,pierce,false,false,0));
			}
		} else {
			double dir = point.getDirection(e.point)+spread/2;
			double dir1 = point.getDirection(e.point)-spread/2;
			double dir2 = point.getDirection(e.point)+spread/2*3;
			double dir3 = point.getDirection(e.point)-spread/2*3;
			Projectile p = new Projectile(new Point2(point.x,point.y), dir,travel,pSize,velocity,damage,pierce,false,false,0);
			Projectile p1 = new Projectile(new Point2(point.x,point.y), dir1,travel,pSize,velocity,damage,pierce,false,false,0);
			Projectile p2 = new Projectile(new Point2(point.x,point.y), dir2,travel,pSize,velocity,damage,pierce,false,false,0);
			Projectile p3 = new Projectile(new Point2(point.x,point.y), dir3,travel,pSize,velocity,damage,pierce,false,false,0);
			if (flame) {
				p.flame = true;
				p1.flame = true;
				p2.flame = true;
				p3.flame = true;
				p.penetrate = true;
				p1.penetrate = true;
				p2.penetrate = true;
				p2.penetrate = true;
			} else if (kb) {
				p.knockback = 5;
				p1.knockback = 5;
				p2.knockback = 5;
				p2.knockback = 5;
			}
			entityManager.projectiles.add(p);
			entityManager.projectiles.add(p1);
			entityManager.projectiles.add(p2);
			entityManager.projectiles.add(p3);
		}
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
		if (!unlimited && ammo >= mag) {
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
			if (targets.peek().isDead || this.targetLeft()) {
				targets.poll();
				return;
			}
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
		} else if (!unlimited) {
			g.setColor(Color.gray);
			g.fillRect(point.intX()-w/2, point.intY()-h/2, w, 10);
			g.setColor(Color.red);
			g.fillRect(point.intX()-w/2, point.intY()-h/2, (int) (w-w/mag*ammo), 10);
		}
	}
}
