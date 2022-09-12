/************************************************************
 * Name:  Alec Rizzo                                        *
 * Project: Project 4 - Mexican Train Java Android          *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date: 12/8/2021                                          *
 ************************************************************/
package com.example.mexicantrainandroid;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Collections;
import java.util.Scanner;
import java.util.ArrayList;

public class Human extends Player implements Serializable {

	/* *********************************************************************
	Function Name: playableTiles
	Purpose: determine if a two playable doubles case is possible
	Parameters:
				mex_train, mexican train ArrayList passed by value
				computer, computer class object passed by value

	Return Value: a ArrayList of the playable tiles in the players hand to the availible trains
	Algorithm:
				1) check using isTilePlayable() to each train and if yes we add to the ArrayList we are returning
				2) if getMarker() is true on the computer we check its values as well as they are playable values
	Assistance Received: none
	********************************************************************* */
	public ArrayList<Tile> playableTiles(ArrayList<Tile> mex_train, Computer computer)
	{
		ArrayList<Tile> playable_tiles = new ArrayList<Tile>();
		playable_tiles.clear();
		for (int i = 0; i < this.hand.size(); i++)
		{
			if (computer.getMarker() == false)
			{
				if (isTilePlayable(this.hand.get(i), mex_train) == true ||
					isTilePlayable(this.hand.get(i), this.train) == true)
				{
					addTileTrain(this.hand.get(i), playable_tiles);
				}
			}
			else
			{
				if (isTilePlayable(this.hand.get(i), mex_train) == true ||
					isTilePlayable(this.hand.get(i), this.train) == true ||
					isTilePlayableToComputer(this.hand.get(i), computer.train) == true)
				{
					addTileTrain(this.hand.get(i), playable_tiles);
				}
			}
		}
		return playable_tiles;
	}// End of playableTiles()
	
	/* *********************************************************************
	Function Name: playTurn
	Purpose: To let the user play through their turn following the games logic.
			 Activated via entering "2" in the menu 
	Parameters:
				mexican_train<>, passed by reference. Has the tiles of the mexican_train
				yard<>, passed by reference. Has the tiles of the boneyard
				copmuter, passed by reference. Other player of the game, of class type Computer()
				mexOrphan, passed by reference. Boolean value to indicate mexican train has an orphan double
				gameEnd, passed by reference. Boolean value to flag whether or not the game needs to be ended
	Return Value: void
	Algorithm:
				1) Check playable tiles using isHandElligibleTrain(ArrayList<Tiles> train)
					if none playable call function noPlayableTiles()
					if hand is playable to any elligible trains, check for orphan doubles 
						if orphan double is true and hand is not elligible to the train call noPlayableTiles()
						if orphan double is true and hand is elligible call playTile(pickTile(ArrayList<Tile> train), trainChoice ...)
				2) Else call selectTrain() to have the user select an elligible train they can play on and they will 
				   then call playTile(pickTile(ArrayList<Tile> train), trainChoice ...)
	Assistance Received: none
	********************************************************************* */
	// all values were originally passed by reference in C++
	public void playTurn(ArrayList<Tile> mex_train, ArrayList<Tile> yard, Computer computer, boolean mexOrphan, boolean gameEnd)
	{
		// Prompt user for input on their choice of tile
				String temp = "";
				String temp2 = "";
		    
				Tile userTile = new Tile();

				Intent intent = new Intent(this, HumanTurnActivity.class);
				startActivity(intent);

				// No playable tiles: If the player does not have a tile that can be played at the end of any eligible train
				if (isHandElligibleTrain(mex_train) == false && isHandElligibleTrain(this.train) == false && 
						(isHandElligibleComputerTrain(computer.getTrain()) == false || computer.getMarker() == false))
				{
					// If the boneyard is empty, the player passes their turn and puts a marker at the end of their personal train
					noPlayableTiles(mex_train, yard, computer, mexOrphan);
				}
				else if (isHandElligibleTrain(mex_train) == true || isHandElligibleTrain(this.train) == true || 
						(isHandElligibleComputerTrain(computer.getTrain()) == true && computer.getMarker() == true))
				{
					// Check orphan double case first as orphan double must be played upon rather than allowing selection
					if (this.getOrphan() == true)
					{

						//System.out.print("There is an Orphan Double on your train, you can only play to that train \n");
						if (isHandElligibleTrain(this.train) == false)
						{
							//System.out.print("No playable tiles to the Orphan Double\n");
							noPlayableTiles(mex_train, yard, computer, mexOrphan);
						}
						else
						{
							// Can play a value to the orphan double
							String trainChoice = "2";
							playTile(pickTile(this.train), trainChoice, mex_train, yard, computer, mexOrphan, gameEnd);
							this.setMarker(false);
							this.setOrphan(false);
						}
					}
					else if (computer.getOrphan() == true)
					{
						//System.out.print("There is an Orphan Double on the Computer's train, you can only play to that train \n");
						if (isHandElligibleComputerTrain(computer.getTrain()) == false)
						{
							//System.out.print("No playable tiles to the Orphan Double\n");
							noPlayableTiles(mex_train, yard, computer, mexOrphan);
						}
						else
						{
							// Play Tile Function to let user pick their tile to play onto the train
							String trainChoice = "3";
							playTile(pickTileComputerTrain(computer.getTrain()), trainChoice, mex_train, yard, computer, mexOrphan, gameEnd);
		    
							computer.setOrphan(false);
						}
					}
					else if (mexOrphan == true)
					{
						//System.out.print("There is an Orphan Double on the Mexican train, you can only play to that train \n");
						if (isHandElligibleTrain(mex_train) == false)
						{
							//System.out.print("No playable tiles to the Orphan Double\n");
							noPlayableTiles(mex_train, yard, computer, mexOrphan);
						}
						else
						{
							// Play Tile Function to let user pick their tile to play onto the train
							String trainChoice = "1";
							playTile(pickTile(mex_train), trainChoice, mex_train, yard, computer, mexOrphan, gameEnd);
		    
							mexOrphan = false;
						}
					}
					else
					{
						// Prompt user for the train choice they would like from elligible trains
						String trainChoice = selectTrain(mex_train, computer);
		    
						// Based on the train now ask them which tile to play from their hand, can only work with given train
						if (trainChoice.equals("1"))
						{
							playTile(pickTile(mex_train), trainChoice, mex_train, yard, computer, mexOrphan, gameEnd);
						}
						else if (trainChoice.equals("2"))
						{
							playTile(pickTile(this.train), trainChoice, mex_train, yard, computer, mexOrphan, gameEnd);
						}
						else if (trainChoice.equals("3"))
						{
							playTile(pickTileComputerTrain(computer.getTrain()), trainChoice, mex_train, yard, computer, mexOrphan, gameEnd);
						}
					}
				}
				else if ((computer.getOrphan() == true && isHandElligibleComputerTrain(computer.train) == false) || (mexOrphan == true && isHandElligibleTrain(mex_train) == false) || (this.getOrphan() == true && isHandElligibleTrain(this.train) == false))
				{
					// If you cannot play to the orphan double, call noPlayableTiles
					noPlayableTiles(mex_train, yard, computer, mexOrphan);
				}
		    
	}// End of playTurn()
	
	public void noPlayableTiles(ArrayList<Tile> mex_train, ArrayList<Tile> yard, Computer computer, boolean mexOrphan)
	{
		// If the boneyard is empty, the player passes their turn and puts a marker at the end of their personal train
		if (yard.size() == 0)
		{
			setMarker(true);
			return;
		}
		else
		{
			boolean mexPlayable = false;
			boolean computerPlayable = false;
			boolean humanPlayable = false;

			// If the boneyard is not empty, the player draws a tile from the boneyard and plays it immediately.
			this.hand.add(yard.get(0));
			yard.remove(0);

			//System.out.print("Drew tile ");
			this.hand.get(this.hand.size()-1).printTile();
			//System.out.print(" from the boneyard\n");

			if (isTilePlayableToComputer(this.hand.get(this.hand.size()-1), computer.getTrain()) == true && computer.getMarker() == true)
			{
				computerPlayable = true;
			}
			if (isTilePlayable(this.hand.get(this.hand.size()-1), mex_train) == true)
			{
				mexPlayable = true;
			}
			if (isTilePlayable(this.hand.get(this.hand.size()-1), this.train) == true)
			{
				humanPlayable = true;
			}

			// If orphan double, check only that condition as it takes full priority to the players available actions
			if (computer.getOrphan() == true)
			{
				if (computerPlayable == true)
				{
					// Flip tile/ Check if need to flip
					flipTileForComputerTrain(this.hand.get(this.hand.size()-1), computer.getTrain());
					// Play tile immediately to computers train
					addTileComputerTrain(this.hand.get(this.hand.size()-1), computer);
					// Let player know what happened
					//System.out.print("Played tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from boneyard to the orphan double\n");
					// Remove tile from hand
					this.hand.remove(this.hand.size()-1);
					// Change status of computers orphan double
					computer.setOrphan(false);

					return;
				}
				else
				{
					// Player must add card to their hand pass turn and mark their train
					// note: card already added to hand above
					setMarker(true);
					//System.out.print("Could not play tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from the boneyard. It is now in your hand.\n");
					return;
				}
			}
			// Check this trains orphan double case
			if (this.getOrphan() == true)
			{
				if (humanPlayable == true)
				{
					// Flip tile for this train
					flipTileForTrain(this.hand.get(this.hand.size()-1), this.train);
					// Play tile immediately to this train
					addTileTrain(this.hand.get(this.hand.size()-1), this.train);
					// Let player know what happened
					//System.out.print("Played tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from boneyard to the orphan double\n");
					// Remove tile from hand
					this.hand.remove(this.hand.size()-1);
					this.setOrphan(false);

					// When playing to personal train always set marker to false as playing to personal train removes markers
					this.setMarker(false);
					return;
				}
				else
				{
					// Player must add card to their hand pass turn and mark their train
					// note: card already added to hand above
					setMarker(true);
					//System.out.print("Could not play tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from the boneyard. It is now in your hand.\n");
					return;
				}
			}
			if (mexOrphan == true)
			{
				if (mexPlayable == true)
				{
					// Flip tile for mexican train
					flipTileForTrain(this.hand.get(this.hand.size()-1), mex_train);
					// Play tile immediately to this train
					addTileTrain(this.hand.get(this.hand.size()-1), mex_train);
					// Let player know what happened
					//System.out.print("Played tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from boneyard to the orphan double\n");
					// Remove tile from hand
					this.hand.remove(this.hand.size()-1);
					// Set the played trains orphan to false
					mexOrphan = false;

					return;
				}
				else
				{
					// Player must add card to their hand pass turn and mark their train
					// note: card already added to hand above
					setMarker(true);
					//System.out.print("Could not play tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from the boneyard. It is now in your hand.\n");
					return;
				}
			}
			else if (computerPlayable == true || humanPlayable == true || mexPlayable == true)
			{
						// let user pick train to play it onto via select train method
						//System.out.print("Select a train to play on");
						// If a train is elligible ask the user the card they would like to play and onto which train
						if (isTilePlayable(this.hand.get(this.hand.size()-1), mex_train) == true)
						{
							//System.out.print(" (1) Mexican Train");
						}
						if (isTilePlayable(this.hand.get(this.hand.size()-1), this.train) == true)
						{
							//System.out.print(" (2) Your Train");
						}
						if (isTilePlayableToComputer(this.hand.get(this.hand.size()-1), computer.getTrain()) == true && computer.getMarker() == true)
						{
							//System.out.print(" (3) Computers Train");
						}
						//System.out.print(": ");

						String temp = "";
						if (mexPlayable == true && humanPlayable == true && computerPlayable == true)
						{
							temp = new Scanner(System.in).nextLine();
							while (!(temp.equals("1") || temp.equals("2") || temp.equals("3")))
							{
								//System.out.print("Please enter a one of the stated values: ");
								temp = new Scanner(System.in).nextLine();
							}
						}
						else if (mexPlayable == true && humanPlayable == true)
						{
							temp = new Scanner(System.in).nextLine();
							while (!(temp.equals("1") || temp.equals("2")))
							{
								//System.out.print("Please enter a one of the stated values: ");
								temp = new Scanner(System.in).nextLine();
							}
						}
						else if (mexPlayable == true && computerPlayable == true)
						{
							temp = new Scanner(System.in).nextLine();
							while (!(temp.equals("1") || temp.equals("3")))
							{
								//System.out.print("Please enter a one of the stated values: ");
								temp = new Scanner(System.in).nextLine();
							}
						}
						else if (humanPlayable == true && computerPlayable == true)
						{
							temp = new Scanner(System.in).nextLine();
							while (!(temp.equals("2") || temp.equals("3")))
							{
								//System.out.print("Please enter a one of the stated values: ");
								temp = new Scanner(System.in).nextLine();
							}
						}
						else if (mexPlayable == true)
						{
							temp = new Scanner(System.in).nextLine();
							while (!(temp.equals("1")))
							{
								//System.out.print("Please enter a one of the stated values: ");
								temp = new Scanner(System.in).nextLine();
							}
						}
						else if (humanPlayable == true)
						{
							temp = new Scanner(System.in).nextLine();
							while (!(temp.equals("2")))
							{
								//System.out.print("Please enter a one of the stated values: ");
								temp = new Scanner(System.in).nextLine();
							}
						}
						else if (computerPlayable == true)
						{
							temp = new Scanner(System.in).nextLine();
							while (!(temp.equals("3")))
							{
								//System.out.print("Please enter a one of the stated values: ");
								temp = new Scanner(System.in).nextLine();
							}
						}

						if (temp.equals("1"))
						{
							// Flip tile for mexican train
							flipTileForTrain(this.hand.get(this.hand.size()-1), mex_train);
							// Play tile immediately to this train
							addTileTrain(this.hand.get(this.hand.size()-1), mex_train);
							// Let player know what happened
							//System.out.print("Played tile ");
							this.hand.get(this.hand.size()-1).printTile();
							//System.out.print(" from boneyard to the mexican train\n");
							// Remove tile from hand
							this.hand.remove(this.hand.size()-1);
							// Set the played trains orphan to false
							mexOrphan = false;

							return;
						}
						else if (temp.equals("2"))
						{
							// Flip tile for this train
							flipTileForTrain(this.hand.get(this.hand.size()-1), this.train);
							// Play tile immediately to this train
							addTileTrain(this.hand.get(this.hand.size()-1), this.train);
							// Let player know what happened
							//System.out.print("Played tile ");
							this.hand.get(this.hand.size()-1).printTile();
							//System.out.print(" from boneyard to your train\n");
							// Remove tile from hand
							this.hand.remove(this.hand.size()-1);

							this.setOrphan(false);

							// When playing to personal train always set marker to false as playing to personal train removes markers
							this.setMarker(false);
							return;
						}
						else if (temp.equals("3"))
						{
							// Flip tile/ Check if need to flip
							flipTileForComputerTrain(this.hand.get(this.hand.size()-1), computer.getTrain());
							// Play tile immediately to computers train
							addTileComputerTrain(this.hand.get(this.hand.size()-1), computer);
							// Let player know what happened
							//System.out.print("Played tile ");
							this.hand.get(this.hand.size()-1).printTile();
							//System.out.print(" from boneyard to the computer's train\n");
							// Remove tile from hand
							this.hand.remove(this.hand.size()-1);
							// Change status of computers orphan double
							computer.setOrphan(false);

							return;
						}
			}
					else
					{
						//System.out.print("Could not play tile ");
						this.hand.get(this.hand.size()-1).printTile();
						//System.out.print(" from the boneyard. It is now in your hand.\n");
						setMarker(true);
					}
		}
	
	}// End of noPlayableTiles()
	
	/* *********************************************************************
	Function Name: pickTile
	Purpose: pick a tile from the member hand to play to the passed train
	Parameters:
				passed_train, train passed by value

	Return Value: a legal playable tile to be played onto the passed train
	Algorithm:
				1) prompt user for input on tile they want to play
				2) if tile is in hand and it is playable to the passed train
						use flipTileForTrain() and return tile
	Assistance Received: none
	********************************************************************* */
	public Tile pickTile(ArrayList<Tile> passed_train)
	{
		if (passed_train.size() > 0)
		{
			String temp = "";
			// Don't start loop with no values in the users hand, should not even get here in that case but
			// check just in case it somehow gets through with no hand values
			while (true && this.hand.size() != 0)
			{
				////System.out.print("Enter a tile from your hand you would like to play to the train: ");
				temp = new Scanner(System.in).nextLine();
				Tile tempTile = new Tile();
				// Don't attempt to access string out of range
				if (temp.length() >= 3)
				{
					int tempA = temp.charAt(0);
					int tempB = temp.charAt(2);
					tempTile.setTile(tempA - 48, tempB - 48);
					// !ALEC this probably works different in C++ check if it is an issue
					//tempTile.setTile(tempA, tempB);
				}
				// If tile is in hand and one of its ends matches the end of the train, we play the tile
				if (isTileInHand(tempTile) == true && (passed_train.get(passed_train.size() - 1).getSideB() == tempTile.getSideA() 
						|| passed_train.get(passed_train.size() - 1).getSideB() == tempTile.getSideB()))
				{
					flipTileForTrain(tempTile, passed_train);
					return tempTile;
				}
				else if (isTileInHand(tempTile) == false)
				{
					continue;
				}
				else
				{
					////System.out.print("Tile must be playable\n");
				}
			}
			////System.out.print("No values in hand?\n");
			return new Tile(0, 0);
		}
		else
		{
			////System.out.print("Error in Class Human pickTile\n");
			System.exit(1);
			return new Tile();
		}
	}// End of pickTile()
	
	
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
	Function Name: playTile
	Purpose: play a tile following the rules of the game logic
	Parameters:
				tile, tile object passed by value. Is the tile being played
				trainChoice, passed by value. String value of the train the user selected.
				mexican_train<>, passed by reference. Has the tiles of the mexican_train
				yard<>, passed by reference. Has the tiles of the boneyard
				copmuter, passed by reference. Is the other player of the game, of class type Computer()
				mexOrphan, passed by reference. Boolean value to indicate mexican train has an orphan double
				gameEnd, passed by reference. Boolean value to flag whether or not the game needs to be ended
	Return Value: void
	Algorithm:
				1) Check all possible ways the passed tile can be played to the train indicated
				2) Determine the outcome of the tile being played via the game logic
	Assistance Received: none
	********************************************************************* */
	public void playTile(Tile tile, String trainChoice, ArrayList<Tile> mex_train, ArrayList<Tile> yard, Computer computer, boolean mexOrphan, boolean gameEnd)
	{
		ArrayList<Tile> played_train = new ArrayList<Tile>();
		// boolean value to keep track of if we played a double already
		boolean playedDouble = false;

		// Set the temp train played_train equal to the train the user is playing to
		switch (Integer.parseInt(trainChoice))
		{
		case 1:
			played_train = mex_train;
			break;
		case 2:
			played_train = this.train;
			break;
		case 3:
			played_train = computer.getTrain();
			Collections.reverse(played_train);
			break;
		}

		// Play a non double tile at the end of the elligible train
		if (tile.isDouble() == false)
		{
			played_train.add(tile);
			removeTileHand(tile);

			////System.out.print("Played non double tile ");
			tile.printTile();
			////System.out.print("\n");

			// Assign the values that have been changed on our temp played_train back to the original train
			switch (Integer.parseInt(trainChoice))
			{
			case 1:
				mex_train = played_train;
				break;
			case 2:
				this.train = played_train;
				setMarker(false);
				break;
			case 3:
				computer.train = played_train;
				Collections.reverse(played_train);
				break;
			}
		}
		if (tile.isDouble() == true)
		{
			// Push the double tile onto the train and remove it from the hand
			played_train.add(tile);
			removeTileHand(tile);

			////System.out.print("Played double tile ");
			tile.printTile();
			////System.out.print("\n");

			playedDouble = true;

			// Assign the values that have been changed on our temp played_train back to the original train
			switch (Integer.parseInt(trainChoice))
			{
			case 1:
				mex_train = played_train;
				break;
			case 2:
				this.train = played_train;
				setMarker(false);
				break;
			case 3:
				computer.train = played_train;
				Collections.reverse(played_train);
				break;
			}
		}
		if (tile.isDouble() == true && this.hand.size() == 1 && twoPlayableDoubles(mex_train, computer.train) == true)
		{
			// Continues the output from if above for this case to print 2 tiles
			////System.out.print("Also played tile ");
			this.hand.get(0).printTile();
			////System.out.print(" as the last tile, Game Over\n");

			// Play the 2nd playable double and end the game
			if (isTilePlayable(this.hand.get(0), mex_train) == true)
			{
				addTileTrain(this.hand.get(0), mex_train);
				removeTileHand(this.hand.get(0));
			}
			else if (isTilePlayable(this.hand.get(0), this.train) == true)
			{
				addTileTrain(this.hand.get(0), this.train);
				removeTileHand(this.hand.get(0));
				// always set marker to false after playing to human train
				this.setMarker(false);
			}
			else if (isTilePlayableToComputer(this.hand.get(0), computer.train) == true)
			{
				addTileComputerTrain(this.hand.get(0), computer);
				removeTileHand(this.hand.get(0));
			}
			gameEnd = true;
		}else if (tile.isDouble() == true && this.hand.size() == 0)
		{
			// if played double and hand is empty end game
			gameEnd = true;
			tile.printTile();
			////System.out.print(" was played as last tile, Game Over\n");
		}
		else if (tile.isDouble() == true && ((isHandElligibleComputerTrain(computer.train) == true && computer.getMarker() == true)
			|| isHandElligibleTrain(mex_train) == true || isHandElligibleTrain(this.train) == true))
		{
			// If any a double is played and the hand is elligible to any train			

			////System.out.print("You have a non double follow up, select a train to play it to, if it's a different train to the double it will create an orphan double\n");
			String newSelection = selectTrain(mex_train, computer);
			if (newSelection.equals("1"))
			{
				// Selection 1 is for mexican train
				Tile tempSwitchTile = pickTile(mex_train);
				addTileTrain(tempSwitchTile, mex_train);
				removeTileHand(tempSwitchTile);
				////System.out.print("Played tile ");
				tempSwitchTile.printTile();
				////System.out.print(" to mexican train\n");
			}
			else if (newSelection.equals("2"))
			{
				// Selection 2 is for human train
				Tile tempSwitchTile = pickTile(this.train);
				addTileTrain(tempSwitchTile, this.train);
				removeTileHand(tempSwitchTile);
				this.setMarker(false);
				////System.out.print("Played tile ");
				tempSwitchTile.printTile();
				////System.out.print(" to your train\n");
			}
			else if (newSelection.equals("3"))
			{
				// Selection 3 for computer train
				Tile tempSwitchTile = pickTileComputerTrain(computer.train);
				addTileComputerTrain(tempSwitchTile, computer);
				removeTileHand(tempSwitchTile);
				////System.out.print("Played tile ");
				tempSwitchTile.printTile();
				////System.out.print( " to computer's train\n");
			}

			if ((!newSelection.equals(trainChoice))&& trainChoice.equals("1"))
			{
				// If it's not the same train set the Orphan value to true
				mexOrphan = true;
			}
			else if ((!newSelection.equals(trainChoice)) && trainChoice.equals("2"))
			{
				this.setOrphan(true);
			}
			else if ((!newSelection.equals(trainChoice)) && trainChoice.equals("3"))
			{
				computer.setOrphan(true);
			}

		}
		else if (playedDouble == false && tile.isDouble() == true && (isHandElligibleComputerTrain(computer.train) == false 
				&& isHandElligibleTrain(mex_train) == false && isHandElligibleTrain(this.train) == false))
		{
			// Use playedDouble to avoid drawing after a case where the double tile is played and reevaluates the hands after being
			// removed causing to draw when user is not supposed to since they did sucessfully play a tile
			// Follow procedure for no tiles
			noPlayableTiles(mex_train, yard, computer, mexOrphan);
		}
	}// End of playTile()
	
	/* *********************************************************************
	Function Name: twoPlayableDoubles
	Purpose: determine if a two playable doubles case is possible
	Parameters:
				mex_train, mexican train ArrayList passed by value
				computer_train, computer train ArrayList passed by value
				
	Return Value: boolean value for whether or not a two playable doubles case is possible
	Algorithm:
				1) determine if the hand.at(0) is a tile playable to the any of the passed
					trains or the local train
							if so return true
							if not return false
	Assistance Received: none
	********************************************************************* */
	// Checks if the last value of hand is playable double
	public boolean twoPlayableDoubles(ArrayList<Tile> mex_train, ArrayList<Tile> computer_train)
	{
		if (this.hand.get(0).isDouble() == true)
		{
			// if Checks that there are two double tiles playable to 2 elligible trains
			if ((isTilePlayable(this.hand.get(0), mex_train) == true 
				|| isTilePlayableToComputer(this.hand.get(0), computer_train) == true 
				|| isTilePlayable(this.hand.get(0), this.train)))
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
		return false;
	}// End of twoPlayableDoubles()
	
	/* *********************************************************************
	Function Name: selectTrain
	Purpose: determines which train the user can play to and asks their selection
	Parameters:
				mex_train, mexican train ArrayList passed by value
				computer, computer class object passed by value

	Return Value: a string of "1" mexican train "2" personal train or "3" computer train
	Algorithm:
				1) check if hands are elligible to the trains and set booleans for if playable to true or false
				2) prompt user for input based on the value of the playable booleans
				3) validate inputs at every cin and return the string
	Assistance Received: none
	********************************************************************* */
	// Has user select the train to play to
	public String selectTrain(ArrayList<Tile> mex_train, Computer computer)
	{
		boolean mexPlayable = false;
		boolean humanPlayable = false;
		boolean computerPlayable = false;
    
		////System.out.print("Select a train to play on");
		// If a train is elligible ask the user the card they would like to play and onto which train
		if (isHandElligibleTrain(mex_train) == true)
		{
			////System.out.print(" (1) Mexican Train");
			mexPlayable = true;
		}
		if (isHandElligibleTrain(this.train) == true)
		{
			////System.out.print(" (2) Your Train");
			humanPlayable = true;
		}
		if ((isHandElligibleComputerTrain(computer.getTrain()) == true) && computer.getMarker() == true)
		{
			////System.out.print(" (3) Computers Train");
			computerPlayable = true;
		}
		////System.out.print(": ");
    
		String temp = "";
		if (mexPlayable == true && humanPlayable == true && computerPlayable == true)
		{
			temp = new Scanner(System.in).nextLine();
			while (!(temp.equals("1") || temp.equals("2") || temp.equals("3")))
			{
				////System.out.print("Please enter a one of the stated values: ");
				temp = new Scanner(System.in).nextLine();
			}
		}
		else if (mexPlayable == true && humanPlayable == true)
		{
			temp = new Scanner(System.in).nextLine();
			while (!(temp.equals("1") || temp.equals("2")))
			{
				////System.out.print("Please enter a one of the stated values: ");
				temp = new Scanner(System.in).nextLine();
			}
		}
		else if (mexPlayable == true && computerPlayable == true)
		{
			temp = new Scanner(System.in).nextLine();
			while (!(temp.equals("1") || temp.equals("3")))
			{
				////System.out.print("Please enter a one of the stated values: ");
				temp = new Scanner(System.in).nextLine();
			}
		}
		else if (humanPlayable == true && computerPlayable == true)
		{
			temp = new Scanner(System.in).nextLine();
			while (!(temp.equals("2") || temp.equals("3")))
			{
				////System.out.print("Please enter a one of the stated values: ");
				temp = new Scanner(System.in).nextLine();
			}
		}
		else if (mexPlayable == true)
		{
			temp = new Scanner(System.in).nextLine();
			while (!(temp.equals("1")))
			{
				//System.out.print("Please enter a one of the stated values: ");
				temp = new Scanner(System.in).nextLine();
			}
		}
		else if (humanPlayable == true)
		{
			temp = new Scanner(System.in).nextLine();
			while (!(temp.equals("2")))
			{
				//System.out.print("Please enter a one of the stated values: ");
				temp = new Scanner(System.in).nextLine();
			}
		}
		else if (computerPlayable == true)
		{
			temp = new Scanner(System.in).nextLine();
			while (!(temp.equals("3")))
			{
				//System.out.print("Please enter a one of the stated values: ");
				temp = new Scanner(System.in).nextLine();
			}
		}
    
		return temp;
	} // End of selectTrain()
	
	/* *********************************************************************
	Function Name: pickTileComputerTrain
	Purpose: pick a tile from the member hand to play to the computer train
	Parameters:
				computer_train, computer train passed by value

	Return Value: a legal playable tile to be played onto the computers train
	Algorithm:
				1) prompt user for input on tile they want to play
				2) if tile is in hand and it is playable to the computer train
						use flipTileForComputerTrain() and return tile
	Assistance Received: none
	********************************************************************* */
	public Tile pickTileComputerTrain(ArrayList<Tile> computer_train)
	{
		if (computer_train.size() == 0)
		{
			//System.out.print("Error at pickTileComputerTrain\n");
			System.exit(1);
		}

		String temp = "";
		// Don't start loop with no values in the users hand, should not even get here in that case but
		// check just in case it somehow gets through with no hand values
		while (true && this.hand.size() != 0)
		{
			//System.out.print("Enter a tile from your hand you would like to play to the train: ");
			temp = new Scanner(System.in).nextLine();
			Tile tempTile = new Tile();
			// Don't attempt to access string out of range
			if (temp.length() >= 3)
			{
				int tempA = temp.charAt(0);
				int tempB = temp.charAt(2);
				tempTile.setTile(tempA - 48, tempB - 48);
			}
			// If tile is in hand and one of its ends matches the end of the train, we play the tile
			if (isTileInHand(tempTile) == true && (computer_train.get(0).getSideA() == tempTile.getSideA() 
					|| computer_train.get(0).getSideA() == tempTile.getSideB()))
			{
				flipTileForComputerTrain(tempTile, computer_train);
				return tempTile;
			}
			else if (isTileInHand(tempTile) == false)
			{
				continue;
			}
			else
			{
				//System.out.print("Tile must be playable\n");
			}
		}
		//System.out.print("No values in hand?\n");
		return new Tile();

	}// End of pickTileComputerTrain()
	
	/* *********************************************************************
	Function Name: help
	Purpose: give the user their playable tiles to help them determine a move
	Parameters:
				mex_train, mexican train ArrayList, passed by value
				computer, computer train ArrayList, passed by value
				mexOrphan, mexican orphan double state boolean, passed by value

	Return Value: boolean value for whether or not a two playable doubles case is possible
	Algorithm:
				1) Get a ArrayList of playable tiles to use by calling playableTiles()
				2) if orphan double is true delete all unplayable values from our ArrayList
				3) output the amount of remaining tiles in playable
	Assistance Received: none
	********************************************************************* */
	public String help(ArrayList<Tile> mex_train, Computer computer, boolean mexOrphan)
	{
		ArrayList<Tile> playable = playableTiles(mex_train, computer);

		String helpString = "Playable tiles are:";

		if (mexOrphan == true)
		{
			for (int i = 0; i < playable.size(); i++)
			{
				if (isTilePlayable(playable.get(i), mex_train) == false)
				{
					// erase elements that are not playable to the only playable train
					playable.remove(i);
				}
			}
		}
		else if (computer.getOrphan() == true)
		{
			for (int i = 0; i < playable.size(); i++)
			{
				if (isTilePlayableToComputer(playable.get(i), computer.train) == false)
				{
					// erase elements that are not playable to the only playable train
					playable.remove(i);
				}
			}
		}
		else if (this.getOrphan() == true)
		{
			for (int i = 0; i < playable.size(); i++)
			{
				if (isTilePlayable(playable.get(i), this.train) == false)
				{
					// erase elements that are not playable to the only playable train
					playable.remove(i);
				}
			}
		}

		if (playable.size() != 0)
		{
			//System.out.print("Your playable tiles are: ");
			for (int i = 0; i < playable.size(); i++)
			{
				helpString = helpString + " " + playable.get(i).stringTile();
			}
		}
		else
		{
			helpString = "You have no playable tiles\n";
			//System.out.print("You have no playable tiles");
		}
		return helpString;
	}// End of help()

}
