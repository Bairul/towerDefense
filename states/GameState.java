package com.TowerDefense.game.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.TowerDefense.game.Game;
import com.TowerDefense.game.display.Display;
import com.TowerDefense.game.entities.EntityManager;
import com.TowerDefense.game.pathFinding.Grid;
import com.TowerDefense.game.pathFinding.Path;
import com.TowerDefense.game.pathFinding.Point2;
import com.TowerDefense.game.towers.TowerBuyer;
import com.TowerDefense.game.towers.TowerManager;

public class GameState extends State {
	private int nodeSize = Display.HEIGHT/60;
	private int shopWidth = Display.WIDTH/6+2, sbutW,sbutH;
	private Grid grid;
	private EntityManager entityManager;
	private TowerManager towerManager;
	private TowerBuyer[] towerBuyers;
	private Point2 startButton;
	private int round;
	private float fontSize = 18F;

	public GameState(Game game) {
		super(game);
		grid = new Grid(nodeSize, shopWidth,
				new Path(2*nodeSize,0,nodeSize,5,16),
				new Path(2*nodeSize,16*nodeSize,nodeSize,20,5),
				new Path(17*nodeSize,4*nodeSize,nodeSize,5,14),
				new Path(21*nodeSize,4*nodeSize,nodeSize,20,5),
				new Path(36*nodeSize,8*nodeSize,nodeSize,5,18),
				new Path(16*nodeSize,26*nodeSize,nodeSize,25,5),
				new Path(16*nodeSize,31*nodeSize,nodeSize,5,20),
				new Path(16*nodeSize,51*nodeSize,nodeSize,40,5),
				new Path(56*nodeSize,26*nodeSize,nodeSize,5,30),
				new Path(61*nodeSize,26*nodeSize,nodeSize,10,5));
		
		entityManager = new EntityManager(game,grid,new Point2(4*nodeSize,1),new Point2(71*nodeSize,28*nodeSize));
		towerManager = new TowerManager(game,entityManager,nodeSize,shopWidth);
		
		towerBuyers = new TowerBuyer[7];
		shop();
		round = 0;
		Display.SHOPWIDTH = shopWidth;
	}
	
	private void shop() {
		int s = shopWidth/3-5;
		int margin = shopWidth/16;
		for (int i=0; i<7;i++) {
			towerBuyers[i] = new TowerBuyer(game,towerManager,Display.WIDTH-shopWidth+s*(i%2)+margin*2*(i%2+1),20+s*(i/2)+margin*(i/2),s,i);
		}
		sbutW = shopWidth/2;
		sbutH = shopWidth/3;
		startButton = new Point2(Display.WIDTH-shopWidth/4*3,20+s*5+margin*3);
	}

	private boolean dragging = false;
	private TowerBuyer towerClicked;
	private int offsetX = 0,offsetY = 0;
	@Override
	public void tick() {
		if (game.mouse.x > startButton.x && game.mouse.x < startButton.x+sbutW && game.mouse.y > startButton.y && game.mouse.y < startButton.y+sbutH) {
			if (game.mouse.clicked && entityManager.enemies.size() == 0 && !towerManager.clickedOnTower()) {
				startWave();
				round++;
				game.mouse.clicked = false;
			}
		}
		
		entityManager.tick();
		towerManager.tick();
		
		if (!dragging) {
			for (TowerBuyer b: towerBuyers) {
				b.tick();
				if (b.isClicked && game.gold >= b.price) {
					dragging = true;
					towerClicked = b;
					if ((towerManager.overlayTower.w/nodeSize)%2 == 1)
						offsetX = nodeSize/2;
					else
						offsetX = 0;
					if ((towerManager.overlayTower.h/nodeSize)%2 == 1)
						offsetY = nodeSize/2;
					else
						offsetY = 0;
				}
			}
		} else {
			Point2 overlayP = towerManager.overlayTower.point;
			overlayP.setPoint(game.mouse.x-(game.mouse.x%nodeSize)+offsetX,game.mouse.y-(game.mouse.y%nodeSize)+offsetY);
			towerClicked.tick();
			if (!towerClicked.isClicked) {
				if (!(game.mouse.x > Display.WIDTH-shopWidth) && grid.setGridValidity(overlayP.intX(), overlayP.intY(), towerManager.overlayTower.w, towerManager.overlayTower.h)) {
					towerClicked.dropTower(towerManager.overlayTower.point);
				}
				dragging = false;
			}
		}
		if (towerManager.isSolded) {
			Point2 soldP = towerManager.soldTower.point;
			grid.updateGridValidity(soldP.intX(), soldP.intY(), towerManager.soldTower.w, towerManager.soldTower.h);
			towerManager.isSolded = false;
			towerManager.clickedTower.clicked(false);
		} 

		game.mouse.offClicked();
	}
	private int goldMargin = nodeSize*3;
	@Override
	public void render(Graphics g) {
		//background
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, Display.WIDTH-shopWidth, Display.HEIGHT);
		g.setColor(Color.GRAY);
		g.fillRect(Display.WIDTH-shopWidth, 0, shopWidth, Display.HEIGHT);
		grid.render(g);
		
		for (TowerBuyer b: towerBuyers) {
			b.render(g);
		}
		if (dragging && game.mouse.x < Display.WIDTH-shopWidth) {
			towerManager.overlayTower.render(g);
			towerManager.overlayTower.renderRange(g);
		} else if (entityManager.enemies.size() == 0) {
			g.setColor(Color.orange);
			if (towerManager.clickedTower == null || towerManager.clickedTower.clicked() == false)
				g.fillRoundRect(startButton.intX(), startButton.intY(), sbutW, sbutH,20,20);
			g.setColor(Color.black);
			g.drawString("Start Wave", startButton.intX()+sbutH/6, startButton.intY()+sbutH/5*3);
			g.drawString("Start Wave", startButton.intX()+sbutH/6+1, startButton.intY()+sbutH/5*3);
		} 
		
		entityManager.render(g);
		towerManager.render(g);
		
		Font font = g.getFont();
		if (font.getSize() != fontSize) {
			Font newFont = font.deriveFont(fontSize);
			g.setFont(newFont);
		}
		
		g.setColor(Color.black);
		if (round != 0)
		g.drawString(""+round, Display.WIDTH-shopWidth-nodeSize*3, nodeSize*2);
		
		g.drawString(""+game.lives, Display.WIDTH-shopWidth-nodeSize*3, nodeSize*4);

		if (game.gold < 100)
			goldMargin = nodeSize*2;
		else if (game.gold < 1000)
			goldMargin = nodeSize*3;
		else if (game.gold < 10000)
			goldMargin = nodeSize*4-2;
		else if (game.gold < 100000)
			goldMargin = nodeSize*5-3;
		else if (game.gold < 1000000)
			goldMargin = nodeSize*6-5;
		else
			goldMargin = nodeSize*7;
		g.drawString("$"+game.gold, Display.WIDTH-shopWidth-goldMargin-10, nodeSize*6);
		//game.gold+=1;
	}
	
	private void startWave() {
		switch (round) {
		case 0:
			entityManager.addEnemy1(7,60);
			//entityManager.addBoss1(1, 0);
			break;
		case 1:
			entityManager.addEnemy1(5,60);
			entityManager.addEnemy1(5,30);
			break;
		case 2:
			entityManager.addEnemy1(3,30);
			entityManager.addEnemy1(4,50);
			entityManager.addEnemy1(2,30);
			entityManager.addEnemy1(4,50);
			entityManager.addEnemy1(2,30);
			entityManager.addEnemy1(5,20);
			break;
		case 3:
			entityManager.addEnemy1(4,30);
			entityManager.addEnemy2(3,45);
			entityManager.addEnemy1(4,20);
			entityManager.addEnemy1(2,10);
			entityManager.addEnemy2(2,35);
			entityManager.addEnemy1(7,50);
			break;
		case 4:
			entityManager.addEnemy2(1,20);
			entityManager.addEnemy1(3,20);
			entityManager.addEnemy2(1,20);
			entityManager.addEnemy1(3,20);
			entityManager.addEnemy2(1,20);
			entityManager.addEnemy1(3,20);
			entityManager.addEnemy2(7,50);
			entityManager.addEnemy1(3,40);
			break;
		case 5:
			entityManager.addEnemy1(6,50);
			entityManager.addEnemy2(6,50);
			entityManager.addEnemy3(6,50);
			entityManager.addEnemy2(6,50);
			entityManager.addEnemy1(6,50);
			break;
		case 6:
			entityManager.addEnemy2(24,42);
			break;
		case 7:
			entityManager.addEnemy2(5,20);
			entityManager.addEnemy3(2,10);
			entityManager.addEnemy3(5,30);
			entityManager.addEnemy3(5,20);
			entityManager.addDelay(90);
			entityManager.addEnemy2(12,35);
			break;
		case 8:
			entityManager.addEnemy3(8,10);
			entityManager.addEnemy2(12,30);
			entityManager.addEnemy1(20,5);
			break;
		case 9:
			entityManager.addEnemy3(5,20);
			entityManager.addEnemy2(2,10);
			entityManager.addEnemy2(5,30);
			entityManager.addEnemy2(5,20);
			entityManager.addEnemy2(3,0);
			entityManager.addEnemy2(5,20);
			entityManager.addDelay(120);
			entityManager.addEnemy3(10,35);
			break;
		case 10:
			entityManager.addEnemy4(10,60);
			entityManager.addEnemy3(10,20);
			entityManager.addEnemy2(3,10);
			entityManager.addEnemy3(10,20);
			entityManager.addEnemy4(2,5);
			break;
		case 11:
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			entityManager.addEnemy3(1,0);
			entityManager.addEnemy2(2,30);
			break;
		case 12:
			entityManager.addEnemy3(2,0);
			entityManager.addEnemy4(1,0);
			entityManager.addEnemy3(4,30);
			entityManager.addEnemy4(2,40);
			entityManager.addEnemy3(2,0);
			entityManager.addEnemy4(1,0);
			entityManager.addEnemy3(4,30);
			entityManager.addEnemy4(2,40);
			entityManager.addEnemy3(2,0);
			entityManager.addEnemy4(1,0);
			entityManager.addEnemy3(4,30);
			entityManager.addEnemy4(2,40);
			break;
		case 13:
			entityManager.addEnemyRect1(1,60);
			entityManager.addDelay(320);
			entityManager.addEnemy3(7,60);
			entityManager.addEnemy2(3,2);
			entityManager.addEnemy3(12,60);
			entityManager.addEnemy2(3,2);
			break;
		case 14:
			entityManager.addEnemy4(4,35);
			entityManager.addEnemy3(4,10);
			entityManager.addEnemy4(4,35);
			entityManager.addEnemy3(4,10);
			entityManager.addEnemy4(4,35);
			entityManager.addDelay(120);
			entityManager.addEnemy3(8,10);
			entityManager.addEnemy4(4,35);
			entityManager.addEnemy3(4,10);
			entityManager.addEnemy4(4,35);
			entityManager.addEnemy3(4,10);
			break;
		case 15:
			entityManager.addEnemy3(2,20);
			entityManager.addEnemyRect1(1,0);
			entityManager.addEnemy3(2,20);
			entityManager.addEnemyRect1(1,0);
			entityManager.addEnemy3(2,20);
			entityManager.addEnemyRect1(1,0);
			entityManager.addEnemy3(2,20);
			entityManager.addEnemyRect1(1,0);
			entityManager.addEnemy3(13,30);
			break;
		case 16:
			entityManager.addEnemy3(6,25);
			entityManager.addEnemy4(6,40);
			entityManager.addEnemy4(3,30);
			entityManager.addEnemy3(7,15);
			entityManager.addDelay(60);
			entityManager.addEnemy3(4,5);
			entityManager.addEnemy3(2,20);
			entityManager.addEnemy2(8,0);
			entityManager.addEnemy4(3,30);
			entityManager.addEnemy3(9,40);
			break;
		case 17:
			entityManager.addEnemyRect1(5,30);
			entityManager.addEnemy4(1,0);
			entityManager.addEnemyRect1(2,30);
			entityManager.addEnemy4(1,0);
			entityManager.addEnemyRect1(2,30);
			entityManager.addEnemy4(1,0);
			entityManager.addEnemyRect1(2,30);
			entityManager.addEnemy4(1,0);
			entityManager.addEnemyRect1(2,30);
			entityManager.addEnemy4(1,0);
			entityManager.addEnemyRect1(2,30);
			entityManager.addEnemy4(1,0);
			entityManager.addEnemyRect1(2,30);
			entityManager.addEnemy4(1,0);
			entityManager.addEnemyRect1(12,50);
			break;
		case 18:
			entityManager.addEnemy4(3,20);
			entityManager.addEnemy4(4,40);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy4(4,40);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy4(3,20);
			entityManager.addEnemy4(4,40);
			entityManager.addDelay(60);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy4(4,40);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy4(5,10);
			break;
		case 19:
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			entityManager.addDelay(120);
			entityManager.addEnemySum1(1,0);
			entityManager.addDelay(160);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemy3(3,0);
			break;
		case 20:
			entityManager.addEnemy3(2,0);
			entityManager.addEnemy4(2,40);
			entityManager.addEnemy2(9,1);
			entityManager.addDelay(70);
			entityManager.addEnemy3(2,0);
			entityManager.addEnemy4(2,40);
			entityManager.addEnemy2(9,1);
			entityManager.addDelay(70);
			entityManager.addEnemy3(2,0);
			entityManager.addEnemy4(2,40);
			entityManager.addEnemy2(9,1);
			entityManager.addDelay(70);
			entityManager.addEnemy3(2,0);
			entityManager.addEnemy4(2,40);
			entityManager.addEnemy2(9,1);
			entityManager.addDelay(70);
			entityManager.addEnemy4(2,40);
			entityManager.addEnemy3(20,42);
			entityManager.addDelay(90);
			entityManager.addEnemy2(15,1);
			break;
		case 21:
			entityManager.addEnemy4(3,30);
			entityManager.addEnemySum1(1,0);
			entityManager.addEnemy4(13,30);
			entityManager.addEnemySum1(1,0);
			entityManager.addEnemy4(13,30);
			entityManager.addDelay(90);
			entityManager.addEnemySum1(1,0);
			entityManager.addEnemy4(16,30);
			break;
		case 22:
			entityManager.addEnemyRect1(2,30);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemyRect1(2,30);
			entityManager.addEnemyRect2(4,30);
			entityManager.addDelay(120);
			entityManager.addEnemyRect2(8,30);
			break;
		case 23:
			entityManager.addEnemy4(6,25);
			entityManager.addEnemy3(6,40);
			entityManager.addEnemyRect2(2,0);
			entityManager.addEnemy3(3,30);
			entityManager.addEnemy4(9,35);
			entityManager.addEnemyRect2(2,0);
			entityManager.addEnemy3(4,5);
			entityManager.addEnemyRect2(2,0);
			entityManager.addEnemy5(3,20);
			entityManager.addEnemy3(12,0);
			entityManager.addEnemy4(8,50);
			entityManager.addEnemyRect1(2,0);
			entityManager.addEnemy4(2,30);
			entityManager.addEnemy4(3,10);
			entityManager.addEnemy4(3,30);
			entityManager.addEnemy4(4,10);
			entityManager.addEnemy4(5,30);
			entityManager.addEnemy4(6,10);
			entityManager.addEnemy4(7,30);
			entityManager.addEnemy4(8,10);
			entityManager.addEnemy4(9,30);
			entityManager.addEnemy4(10,10);
			entityManager.addEnemy4(11,30);
			entityManager.addEnemy4(12,10);
			break;
		case 24:
			entityManager.addEnemy3(25,50);
			entityManager.addEnemy4(25,50);
			break;
		case 25:
			entityManager.addEnemyRect1(1,0);
			entityManager.addEnemy4(3,20);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemy5(4,40);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemy4(2,20);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemy4(4,40);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemy5(2,20);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemy4(3,20);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemy4(4,40);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemy5(2,20);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemy4(4,40);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemy5(2,20);
			entityManager.addEnemyRect2(1,0);
			entityManager.addEnemy4(5,10);
			entityManager.addEnemyRect2(7,10);
			entityManager.addEnemyRect2(10,30);
			break;
		case 26:
			entityManager.addEnemy4(14,70);
			entityManager.addEnemy5(10,30);
			entityManager.addEnemy4(16,70);
			entityManager.addEnemySum1(1,0);
			entityManager.addEnemy4(5,70);
			entityManager.addEnemySum1(2,20);
			entityManager.addEnemy4(14,70);
			entityManager.addEnemy4(15,30);
			entityManager.addEnemy4(16,70);
			break;
		case 27:
			entityManager.addEnemyRect2(10,60);
			entityManager.addEnemy4(15,4);
			entityManager.addEnemyRect2(10,60);
			entityManager.addEnemy4(15,4);
			entityManager.addEnemyRect2(10,60);
			entityManager.addEnemy4(20,3);
			entityManager.addEnemyRect2(10,60);
			break;
		case 28:
			entityManager.addEnemy1(20,50);
			entityManager.addEnemy2(20,40);
			entityManager.addEnemy3(15,30);
			entityManager.addEnemy4(15,20);
			entityManager.addEnemy5(15,20);
			entityManager.addEnemySum1(3,40);
			break;
		case 29:
			entityManager.addBoss1(1,0);
			break;
		case 30:
			entityManager.addEnemy4(8,25);
			entityManager.addEnemy5(10,30);
			entityManager.addEnemy5(5,15);
			entityManager.addEnemy4(13,30);
			entityManager.addDelay(90);
			entityManager.addEnemy4(12,9);
			entityManager.addEnemy4(4,20);
			entityManager.addDelay(100);
			entityManager.addEnemy5(7,30);
			entityManager.addEnemy4(16,40);
			break;
		case 31:
			entityManager.addEnemy4(3,20);
			entityManager.addDelay(30);
			entityManager.addEnemySum1(5,60);
			entityManager.addDelay(120);
			entityManager.addEnemySum1(5,60);
			entityManager.addDelay(120);
			entityManager.addEnemySum1(5,30);
			break;
		case 32:
			
			break;
			
		}
	}
}
