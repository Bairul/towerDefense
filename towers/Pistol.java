package com.TowerDefense.game.towers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.TowerDefense.game.display.Display;
import com.TowerDefense.game.entities.Enemy;
import com.TowerDefense.game.entities.EntityManager;
import com.TowerDefense.game.entities.Projectile;
import com.TowerDefense.game.pathFinding.Point2;

public class Pistol extends Tower {
	
	public int atkSpd,damage,pierce,rldSpd,mag;
	public double velocity;
	private int interval, ammo = 0, rInterval = 0;
	private float fontSize = 15F;
	private boolean unlimited = false,reloading = false, aPen = false;
	private int[] uPrices = {30,50,80,100,120,180,150,260,330,710,900,1050,1500,620,1440};
	private String[] upgradeNames = {"+4 Mag","+15% Atkspd","+1 Dmg","+100 Rg","+20% Atkspd","+1 Pierce","+25% Rldspd","+1 Pierce","+2 Dmg","Ability","Speed","Piercing","Infinte Mag","Armor Pen","Damage"};

	public Pistol(Point2 point, EntityManager entityManager, int size, int shopWidth, int range, int price,double velocity, int attackSpeed, int damage, int pierce, int mag, int reloadSpeed) {
		super(point, entityManager,size, size, shopWidth,range,price);
		atkSpd = attackSpeed;
		rldSpd = reloadSpeed;
		interval = attackSpeed;
		this.mag = mag;
		this.pierce = pierce;
		this.velocity = velocity;
		this.damage = damage;
		sellPrice = Math.round((float)(price*0.6));
	}
	
	public Pistol(Point2 point, EntityManager entityManager, int size, int shopWidth, int range,int price) {
		super(point, entityManager,size, size, shopWidth,range,price);
		mag = 1;
	}
	
	@Override
	public void shoot(Enemy e) {
		double dir = point.getDirection(e.point);
		Projectile p = new Projectile(new Point2(point.x,point.y), dir,200,10,velocity,damage,pierce,false,false,0);
		if (aPen)
			p.extraDamage(true,false,0);
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

	@Override
	public int upgrade(int upgrade,int gold) {
		if (upgradeCount >= 6 || ((maxTier || upgradeCount>4) && upgrade>=9))
			return 0;
		else if (gold < uPrices[upgrade])
			return 0;
		
		switch (upgrade) {
		case 0:
			mag+=4;
			
			upgradePath[0] = true;
			break;
		case 1:
			atkSpd-=4;
			
			upgradePath[1] = true;
			break;
		case 2:
			damage++;
			
			upgradePath[2] = true;
			break;
		case 3:
			range+=100;
			upgradePath[3] = true;
			
			break;
		case 4:
			atkSpd-=10;
			upgradePath[4] = true;
			
			break;
		case 5:
			pierce++;
			upgradePath[5] = true;
			
			break;
		case 6:
			rldSpd-=20;
			upgradePath[6] = true;
			
			break;
		case 7:
			pierce++;
			upgradePath[7] = true;
			
			break;
		case 8:
			damage+=2;
			upgradePath[8] = true;
			
			break;
		case 9:
			
			upgradePath[9] = true;
			break;
		case 10:
			atkSpd-=12;
			rldSpd-=20;
			
			upgradePath[9] = true;
			break;
		case 11:
			pierce++;
			damage++;
			
			upgradePath[9] = true;
			break;
		case 12:
			unlimited = true;
			
			upgradePath[9] = true;
			break;
		case 13:
			aPen = true;
			damage++;
			velocity+=1;
			upgradePath[9] = true;
			break;
		case 14:
			damage+=3;
			range+=50;
			
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
}
