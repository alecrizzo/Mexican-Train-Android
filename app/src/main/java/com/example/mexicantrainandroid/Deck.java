/************************************************************
 * Name:  Alec Rizzo                                        *
 * Project: Project 4 - Mexican Train Java Android          *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date: 12/8/2021                                          *
 ************************************************************/
package com.example.mexicantrainandroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ArrayList;

public class Deck {

	ArrayList<Tile> double_nine_set = new ArrayList<Tile>();
	private final int DOUBLE_NINE_SIZE = 55;
	
	// There are better ways to setup this constructor but I want to keep the code
	// as similar to the C++ as possible, and ArrayList addAll was giving issues
	public Deck()
	{
		Tile tile_1 = new Tile(0, 0);
		Tile tile_2 = new Tile(0, 1);
		Tile tile_3 = new Tile(0, 2);
		Tile tile_4 = new Tile(0, 3);
		Tile tile_5 = new Tile(0, 4);
		Tile tile_6 = new Tile(0, 5);
		Tile tile_7 = new Tile(0, 6);
		Tile tile_8 = new Tile(0, 7);
		Tile tile_9 = new Tile(0, 8);
		Tile tile_10 = new Tile(0, 9);
		Tile tile_11 = new Tile(1, 1);
		Tile tile_12 = new Tile(1, 2);
		Tile tile_13 = new Tile(1, 3);
		Tile tile_14 = new Tile(1, 4);
		Tile tile_15 = new Tile(1, 5);
		Tile tile_16 = new Tile(1, 6);
		Tile tile_17 = new Tile(1, 7);
		Tile tile_18 = new Tile(1, 8);
		Tile tile_19 = new Tile(1, 9);
		Tile tile_20 = new Tile(2, 2);
		Tile tile_21 = new Tile(2, 3);
		Tile tile_22 = new Tile(2, 4);
		Tile tile_23 = new Tile(2, 5);
		Tile tile_24 = new Tile(2, 6);
		Tile tile_25 = new Tile(2, 7);
		Tile tile_26 = new Tile(2, 8);
		Tile tile_27 = new Tile(2, 9);
		Tile tile_28 = new Tile(3, 3);
		Tile tile_29 = new Tile(3, 4);
		Tile tile_30 = new Tile(3, 5);
		Tile tile_31 = new Tile(3, 6);
		Tile tile_32 = new Tile(3, 7);
		Tile tile_33 = new Tile(3, 8);
		Tile tile_34 = new Tile(3, 9);
		Tile tile_35 = new Tile(4, 4);
		Tile tile_36 = new Tile(4, 5);
		Tile tile_37 = new Tile(4, 6);
		Tile tile_38 = new Tile(4, 7);
		Tile tile_39 = new Tile(4, 8);
		Tile tile_40 = new Tile(4, 9);
		Tile tile_41 = new Tile(5, 5);
		Tile tile_42 = new Tile(5, 6);
		Tile tile_43 = new Tile(5, 7);
		Tile tile_44 = new Tile(5, 8);
		Tile tile_45 = new Tile(5, 9);
		Tile tile_46 = new Tile(6, 6);
		Tile tile_47 = new Tile(6, 7);
		Tile tile_48 = new Tile(6, 8);
		Tile tile_49 = new Tile(6, 9);
		Tile tile_50 = new Tile(7, 7);
		Tile tile_51 = new Tile(7, 8);
		Tile tile_52 = new Tile(7, 9);
		Tile tile_53 = new Tile(8, 8);
		Tile tile_54 = new Tile(8, 9);
		Tile tile_55 = new Tile(9, 9);
		
		double_nine_set.add(tile_1);
		double_nine_set.add(tile_2);
		double_nine_set.add(tile_3);
		double_nine_set.add(tile_4);
		double_nine_set.add(tile_5);
		double_nine_set.add(tile_6);
		double_nine_set.add(tile_7);
		double_nine_set.add(tile_8);
		double_nine_set.add(tile_9);
		double_nine_set.add(tile_10);
		double_nine_set.add(tile_11);
		double_nine_set.add(tile_12);
		double_nine_set.add(tile_13);
		double_nine_set.add(tile_14);
		double_nine_set.add(tile_15);
		double_nine_set.add(tile_16);
		double_nine_set.add(tile_17);
		double_nine_set.add(tile_18);
		double_nine_set.add(tile_19);
		double_nine_set.add(tile_20);
		double_nine_set.add(tile_21);
		double_nine_set.add(tile_22);
		double_nine_set.add(tile_23);
		double_nine_set.add(tile_24);
		double_nine_set.add(tile_25);
		double_nine_set.add(tile_26);
		double_nine_set.add(tile_27);
		double_nine_set.add(tile_28);
		double_nine_set.add(tile_29);
		double_nine_set.add(tile_30);
		double_nine_set.add(tile_31);
		double_nine_set.add(tile_32);
		double_nine_set.add(tile_33);
		double_nine_set.add(tile_34);
		double_nine_set.add(tile_35);
		double_nine_set.add(tile_36);
		double_nine_set.add(tile_37);
		double_nine_set.add(tile_38);
		double_nine_set.add(tile_39);
		double_nine_set.add(tile_40);
		double_nine_set.add(tile_41);
		double_nine_set.add(tile_42);
		double_nine_set.add(tile_43);
		double_nine_set.add(tile_44);
		double_nine_set.add(tile_45);
		double_nine_set.add(tile_46);
		double_nine_set.add(tile_47);
		double_nine_set.add(tile_48);
		double_nine_set.add(tile_49);
		double_nine_set.add(tile_50);
		double_nine_set.add(tile_51);
		double_nine_set.add(tile_52);
		double_nine_set.add(tile_53);
		double_nine_set.add(tile_54);
		double_nine_set.add(tile_55);
	} // End of Deck()
	
	
	/* *********************************************************************
	Function Name: popNumTiles
	Purpose: pop the number of tiles equal to int num to the ArrayList passed
	Parameters:
				stack, ArrayList of tiles passed by reference
				num, integer of how many tiles to pop passed by value
	Return Value: void
	Algorithm:
				1) pop the tiles and push them to the passed ArrayList if the 
					deck has enough tiles left
	Assistance Received: none
	********************************************************************* */
	protected void popNumTiles(ArrayList<Tile> stack, int num)
	{
		if (!(this.double_nine_set.size() < num))
		{
			for (int i = 0; i < num; i++)
			{
				stack.add(this.double_nine_set.get(this.double_nine_set.size()-1));
				this.double_nine_set.remove(this.double_nine_set.size()-1);
			}
		}
	}// End of popNumTiles()

	public int getDOUBLE_NINE_SIZE() {
		return DOUBLE_NINE_SIZE;
	}
	
	/* *********************************************************************
	Function Name: shuffleDeck
	Purpose: shuffle the double nine set
	Parameters:
	Return Value: void
	Algorithm:
				1) use Collections shuffle on double_nine_set
	Assistance Received: Got help from: https://www.geeksforgeeks.org/java-program-to-shuffle-ArrayList-elements/
	********************************************************************* */
	protected void shuffleDeck() 
	{
		// Use shuffle method from collections class to shuffle elements
		Collections.shuffle(this.double_nine_set);
			
	}// End of shuffleDeck()
	
}
