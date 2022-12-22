package com.TowerDefense.game.pathFinding;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.TowerDefense.game.display.Display;

public class Grid {
	private Node[][] grid;
	public int nodeSize;
	private int gridSizeX,gridSizeY;
	private Path[] paths;
	
	public Grid(int nodeSize, int shopWidth, Path... paths) {
		this.nodeSize = nodeSize;
		this.paths = paths;
		gridSizeX = Math.round((Display.WIDTH-shopWidth)/nodeSize);
		gridSizeY = Math.round(Display.HEIGHT/nodeSize);
		grid = new Node[gridSizeY][gridSizeX];
		
		//create grid
		for (int y=0; y<gridSizeY; y++) {
			for (int x=0; x<gridSizeX; x++) {
				Point2 point = new Point2(x*nodeSize,y*nodeSize);
				boolean walkable = false, validity = true;
				int movementPenalty = 100;
				
				for (Path p: paths) {
					if (Path.contains(point, p)) {
						walkable = true;
						validity = false;
						movementPenalty = 0;
					}
				}
				
				grid[y][x] = new Node(point,walkable,validity,movementPenalty);
			}
		}
		
		blurPenaltyMap(2);
	}
	
	public Node getNodeFromPoint(Point2 p) {
		double nodeX = p.x/nodeSize;
		double nodeY = p.y/nodeSize;
		
		nodeX = Math.max(0, Math.min(nodeX, gridSizeX-1));
		nodeY = Math.max(0, Math.min(nodeY, gridSizeY-1));
		
		int x = Math.round((float)nodeX);
		int y = Math.round((float)nodeY);
		
		return grid[y][x];
	}
	
	public ArrayList<Node> getNeighbors(Node n) {
		ArrayList<Node> neighbors = new ArrayList<Node>();
		
		for (int y=-1; y<=1; y++) {
			for (int x=-1; x<=1; x++) {
				if (x == 0 && y == 0)
					continue;
				
				int checkX = n.x()/nodeSize+x;
				int checkY = n.y()/nodeSize+y;
				
				if (checkX >= 0 && checkX < gridSizeX && checkY >= 0 && checkY < gridSizeY && grid[checkY][checkX].walkable)
					neighbors.add(grid[checkY][checkX]);
			}
		}
		return neighbors;
	}
	
	private void blurPenaltyMap(int blurSize) {
		int kernelSize = blurSize *2 + 1;
		
		int[][] penHorpass = new int[gridSizeY][gridSizeX];
		int[][] penVerpass = new int[gridSizeY][gridSizeX];
		
		for (int y=0; y<gridSizeY; y++) {
			for (int x=-blurSize; x<=blurSize; x++) {
				int sampleX = Math.max(0, Math.min(x, blurSize));
				penHorpass[y][0] += grid[y][sampleX].movementPenalty;
			}
			
			for (int x=1; x<gridSizeX; x++) {
				int removeIndex = Math.max(0,Math.min(x-blurSize-1, gridSizeX));
				int addIndex = Math.max(0,Math.min(x+blurSize,gridSizeX-1));
				
				penHorpass[y][x] = penHorpass[y][x-1] - grid[y][removeIndex].movementPenalty + grid[y][addIndex].movementPenalty;
			}
		}
		
		for (int x=0; x<gridSizeX; x++) {
			for (int y=-blurSize; y<=blurSize; y++) {
				int sampleY = Math.max(0, Math.min(y, blurSize));
				penVerpass[0][x] += penHorpass[sampleY][x];
			}
			
			for (int y=1; y<gridSizeY; y++) {
				int removeIndex = Math.max(0,Math.min(y-blurSize-1, gridSizeY));
				int addIndex = Math.max(0,Math.min(y+blurSize,gridSizeY-1));
				
				penVerpass[y][x] = penVerpass[y-1][x] - penHorpass[removeIndex][x] + penHorpass[addIndex][x];
				
				int blurredPenalty = Math.round((float) penVerpass[y][x]/kernelSize*kernelSize);
				grid[y][x].movementPenalty = blurredPenalty;
			}
		}
	}
	
	public boolean setGridValidity(int x, int y, int width, int height) {
		ArrayList<Node> checkedNodes = new ArrayList<Node>();
		for (Node[] i: grid) {
			for (Node n: i) {
				if (n.point.x >= x-width/2 && n.point.x < x+width/2 && n.point.y >= y-height/2 && n.point.y < y+height/2) {
					if (!n.validity)
						return false;
					checkedNodes.add(n);
				}
			}
		}
		for (Node n: checkedNodes) {
			n.validity = false;
		}
		return true;
	}
	
	public void updateGridValidity(int x, int y, int width, int height) {
		for (Node[] i: grid) {
			for (Node n: i) {
				if (n.point.x >= x-width/2 && n.point.x < x+width/2 && n.point.y >= y-height/2 && n.point.y < y+height/2) {
					if (n.walkable)
						continue;
					n.validity = true;
				}
			}
		}
	}

	
	public int maxSize() {
		return gridSizeX * gridSizeY;
	}
	
	public void render(Graphics g) {
		for (Path p: paths) {
			p.render(g);
		}
//		for (Node[] i: grid) {
//			for (Node n: i) {
//				if (n.path) {
//					g.setColor(Color.red);
//					g.fillRect(n.x(), n.y(), nodeSize-1, nodeSize-1);
//				}
////				else if (!n.walkable)
////					g.setColor(Color.gray);
////				else
////					g.setColor(Color.green);
//				
//			}
//		}
	}
}