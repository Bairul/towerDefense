package com.TowerDefense.game.pathFinding;

import java.util.ArrayList;
import java.util.HashSet;

public class PathFind {
	private Grid grid;
	
	public PathFind(Grid grid) {
		this.grid = grid;
	}
	
	public Point2[] pathFind(Point2 startP, Point2 endP) {
		Node startNode = grid.getNodeFromPoint(startP);
		Node endNode = grid.getNodeFromPoint(endP);
		
		Heap<Node> openSet = new Heap<Node>(grid.maxSize());
		HashSet<Node> closedSet = new HashSet<Node>();
		
		openSet.add(startNode);
		while (openSet.size() > 0) {
			Node currentNode = openSet.removeFirst();
			
			closedSet.add(currentNode);
			
			if (currentNode == endNode) {
				return retracePath(startNode,endNode);
			}
			
			for (Node neighbor: grid.getNeighbors(currentNode)) {
				if (closedSet.contains(neighbor))
					continue;
				
				int newMvtCostToNeighbor = currentNode.gCost + nodeDist(currentNode,neighbor) + neighbor.movementPenalty;
				if (newMvtCostToNeighbor < neighbor.gCost || !openSet.contains(neighbor)) {
					neighbor.gCost = newMvtCostToNeighbor;
					neighbor.hCost = nodeDist(neighbor,endNode);
					neighbor.setParent(currentNode);
					
					if (!openSet.contains(neighbor))
						openSet.add(neighbor);
					else
						openSet.updateItem(neighbor);
				}
			}
		}
		return null;
	}
	
	private Point2[] retracePath(Node startNode, Node endNode) {
		Node currentNode = endNode;
		ArrayList<Node> path = new ArrayList<Node>();
		
		while (currentNode != startNode) {
			path.add(currentNode);
			currentNode.path = true;
			currentNode = currentNode.getParent();
		}
		
		for (int i=0; i<path.size()/2; i++) {
			Node temp = path.get(i);
			
			int index = path.size()-1-i;
			path.set(i, path.get(index));
			path.set(index, temp);
		}
		
		return simplifyPath(path);
	}
	
	private Point2[] simplifyPath(ArrayList<Node> path) {
		ArrayList<Point2> wayPoints = new ArrayList<Point2>();
		double directionOld = 0;
		
		for (int i=0; i<path.size()-1; i++) {
			Node currentNode = path.get(i);
			Node nextNode = path.get(i+1);
			double directionNew = currentNode.point.getDirection(nextNode.point);
			
			if (directionNew != directionOld) {
				directionOld = directionNew;
				
				wayPoints.add(new Point2(currentNode.point.x+grid.nodeSize/2,currentNode.point.y+grid.nodeSize/2));
			}
		}
		Point2 endPoint = new Point2(path.get(path.size()-1).point.x+grid.nodeSize/2,path.get(path.size()-1).point.y+grid.nodeSize/2);
		wayPoints.add(endPoint);
		return wayPoints.toArray(new Point2[0]);
	}
	
	private int nodeDist(Node n1, Node n2) {
		double distX = Math.abs(n1.point.x - n2.point.x);
		double distY = Math.abs(n1.point.y - n2.point.y);
		
		if (distX > distY)
			return (int) (14*distY+10*(distX-distY));
		return (int) (14*distX+10*(distY-distX));
	}
}
