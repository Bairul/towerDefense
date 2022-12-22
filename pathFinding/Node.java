package com.TowerDefense.game.pathFinding;

public class Node extends IHeapItem<Node>{
	public Point2 point;
	public int hCost,gCost;
	private Node parent;
	public boolean walkable,validity;
	public int movementPenalty;
	
	public boolean path;
	
	public Node(Point2 point, boolean walkable, boolean validity, int penalty) {
		this.point = point;
		this.walkable = walkable;
		movementPenalty = penalty;
		this.validity = validity;
	}
	
	public int x() {
		return point.intX();
	}
	
	public int y() {
		return point.intY();
	}
	
	public int fCost() {
		return hCost+gCost;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}

	@Override
	public int compareTo(Node other) {
		Integer fCost = fCost();
		
		int compare = fCost.compareTo(other.fCost());
		if (fCost == other.fCost()) {
			Integer hCost = this.hCost;
			compare = hCost.compareTo(other.hCost);
		}
		
		return -compare;
	}
}
