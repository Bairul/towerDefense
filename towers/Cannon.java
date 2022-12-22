package com.TowerDefense.game.towers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.TowerDefense.game.display.Display;
import com.TowerDefense.game.entities.Enemy;
import com.TowerDefense.game.entities.EntityManager;
import com.TowerDefense.game.entities.Projectile;
import com.TowerDefense.game.pathFinding.Point2;

public class Cannon extends Tower {
	
	public int atkSpd,damage,pierce,rldSpd,mag;
	public double velocity;
	private int interval, ammo = 0, rInterval = 0, boss = 0, travel = 800, bSize = 40;
	private float fontSize = 15F;
	private boolean unlimited = false,reloading = false, aPen = false, sum = false, kb = false, bounce = false;
	private int[] uPrices = {120,230,280,290,460,370,510,300,640,1530,2290,3020,2770,1000,3450};
	private String[] upgradeNames = {"Dmg To Armor","+20% Atkspd","+3 Dmg","Dmg to Summon","+5 Pierce","+Velocity","Dmg to Boss","+15 Size","+5 Dmg","Knockback","Pierce x2","Bounce","Boss Damager","Ability","+1 Mag"};

	public Cannon(Point2 point, EntityManager entityManager, int size, int shopWidth, int range, int price, double velocity, int attackSpeed, int damage, int pierce, int mag, int reloadSpeed) {
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
	
	public Cannon(Point2 point, EntityManager entityManager, int size, int shopWidth, int range, int price) {
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
			aPen = true;
			upgradePath[0] = true;
			break;
		case 1:
			rldSpd -= 48;
			upgradePath[1] = true;
			break;
		case 2:
			damage+=3;
			upgradePath[2] = true;
			break;
		case 3:
			upgradePath[3] = true;
			sum = true;
			break;
		case 4:
			upgradePath[4] = true;
			pierce+=5;
			break;
		case 5:
			upgradePath[5] = true;
			velocity+=3;
			damage+=2;
			pierce+=2;
			break;
		case 6:
			upgradePath[6] = true;
			boss = 1;
			break;
		case 7:
			upgradePath[7] = true;
			damage+=3;
			bSize+=15;
			break;
		case 8:
			upgradePath[8] = true;
			damage+=5;
			break;
		case 9:
			kb = true;
			upgradePath[9] = true;
			break;
		case 10:
			pierce*=2;
			damage++;
			upgradePath[9] = true;
			break;
		case 11:
			bounce = true;
			travel = 2500;
			velocity+=2;
			upgradePath[9] = true;
			break;
		case 12:
			boss = 2;
			upgradePath[9] = true;
			break;
		case 13:
			//ability
			upgradePath[9] = true;
			break;
		case 14:
			mag++;
			atkSpd = 10;
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
		Projectile p = new Projectile(new Point2(point.x,point.y), dir,travel,bSize,velocity,damage,pierce,false,false,0);
		if (sum)
			p.extraDamage(true,true,damage);
		else if (aPen)
			p.extraDamage(true,false,damage);
		else
			p.extraDamage(true, false, 0);
		
		if (boss == 2)
			p.bossDmg = damage*2;
		else if (boss == 1)
			p.bossDmg = damage;
		
		if (kb) {
			p.knockback = 20;
			p.stun(20);
		} else if (bounce)
			p.bounce = true;
		
			
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
}
