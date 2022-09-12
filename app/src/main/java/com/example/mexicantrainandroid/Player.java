/************************************************************
 * Name:  Alec Rizzo                                        *
 * Project: Project 4 - Mexican Train Java Android          *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date: 12/8/2021                                          *
 ************************************************************/
package com.example.mexicantrainandroid;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements Serializable {
	protected int score;
	protected ArrayList<Tile> hand = new ArrayList<Tile>();
	protected ArrayList<Tile> train = new ArrayList<Tile>();	

	// false is no marker - true is yes marker
	protected boolean marker;
	// false is no orphan double on train - true is yes
	protected boolean orphanDouble;

	
	/* *********************************************************************
	Function Name: addTileHand()
	Purpose: add tile to the hand
	Parameters:
				t, tile to add to the hand passed by value
	Return Value: void
	Algorithm: push the tile t onto the member hand
	Assistance Received: none
	********************************************************************* */
	protected void addTileHand(Tile t)
	{
		this.hand.add(t);
	} // End of addTileHand()
	
	/* *********************************************************************
	Function Name: removeTileHand()
	Purpose: removes the passed tile from hand
	Parameters:
				t, tile to remove from the hand
	Return Value: void
	Algorithm:  if a matching tile exists in the hand erase it from the hand
	Assistance Received: none
	********************************************************************* */
	protected void removeTileHand(Tile t)
	{
		for (int i = 0; i < this.hand.size(); i++)
		{
			// If matching tile exists in hand remove the tile 
			if ((t.getSideA() == this.hand.get(i).getSideA() && t.getSideB() == this.hand.get(i).getSideB()) 
					|| ((t.getSideB() == this.hand.get(i).getSideA() && t.getSideA() == this.hand.get(i).getSideB())))
			{
				this.hand.remove(i);
				return;
			}
		}
	} // End of removeTileHand()
	
	/* *********************************************************************
	Function Name: addTileComputerTrain()
	Purpose: add tile to the computer train
	Parameters:
				t, tile to add to the train passed by value
				computer, Player object to insert the values onto
	Return Value: void
	Algorithm: insert the tile t onto the passed computer players train at the beginning
	Assistance Received: none
	********************************************************************* */
	protected void addTileComputerTrain(Tile t, Player computer)
	{
		computer.train.add(0, t);
	} // End of addTileComputerTrain()
	
	
	/* *********************************************************************
	Function Name: addTileTrain()
	Purpose: add tile to the train
	Parameters:	
				t, tile to add to the train passed by value
				t_train, ArrayList of tiles to push the tile onto
	Return Value: void
	Algorithm: push the tile t onto the passed train t_train
	Assistance Received: none
	********************************************************************* */
	// t_train was passed by reference in C++
	// When calling this function be sure to use an object to make sure
	// the values are adjusted
	protected void addTileTrain(Tile t, ArrayList<Tile> t_train)
	{
		t_train.add(t);
	}// End of addTileTrain()

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	/* *********************************************************************
	Function Name: getOrphan()
	Purpose: get the orphan double value from the object
	Parameters:
	Return Value: bool
	Algorithm:  return the member value of orphanDouble
	Assistance Received: none
	********************************************************************* */
	public boolean getOrphan()
	{
		return this.orphanDouble;
	}// End of getOrphan()
	
	/* *********************************************************************
	Function Name: printHand()
	Purpose: print the tiles in the Players hand
	Parameters:
	Return Value: void
	Algorithm:
				1) for loop to print all values in hand
	Assistance Received: none
	********************************************************************* */
	public void printHand()
	{
		for (int i = 0; i < this.hand.size(); i++)
		{
			this.hand.get(i).printTile();
			System.out.print(" ");
		}
	}// End of printHand()
	
	/* *********************************************************************
	Function Name: getTrain()
	Purpose: get the train
	Parameters:	none
	Return Value: ArrayList<Tile>
	Algorithm: return the train
	Assistance Received: none
	********************************************************************* */
	public ArrayList<Tile> getTrain()
	{
		return this.train;
	}// End of getTrain()
	
	/* *********************************************************************
	Function Name: getMarker()
	Purpose: get the marker
	Parameters:	none
	Return Value: bool
	Algorithm: return the marker
	Assistance Received: none
	********************************************************************* */
	public boolean getMarker()
	{
		return this.marker;
	}// End of getMarker()

	/* *********************************************************************
	Function Name: printTrain()
	Purpose: print the tiles in the Players train
	Parameters:
	Return Value: void
	Algorithm:
				1) for loop to print all values in train
	Assistance Received: none
	********************************************************************* */
	public void printTrain(Tile engine)
	{
		for (int i = 0; i < this.train.size(); i++)
		{
			if (this.train.get(i).getSideA() == engine.getSideA() && this.train.get(i).getSideB() == engine.getSideB())
			{
				continue;
			}
			else
			{
				this.train.get(i).printTile();
				System.out.print(" ");
			}
		}
	}// End of printTrain()
	
	/* *********************************************************************
	Function Name: printM()
	Purpose: print M if there is a marker on the train
	Parameters:
	Return Value: void
	Algorithm:
				1) if marked train, print M to show marker in output
	Assistance Received: none
	********************************************************************* */
	public void printM()
	{
		if (this.marker == true)
		{
			System.out.print("M");
		}
	}// End of printM
	
	/* *********************************************************************
	Function Name: playableTilesToComputerTrain()
	Purpose: get the playable tiles ArrayList to the passed computer train
	Parameters:
				t_train, ArrayList of tiles to compare our hand values to, passed by value
	Return Value: ArrayList<Tile> 
	Algorithm:  use isTilePlayableToComputer and if so addTileTrain to our temp ArrayList
				return our temp ArrayList after looping through the values
	Assistance Received: none
	********************************************************************* */
	public ArrayList<Tile> playableTilesToComputerTrain(ArrayList<Tile> t_train)
	{
		ArrayList<Tile> playable_tiles = new ArrayList<Tile>();
		for (int i = 0; i < this.hand.size(); i++)
		{
			if (isTilePlayableToComputer(this.hand.get(i), t_train) == true)
			{
				// !HERE this call might be an issue where it wont change values of playable_tiles
				addTileTrain(this.hand.get(i), playable_tiles);
			}
		}
		return playable_tiles;
	}// End of playableTilesToComputerTrain()
	
	/* *********************************************************************
	Function Name: isTilePlayableToComputer()
	Purpose: determine if the passed tile is playable to the passed train
	Parameters:
				tile, Tile object by value
				computer_train, ArrayList of Tiles by value
	Return Value: bool
	Algorithm:  check if the sideA of the first value in passed train is equal to either
				side of our passed tile. if so return true, otherwise return false
	Assistance Received: none
	********************************************************************* */
	public boolean isTilePlayableToComputer(Tile tile, ArrayList<Tile> computer_train)
	{
		if (computer_train.size() > 0)
		{
			int trainTail = computer_train.get(0).getSideA();
			if (tile.getSideA() == trainTail || tile.getSideB() == trainTail)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return false;
	}// End of isTilePlayableToComputer()
	
	/* *********************************************************************
	Function Name: playableTilesToTrain()
	Purpose: gets playable tiles to one specific train
	Parameters:
				t_train, ArrayList of tiles to check if this hand is playable to, passed by value
	Return Value: ArrayList<Tile>
	Algorithm:  use isTilePlayable for all values in hand on the passed train to determine
				if the tiles in hand are playable
	Assistance Received: none
	********************************************************************* */
	public ArrayList<Tile> playableTilesToTrain(ArrayList<Tile> t_train)
	{
		ArrayList<Tile> playable_tiles = new ArrayList<Tile>();
		for (int i = 0; i < this.hand.size(); i++)
		{
			if (isTilePlayable(this.hand.get(i), t_train) == true)
			{
				addTileTrain(this.hand.get(i), playable_tiles);
			}
		}
		return playable_tiles;
	}// End of playableTilestoTrain()
	
	/* *********************************************************************
	Function Name: playableTiles()
	Purpose: return a ArrayList of playable tiles in the hand
	Parameters:
				mex_train, ArrayList of tiles for mexican train by value
				computer, Player computer passed by value
				mexOrphan, mexican train orphan value passed by value
	Return Value: ArrayList<Tile>
	Algorithm:  use isTilePlayable to determine playable tiles, add them to our
				ArrayList and when done return the playable tiles ArrayList
	Assistance Received: none
	********************************************************************* */
	// Returns a ArrayList of the playable tiles in the players hand to mexican train computer train and personal train
	public ArrayList<Tile> playableTiles(ArrayList<Tile> mex_train, Player computer, boolean mexOrphan)
	{
		ArrayList<Tile> playable_tiles = new ArrayList<Tile>();
		for (int i = 0; i < this.hand.size(); i++)
		{
			if (isTilePlayable(this.hand.get(i), mex_train) == true ||
				isTilePlayable(this.hand.get(i), this.train) == true ||
				isTilePlayableToComputer(this.hand.get(i), computer.train) == true)
			{
				addTileTrain(this.hand.get(i), playable_tiles);
			}
		}
		return playable_tiles;
	}// End of playableTiles()
	
	/* *********************************************************************
	Function Name: isTilePlayable()
	Purpose: determine if the passed tile is playable to the passed train
	Parameters:
				tile, Tile object by value
				train, ArrayList of Tiles by value
	Return Value: bool
	Algorithm:  check if the sideB of the last value in passed train is equal to either
				side of our passed tile. if so return true, otherwise return false
	Assistance Received: none
	********************************************************************* */
	public boolean isTilePlayable(Tile tile, ArrayList<Tile> train)
	{
		if (train.size() > 0)
		{
			int trainTail = train.get(train.size() -1).getSideB();
			if (tile.getSideA() == trainTail || tile.getSideB() == trainTail)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}// End of isTilePlayable()

	/* *********************************************************************
	Function Name: addScore
	Purpose: add the passed points to our member value of score
	Parameters: 
				points, integer passed by value
	Return Value: void
	Algorithm: update our score variable
	Assistance Received: none
	********************************************************************* */
	public void addScore(int points)
	{
		this.score += points;
	}// End of addScore()
	
	/* *********************************************************************
	Function Name: calculateTotalPips()
	Purpose: calculate the total pips to update score at end of round
	Parameters: none
	Return Value: int
	Algorithm:  loop through all our hands values and add up the pips to pipScore
	Assistance Received: none
	********************************************************************* */
	public int calculateTotalPips()
	{
		int pipScore = 0;
		for (int i = 0; i < this.hand.size(); i++)
		{
			pipScore += this.hand.get(i).getSideA();
			pipScore += this.hand.get(i).getSideB();
		}

		return pipScore;
	}// End of calculateTotalPips()
	
	
	/* *********************************************************************
	Function Name: newRoundReset
	Purpose: reset all the necessary values for new round
	Parameters: none
	Return Value: void
	Algorithm: reset our hand, train, orphanDouble and marker
	Assistance Received: none
	********************************************************************* */
	public void newRoundReset()
	{
		clearHand();
		clearTrain();
		clearOrphan();
		clearMarker();
	}// End of newRoundReset()
	
	/* *********************************************************************
	Function Name: clearHand
	Purpose: clear the hand ArrayList
	Parameters: none
	Return Value: void
	Algorithm: clear our hand ArrayList
	Assistance Received: none
	********************************************************************* */
	private void clearHand()
	{
		this.hand.clear();
	}// End of clearHand()
	
	/* *********************************************************************
	Function Name: clearTrain
	Purpose: clear the train ArrayList
	Parameters: none
	Return Value: void
	Algorithm: clear our train ArrayList
	Assistance Received: none
	********************************************************************* */
	private void clearTrain()
	{
		this.train.clear();
	}// End of clearTrain()
	
	/* *********************************************************************
	Function Name: clearOrphan
	Purpose: set the orphandouble member value to false
	Parameters: none
	Return Value: void
	Algorithm: reset our orphanDouble value
	Assistance Received: none
	********************************************************************* */
	private void clearOrphan() 
	{
		this.orphanDouble = false;
	}// End of clearOrphan()

	/* *********************************************************************
	Function Name: clearMarker
	Purpose: set the marker member value to false
	Parameters: none
	Return Value: void
	Algorithm: reset our marker value
	Assistance Received: none
	********************************************************************* */
	private void clearMarker() 
	{
		this.marker = false;
	}// End of clearMarker()
	
	// mex_train and computer were pass by ref in C++
	public void playTile(Tile tile, String trainChoice, ArrayList<Tile> mex_train, Player computer)
	{
	}
	public void playTurn()
	{
	}
	
	/* *********************************************************************
	Function Name: isHandElligibleTrain()
	Purpose: determine if the hand is elligible to play on the passed train
	Parameters:
				elligible_train, ArrayList of tiles to see if we can play to
	Return Value: bool
	Algorithm:  check if the sideB of the last value in passed train is equal to either
				side of our hands tiles. if so any of them are elligible return true,
				otherwise false.
	Assistance Received: none
	********************************************************************* */
	public boolean isHandElligibleTrain(ArrayList<Tile> elligible_train)
	{
		if (elligible_train.size() == 0)
		{
			return false;
		}
		else
		{
			int trainTail = elligible_train.get(elligible_train.size() -1).getSideB();
			for (int i = 0; i < hand.size(); i++)
			{
				if (trainTail == hand.get(i).getSideA() || trainTail == hand.get(i).getSideB())
				{
					return true;
				}
			}
			return false;
		}
	}// End of isHandElligibleTrain()
	
	/* *********************************************************************
	Function Name: isHandElligibleComputerTrain()
	Purpose: determine if the hand is elligible to play on the computer train
	Parameters:
				computer_train, ArrayList of tiles to compare our hand values to, passed by value
	Return Value: bool
	Algorithm:  check if the sideA of the first value in passed train is equal to either
				side of our passed hands tiles. if so any of them are elligible return true,
				otherwise false.
	Assistance Received: none
	********************************************************************* */
	public boolean isHandElligibleComputerTrain(ArrayList<Tile> computer_train)
	{
		// Computer train faces left so the concept is the same but the 
		// syntax is slightly flipped from isHandElligibleTrain
		if (computer_train.size() > 0)
		{
			int compTail = computer_train.get(0).getSideA();
			for (int i = 0; i < hand.size(); i++)
			{
				if (compTail == hand.get(i).getSideA() || compTail == hand.get(i).getSideB())
				{
					return true;
				}
			}
			return false;
		}
		return false;
	}// End of isHandElligibleComputerTrain()

	/* *********************************************************************
	Function Name: setMarker()
	Purpose: set merker to the passed marker value
	Parameters:	mark, boolean for if train is marked passed by value
	Return Value: void
	Algorithm: assign value
	Assistance Received: none
	********************************************************************* */
	public void setMarker(boolean mark)
	{
		this.marker = mark;
	}// End of setMarker()
	
	/* *********************************************************************
	Function Name: flipTileForComputerTrain()
	Purpose: flips a tile to the proper orientation for the computer train
	Parameters:
				tile, Tile object passed by reference
				computer_train, ArrayList of tiles passed by value
	Return Value: void
	Algorithm:  check the start of the trains sideA and compare it to both sideA and SideB of
				our passed tile. flip the tile if needed
	Assistance Received: none
	********************************************************************* */
	public void flipTileForComputerTrain(Tile tile, ArrayList<Tile> computer_train)
	{
		if (computer_train.size() > 0)
		{
			if (computer_train.get(0).getSideA() == tile.getSideB())
			{
				return;
			}
			else if (computer_train.get(0).getSideA() == tile.getSideA())
			{
				// Flip values to the computers logic on the board (backwards)
				int tempside = tile.getSideA();
				tile.setSideA(tile.getSideB());
				tile.setSideB(tempside);

				return;
			}
			else
			{
				System.out.print("WHAT?! Error using flipTileForComputerTrain with ");
				tile.printTile();
				System.out.print(" to ");
				computer_train.get(0).printTile();
				System.exit(1);
			}
		}
	}// End of flipTileForComputerTrain()
	
	/* *********************************************************************
	Function Name: flipTileForTrain()
	Purpose: flips a tile to the proper orientation for the passed train
	Parameters:
				tile, Tile object passed by reference
				passed_train, ArrayList of tiles passed by value
	Return Value: void
	Algorithm:  check the end of the trains sideB and compare it to both sideA and SideB of
				our passed tile. flip the tile if needed
	Assistance Received: none
	********************************************************************* */
	public void flipTileForTrain(Tile tile, ArrayList<Tile> passed_train)
	{
		if (passed_train.size() > 0)
		{
			if (passed_train.get(passed_train.size()-1).getSideB() == tile.getSideA())
			{
				return;
			}
			else if (passed_train.get(passed_train.size()-1).getSideB() == tile.getSideB())
			{
				// Flip values to the proper display for the train
				int tempside = tile.getSideA();
				tile.setSideA(tile.getSideB());
				tile.setSideB(tempside);

				return;
			}
			else
			{
				System.out.print("WHAT?! Error using flipTileForTrain with ");
				tile.printTile();
				System.out.print(" to ");
				passed_train.get(passed_train.size()-1).printTile();
				System.exit(1);
			}
		}
		else
		{
			System.out.print("Error at flipTileForTrain, passed train has no values to compare\n");
			System.exit(1);
		}

	}// End of flipTileForTrain()
	
	/* *********************************************************************
	Function Name: setOrphan()
	Purpose: set the orphan double member to the passed boolean
	Parameters:
				orphan, boolean passed by value
	Return Value: void
	Algorithm:  set the member value to the parameter
	Assistance Received: none
	********************************************************************* */
	public void setOrphan(boolean orphan)
	{
		this.orphanDouble = orphan;
	}// End of setOrphan()
	
	/* *********************************************************************
	Function Name: setHand()
	Purpose: set hand ArrayList to the passed ArrayList
	Parameters:	new_hand ArrayList of tiles passed by value
	Return Value: void
	Algorithm: assign value
	Assistance Received: none
	********************************************************************* */
	public void setHand(ArrayList<Tile> new_hand)
	{
		this.hand = new_hand;
	}// End of setHand()
	
	/* *********************************************************************
	Function Name: setTrain()
	Purpose: set train ArrayList to the passed ArrayList
	Parameters:	new_train ArrayList of tiles passed by value
	Return Value: void
	Algorithm: assign value
	Assistance Received: none
	********************************************************************* */
	public void setTrain(ArrayList<Tile> new_train)
	{
		this.train = new_train;
	}// End of setTrain
	
	/* *********************************************************************
	Function Name: isTileInHand()
	Purpose: determines if the tile passed is in the hand
	Parameters:
				tile, tile to check our hand for, passed by value
	Return Value: bool
	Algorithm:  if the values of the passed tile match our hands tile at i
				return true, otherwise return false
	Assistance Received: none
	********************************************************************* */
	public boolean isTileInHand(Tile tile)
	{
		for (int i = 0; i < this.hand.size(); i++)
		{
			if (tile.getSideA() == this.hand.get(i).getSideA() && tile.getSideB() == this.hand.get(i).getSideB())
			{
				return true;
			}
		}
		return false;
	}// End of isTileInHand()
	
	/* *********************************************************************
	Function Name: hasNonDoubleFollowUp()
	Purpose: determines if player has a non double follow up tile
	Parameters:
				doubleTile, tile to compare to, passed by value
	Return Value: bool
	Algorithm:  if a non double follow up is found return true
	Assistance Received: none
	********************************************************************* */
	public boolean hasNonDoubleFollowUp(Tile doubleTile)
	{
		for (int i = 0; i < this.hand.size(); i++)
		{
			if (doubleTile.getSideA() == this.hand.get(i).getSideA() && doubleTile.getSideB() == this.hand.get(i).getSideB())
			{
				// Found the passed double in the hand, skip
				continue;
			}
			else if (doubleTile.getSideA() == this.hand.get(i).getSideA() || doubleTile.getSideA() == this.hand.get(i).getSideB())
			{
				// Found a follow up to the passed double
				return true;
			}
		}
		// Couldn't find a follow up to passed double
		return false;
	}// End of hasNonDoubleFollowUp

	/* *********************************************************************
	Function Name: getHand()
	Purpose: get the hand
	Parameters:	none
	Return Value: ArrayList<Tile>
	Algorithm: return the hand
	Assistance Received: none
	********************************************************************* */
	public ArrayList<Tile> getHand()
	{
		return this.hand;
	}// End of getHand()
	
	/* *********************************************************************
	Function Name: getNonDoubleFollowUp()
	Purpose: get the non double follow up that we determined exists using hasNonDoubleFollowUp
	Parameters:
				doubleTile, tile to compare to to find a follow up, passed by value
	Return Value: void
	Algorithm:  if a non double follow up exists, return it
	Assistance Received: none
	********************************************************************* */
	// !ALEC might remove, do not remember the purpose of this and the logic does not seem correct
	public Tile getNonDoubleFollowUp(Tile doubleTile)
	{
		Tile tempTile = new Tile();
		for (int i = 0; i < this.hand.size(); i++)
		{
			if (doubleTile.getSideA() == this.hand.get(i).getSideA() && doubleTile.getSideB() == this.hand.get(i).getSideB())
			{
				// Found the passed double in the hand, skip
				continue;
			}
			else if (doubleTile.getSideA() == this.hand.get(i).getSideA() || doubleTile.getSideA() == this.hand.get(i).getSideB())
			{
				// Found a follow up to the passed double
				return tempTile;
			}
		}
		// Couldn't find a follow up to passed double
		return tempTile;
	}// End of getNonDoubleFollowUp
	
}
