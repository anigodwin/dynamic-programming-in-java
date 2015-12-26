package com.dprogram;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a model of a Key in the keypad.
 * 
 * <p>Key is described by its value and associated position or coordinate
 * in the keypad. Each key is aware of the possible next key presses,
 * which must be a knight move from the key. However, valid Knight moves
 * must be within the Keypad as depicted below. 
 * 
 * @author godwin
 *
 */
public enum Key {

	A(1,1), B(1,2), C(1,3), D(1,4), E(1,5),
	F(2,1), G(2,2), H(2,3), I(2,4), J(2,5),
	K(3,1), L(3,2), M(3,3), N(3,4), O(3,5),
	       _1(4,2),_2(4,3),_3(4,4);
	
	private final int row;
	private final int col;
	
	
	private Key(final int row, final int col) {
		this.row = row;
		this.col = col;
	}

	/**
	 * Returns all valid knight moves (Keys) from the this Key position.
	 * 
	 * @return a list of Keys
	 */
	public final List<Key> adjacentKnightMoves() {
		List<Key> moves = new ArrayList<>();
		for (Key k : values()) {
			if (isValidKnightMove(k)) moves.add(k);
		}
		
		return moves;
	}

	private boolean isValidKnightMove(final Key k) {
		int rowUp = row + 1;
		int rowDown = row - 1;
		int row2Up = row + 2;
		int row2Down = row - 2;
		
		int colUp = col + 1;
		int colDown = col - 1;
		int col2Up = col + 2;
		int col2Down = col - 2;
		
		return k.hasCoord(rowUp,col2Up) || k.hasCoord(rowUp, col2Down) || k.hasCoord(row2Up, colUp) || 
				k.hasCoord(row2Up, colDown) || k.hasCoord(rowDown, col2Down) || k.hasCoord(rowDown, col2Up) ||
				k.hasCoord(row2Down, colDown) || k.hasCoord(row2Down, colUp);		
	}
	
	private boolean hasCoord(int row, int col) {
		return this.row == row && this.col == col;
	}
	
	/**
	 * Checks if this Key is a vowel or any of these [A,E,I,O] alphabet.
	 * 
	 * @return <code>true</code> if this is a vowel, otherwise returns <code>false</code>.
	 */
	public final boolean isVowel() {		
		return this == Key.A || this == Key.E || this == Key.I || this == Key.O;
	}
	
}
