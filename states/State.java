package com.TowerDefense.game.states;

import java.awt.Graphics;

import com.TowerDefense.game.Game;

public abstract class State {
	private static State currentState = null;
	protected Game game;
	
	public State(Game game) {
		this.game = game;
	}
	
	public static void setState(State newState) {
		currentState = newState;
	}
	
	public static State getState() {
		return currentState;
	}
	
	public abstract void tick();
	public abstract void render(Graphics g);
}
