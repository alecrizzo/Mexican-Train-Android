/************************************************************
 * Name:  Alec Rizzo                                        *
 * Project: Project 4 - Mexican Train Java Android          *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date: 12/8/2021                                          *
 ************************************************************/
package com.example.mexicantrainandroid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Computer extends Player implements Serializable {

	
	/* *********************************************************************
	Function Name: printM
	Purpose: print if marked for computer players train
	Parameters:
	Return Value: void
	Algorithm:
				1) print "M " if member variable marker is true
	Assistance Received: none
	********************************************************************* */
	public void printM()
	{
		if (this.marker == true)
		{
			//System.out.print("M ");
		}
	}
	
	/* *********************************************************************
	Function Name: playTurn
	Purpose: play through a computers turn following the logic of the game
	Parameters:
				mexican_train<>, passed by reference. Has the tiles of the mexican_train
				yard<>, passed by reference. Has the tiles of the boneyard
				human, passed by reference. Other player of the game, of class type Human()
				mexOrphan, passed by reference. Boolean value to indicate mexican train has an orphan double
				gameEnd, passed by reference. Boolean value to flag whether or not the game needs to be ended
	Return Value: void
	Algorithm:
				1) Check playable tiles using isHandElligibleTrain(ArrayList<Tiles> train)
					if none playable call function noPlayableTiles()
					if hand is playable to any elligible trains, check for orphan doubles 
						if orphan double is true and hand is not elligible to the train call noPlayableTiles()
						if orphan double is true and hand is elligible call playTile()
				2) Else call selectTrain() to have the user select an elligible train they can play on and they will 
				   then call playTile()

				3) Computer will attempt to play tiles to trains in this heirarchical order
							onto its own train if marked (to defend)
							onto human train if marked	(to attack)
							onto mexican train if the others weren't availible (to increase the amount of options)
							onto its own train without marker (no more options)

	Assistance Received: none
	********************************************************************* */
	public void playTurn(ArrayList<Tile> mex_train, ArrayList<Tile> yard, Human human,
						 boolean mexOrphan, boolean gameEnd, String computerLogic)
	{
		// No playable tiles: If the computer does not have a tile that can be played at the end of any eligible train
		if (isHandElligibleTrain(mex_train) == false && (isHandElligibleTrain(human.train) == false || human.getMarker() == false)
			&& isHandElligibleComputerTrain(this.train) == false)
		{
			// If the boneyard is empty, the player passes their turn and puts a marker at the end of their personal train
			noPlayableTiles(mex_train, yard, human, mexOrphan);
		}
		else if (isHandElligibleTrain(mex_train) == true || (isHandElligibleTrain(human.train) == true && human.getMarker() == true)
			|| isHandElligibleComputerTrain(this.train) == true)
		{

			// Check orphan double case first as orphan double must be played upon rather than allowing selection
			if (this.getOrphan() == true)
			{
				////System.out.print("There is an Orphan Double on computer train, computer can only play to that train \n");
				computerLogic = computerLogic + "There is an Orphan Double on computer train, computer can only play to that train \n";
				if (isHandElligibleComputerTrain(this.train) == false)
				{
					////System.out.print("No playable tiles to the Orphan Double\n");
					computerLogic = computerLogic + "No playable tiles to the Orphan Double\n";
					noPlayableTiles(mex_train, yard, human, mexOrphan);
				}
				else
				{
					// Can play a value to the orphan double
					// determine playable computer tiles then play tile
					String trainChoice = "3";

					ArrayList<Tile> playable_comp_tiles = new ArrayList<Tile>();
					playable_comp_tiles = playableTilesToComputerTrain(this.train);
					Tile compChoice = getComputerChoice(playable_comp_tiles);
					flipTileForComputerTrain(compChoice, this.train);
					playTile(compChoice, trainChoice, mex_train, yard, human, mexOrphan, gameEnd, computerLogic);
					this.setMarker(false);
					this.setOrphan(false);
				}
			}
			else if (human.getOrphan() == true)
			{
				////System.out.print("There is an Orphan Double on the humans train, computer can only play to that train \n");
				computerLogic = computerLogic + "There is an Orphan Double on the humans train, computer can only play to that train \n";
				if (isHandElligibleTrain(human.getTrain()) == false)
				{
					////System.out.print("No playable tiles to the Orphan Double\n");
					computerLogic = computerLogic + "No playable tiles to the Orphan Double\n";
					noPlayableTiles(mex_train, yard, human, mexOrphan);
				}
				else
				{
					String trainChoice = "2";
					ArrayList<Tile> playable_comp_tiles = new ArrayList<Tile>();
					playable_comp_tiles = playableTilesToTrain(human.train);
					Tile compChoice = getComputerChoice(playable_comp_tiles);
					flipTileForTrain(compChoice, human.train);
					playTile(compChoice, trainChoice, mex_train, yard, human, mexOrphan, gameEnd, computerLogic);

					human.setOrphan(false);
				}
			}
			else if (mexOrphan == true)
			{
				////System.out.print("There is an Orphan Double on the Mexican train, computer can only play to that train \n");
				computerLogic = computerLogic + "There is an Orphan Double on the Mexican train, computer can only play to that train \n";
				if (isHandElligibleTrain(mex_train) == false)
				{
					////System.out.print("No playable tiles to the Orphan Double\n");
					computerLogic = computerLogic + "No playable tiles to the Orphan Double\n";
					noPlayableTiles(mex_train, yard, human, mexOrphan);
				}
				else
				{
					String trainChoice = "1";
					ArrayList<Tile> playable_comp_tiles = new ArrayList<Tile>();
					playable_comp_tiles = playableTilesToTrain(mex_train);
					Tile compChoice = getComputerChoice(playable_comp_tiles);
					flipTileForTrain(compChoice, mex_train);
					playTile(compChoice, trainChoice, mex_train, yard, human, mexOrphan, gameEnd, computerLogic);
					mexOrphan = false;
				}
			}
			else
			{

				// COMPUTERS LOGIC
				if (this.getMarker() == true && isHandElligibleComputerTrain(this.train) == true)
				{
					// If the computer is marked try to play that first to unmark (Defense)
					ArrayList<Tile> playable_comp_tiles = new ArrayList<Tile>();
					playable_comp_tiles = playableTilesToComputerTrain(this.train);
					Tile compChoice = getComputerChoice(playable_comp_tiles);
					String trainChoice = getTrainChoice(compChoice, human, mex_train);
					removeTileHand(compChoice);
					// This line that broke case 1 throwing error WHAT?! Error using flipTileForTrain with 6-6 to 9-9
					// This should have been flip tile for computer train
					// Also should just make the computer train play exactly like the human train, but it just serializes and 
					// displays differently, that would have avoided the issue entirely
					flipTileForComputerTrain(compChoice, this.train);
					playTile(compChoice, trainChoice, mex_train, yard, human, mexOrphan, gameEnd, computerLogic);
					this.setMarker(false);

					////System.out.print("Computer played to its own train to unmark the train\n");
					computerLogic = computerLogic + "Computer played to its own train to unmark the train\n";

				}
				else if (isHandElligibleTrain(human.train) == true && human.getMarker() == true)
				{
					// If the humans train is elligible, play to it to get the most out of the marker while it's there
					ArrayList<Tile> playable_comp_tiles = new ArrayList<Tile>();
					playable_comp_tiles = playableTilesToTrain(human.train);
					Tile compChoice = getComputerChoice(playable_comp_tiles);
					String trainChoice = getTrainChoice(compChoice, human, mex_train);
					removeTileHand(compChoice);
					flipTileForTrain(compChoice, human.train);
					playTile(compChoice, trainChoice, mex_train, yard, human, mexOrphan, gameEnd, computerLogic);

					////System.out.print("Computer played to your train to take advantage of your marker\n");
					computerLogic = computerLogic + "Computer played to your train to take advantage of your marker\n";
				}
				else if (isHandElligibleTrain(mex_train) == true)
				{
					// We can put a double on the mexican train, potentially making an orphan double or losing us more tiles
					// If the humans train is elligible, play to it to get the most out of the marker while it's there
					ArrayList<Tile> playable_comp_tiles = new ArrayList<Tile>();
					playable_comp_tiles = playableTilesToTrain(mex_train);
					Tile compChoice = getComputerChoice(playable_comp_tiles);
					String trainChoice = getTrainChoice(compChoice, human, mex_train);
					removeTileHand(compChoice);
					flipTileForTrain(compChoice, mex_train);
					playTile(compChoice, trainChoice, mex_train, yard, human, mexOrphan, gameEnd, computerLogic);

					////System.out.print("Computer played to Mexican train in the hopes of creating more options\n");
					computerLogic = computerLogic + "Computer played to Mexican train in the hopes of creating more options\n";
				}
				else if (isHandElligibleComputerTrain(this.train) == true)
				{
					ArrayList<Tile> playable_comp_tiles = new ArrayList<Tile>();
					playable_comp_tiles = playableTilesToComputerTrain(this.train);
					Tile compChoice = getComputerChoice(playable_comp_tiles);
					String trainChoice = getTrainChoice(compChoice, human, mex_train);
					removeTileHand(compChoice);
					flipTileForComputerTrain(compChoice, this.train);
					playTile(compChoice, trainChoice, mex_train, yard, human, mexOrphan, gameEnd, computerLogic);
					this.setMarker(false);

					computerLogic = computerLogic + "Computer played to its own train because it's out of options\n";

				}
			}
		}
		else if ((human.getOrphan() == true && isHandElligibleTrain(human.train) == false)
			|| (isHandElligibleComputerTrain(this.train) == false && this.getOrphan() == true)
			|| (mexOrphan == true && isHandElligibleTrain(mex_train) == false))
		{
			// No playable tiles while the opponents train is availible, but the opponents train is not marked
			// and there is an orphan double somewhere
			noPlayableTiles(mex_train, yard, human, mexOrphan);
		}
	}// End of playTurn()
	
	/* *********************************************************************
	Function Name: playTile
	Purpose: play a tile following the rules of the game logic
	Parameters:
				tile, tile object passed by value. Is the tile being played
				trainChoice, passed by value. String value of the train the user selected.
				mexican_train<>, passed by reference. Has the tiles of the mexican_train
				yard<>, passed by reference. Has the tiles of the boneyard
				human, passed by reference. Is the other player of the game, of class type Human()
				mexOrphan, passed by reference. Boolean value to indicate mexican train has an orphan double
				gameEnd, passed by reference. Boolean value to flag whether or not the game needs to be ended
	Return Value: void
	Algorithm:
				1) Check all possible ways the passed tile can be played to the train indicated
				2) Determine the outcome of the tile being played via the game logic
	Assistance Received: none
	********************************************************************* */
	public void playTile(Tile tile, String trainChoice, ArrayList<Tile> mex_train,
						 ArrayList<Tile> yard, Human human, boolean mexOrphan, boolean gameEnd,
						 String computerLogic)
	{
		ArrayList<Tile> played_train = new ArrayList<Tile>();
		boolean playedDouble = false;

		// Set the temp train played_train equal to the train the user is playing to
		switch (Integer.parseInt(trainChoice))
		{
		case 1:
			played_train = mex_train;
			break;
		case 2:
			played_train = human.train;
			break;
		case 3:
			played_train = this.train;
			Collections.reverse(played_train);
			break;
		}

		// Play a non double tile at the end of the elligible train
		if (tile.isDouble() == false)
		{
			played_train.add(tile);
			removeTileHand(tile);

			computerLogic = computerLogic + " Computer played non double tile "  + tile.stringTile() + "\n";

			// Assign the values that have been changed on our temp played_train back to the original train
			switch (Integer.parseInt(trainChoice))
			{
			case 1:
				mex_train = played_train;
				break;
			case 2:
				human.train = played_train;
				break;
			case 3:
				this.train = played_train;
				Collections.reverse(this.train);
				setMarker(false);
				break;
			}
		}
		if (tile.isDouble() == true)
		{
			// Push the double tile onto the train and remove it from the hand
			played_train.add(tile);
			removeTileHand(tile);

			computerLogic = computerLogic + " Computer played double tile " + tile.stringTile() + "\n";

			playedDouble = true;

			// Assign the values that have been changed on our temp played_train back to the original train
			switch (Integer.parseInt(trainChoice))
			{
			case 1:
				mex_train = played_train;
				break;
			case 2:
				human.train = played_train;
				break;
			case 3:
				this.train = played_train;
				Collections.reverse(this.train);
				setMarker(false);
				break;
			}
		}
		if (tile.isDouble() == true && this.hand.size() == 1 && twoPlayableDoubles(mex_train, human.train) == true)
		{
			////System.out.print("Computer also played tile ");
			//this.hand.get(0).printTile();
			////System.out.print(" as the last tile, Game Over\n");

			computerLogic = computerLogic + "Computer also played tile " +
					this.hand.get(0).stringTile() + " as last tile, Game Over\n";

			// Play the 2nd playable double and end the game
			if (isTilePlayable(this.hand.get(0), mex_train) == true)
			{
				addTileTrain(this.hand.get(0), mex_train);
				removeTileHand(this.hand.get(0));
			}
			else if (isTilePlayable(this.hand.get(0), human.train) == true)
			{
				addTileTrain(this.hand.get(0), human.train);
				removeTileHand(this.hand.get(0));
				// always set marker to false after playing to human train
				this.setMarker(false);
			}
			else if (isTilePlayableToComputer(this.hand.get(0), this.train) == true)
			{
				addTileComputerTrain(this.hand.get(0));
				removeTileHand(this.hand.get(0));
			}
			gameEnd = true;
		}
		else if (tile.isDouble() == true && this.hand.size() == 0)
		{
			gameEnd = true;
			computerLogic = computerLogic + " Computer played double " +
					tile.stringTile() + " as last tile, Game Over\n";
		}
		else if (tile.isDouble() == true && (isHandElligibleComputerTrain(this.train) == true || 
			isHandElligibleTrain(mex_train) == true || ((isHandElligibleTrain(human.train) == true) 
			&& human.getMarker() == true )))
		{
			// If any a double is played and the hand is elligible to any train			
			String newSelection = "";
			ArrayList<Tile> playable_comp_tiles = new ArrayList<Tile>();
			Tile compChoice = new Tile();

			if (isHandElligibleTrain(mex_train) == true)
			{
				playable_comp_tiles = playableTilesToTrain(mex_train);
				compChoice = getComputerChoice(playable_comp_tiles);
				newSelection = "1";
			}
			else if (isHandElligibleTrain(human.train) == true && human.getMarker() == true)
			{
				playable_comp_tiles = playableTilesToTrain(human.train);
				compChoice = getComputerChoice(playable_comp_tiles);
				newSelection = "2";
			}
			else if (isHandElligibleComputerTrain(this.train) == true)
			{
				playable_comp_tiles = playableTilesToComputerTrain(this.train);
				compChoice = getComputerChoice(playable_comp_tiles);
				newSelection = "3";
			}

			computerLogic = computerLogic + " Computer played follow up tile "
					+ compChoice.stringTile() + " ";

			if (newSelection.equals("1"))
			{
				// Selection 1 is for mexican train
				Tile tempSwitchTile = compChoice;
				removeTileHand(tempSwitchTile);
				flipTileForTrain(tempSwitchTile, mex_train);
				addTileTrain(tempSwitchTile, mex_train);
				computerLogic = computerLogic + " to Mexican Train\n";
				////System.out.print(" to Mexican Train\n");
			}
			else if (newSelection.equals("2"))
			{
				// Selection 2 is for human train
				Tile tempSwitchTile = compChoice;
				removeTileHand(tempSwitchTile);
				flipTileForTrain(tempSwitchTile, human.train);
				addTileTrain(tempSwitchTile, human.train);
				computerLogic = computerLogic + " to Your Train\n";
				////System.out.print(" to Your Train\n");
			}
			else if (newSelection.equals("3"))
			{
				// Selection 3 for computer train
				Tile tempSwitchTile = compChoice;
				removeTileHand(tempSwitchTile);
				flipTileForComputerTrain(tempSwitchTile, this.train);
				addTileComputerTrain(tempSwitchTile);
				this.setMarker(false);
				computerLogic = computerLogic + " to its Train\n";
				////System.out.print(" to its Train\n");
			}

			if ((!newSelection.equals(trainChoice)) && trainChoice.equals("1"))
			{
				// If it's not the same train set the Orphan value to true
				mexOrphan = true;
			}
			else if ((!newSelection.equals(trainChoice)) &&  trainChoice.equals("2"))
			{
				human.setOrphan(true);
			}
			else if ((!newSelection.equals(trainChoice)) && trainChoice.equals("3"))
			{
				this.setOrphan(true);
			}

		}
		else if (playedDouble == false && tile.isDouble() == true && (isHandElligibleComputerTrain(this.train) == false 
				&& isHandElligibleTrain(mex_train) == false && isHandElligibleTrain(human.train) == false))
		{
			// Follow procedure for no tiles
			noPlayableTiles(mex_train, yard, human, mexOrphan);
		}
	}// End of playTile()
	
	/* *********************************************************************
	Function Name: getTrainChoice
	Purpose: get computers selection of train to play the passed tile
	Parameters:
				tile, tile object passed by value. Is the tile being played
				human, human object passed by value. The opponent to computer
				mexican_train<>, passed by value. Has the tiles of the mexican_train

	Return Value: string of "1" "2" or "3" for mexican train, human train, computer train
	Algorithm:
				1) if isTilePlayable to the trains pick in the order of
						human train
						mexican train
						computer train
	Assistance Received: none
	********************************************************************* */
	public String getTrainChoice(Tile tile, Human human, ArrayList<Tile> mex_train)
	{
		String trainChoice = "";
		
		if (isTilePlayable(tile, human.train) == true && (human.getMarker() == true || human.getOrphan() == true))
		{
			// Play tile to the human
			trainChoice = "2";
		}
		else if (isTilePlayable(tile, mex_train) == true)
		{
			// Play tile to mexican train
			trainChoice = "1";
		}
		else 
		{
			// Play tile to computer
			trainChoice = "3";
		}

		return trainChoice;
	}// End of getTrainChoice()
	

	/* *********************************************************************
	Function Name: addTileComputerTrain(Tile, t)
	Purpose: add a tile to the computers train
	Parameters:
				tile, tile object passed by value
	Return Value: void
	Algorithm:
				1) insert tile t at the front of the train ArrayList
	Assistance Received: none
	********************************************************************* */
	public void addTileComputerTrain(Tile t)
	{
		this.train.add(0, t);
	}// End of addTileComputerTrain()
	
	/* *********************************************************************
	Function Name: checkPlayableDoubles
	Purpose:	determine if playable double case is possible
	Parameters:
				playable_tiles, ArrayList of tiles that are playable, passed by value
	Return Value: boolean value for whether playable double case is possible
	Algorithm:
				1) recieve the playable tiles
				2) determine if there is a double amongst the playable tiles
				3) if so return true, we should play it
	Assistance Received: none
	********************************************************************* */
	public boolean checkPlayableDoubles(ArrayList<Tile> playable_tiles)
	{
		for (int i = 0; i < playable_tiles.size(); i++)
		{
			if (playable_tiles.get(i).isDouble() == true)
			{
				return true;
			}
		}
		return false;
	}// End of checkPlayableDoubles()

	/* *********************************************************************
	Function Name: twoPlayableDoubles
	Purpose: determine if a two playable doubles case is possible
	Parameters:
				mex_train, mexican train ArrayList passed by value
				human_train, human train ArrayList passed by value

	Return Value: boolean value for whether or not a two playable doubles case is possible
	Algorithm:
				1) determine if the hand.at(0) is a tile playable to the any of the passed
					trains or the local train
							if so return true
							if not return false
	Assistance Received: none
	********************************************************************* */
	public boolean twoPlayableDoubles(ArrayList<Tile> mex_train, ArrayList<Tile> human_train)
	{
		if (this.hand.get(0).isDouble() == true)
		{
			// if Checks that there are two double tiles playable to 2 elligible trains
			if ((isTilePlayable(this.hand.get(0), mex_train) == true
				|| isTilePlayableToComputer(this.hand.get(0), this.train) == true
				|| isTilePlayable(this.hand.get(0), human_train)))
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
	Function Name: getPlayableDouble
	Purpose: gets the playable double from the playable tiles
	Parameters:
				playable_tiles, playable tiles ArrayList passed by value

	Return Value: Tile value if theres a playable double
	Algorithm:
				1) takes a double from playable tiles
				2) should always be used with checkPlayableDoubles
	Assistance Received: none
	********************************************************************* */
	public Tile getPlayableDouble(ArrayList<Tile> playable_tiles)
	{
		Tile temp = new Tile(); 
		for (int i = 0; i < playable_tiles.size(); i++)
		{
			if (playable_tiles.get(i).isDouble() == true)
			{
				temp = playable_tiles.get(i);
			}
		}
		return temp;
	}// End of getPlayableDouble()
	
	/* *********************************************************************
	Function Name: getComputerChoice
	Purpose: gets the computers selection of tile
	Parameters:
				playable_tiles, playable tiles ArrayList passed by value

	Return Value: Tile value of the playable tile the computer prefers
	Algorithm:
				1) determine if there is a playable double
					if so play that
					if not play first playable tile
	Assistance Received: none
	********************************************************************* */
	public Tile getComputerChoice(ArrayList<Tile> playable_tiles)
	{
		if(checkPlayableDoubles(playable_tiles) == false)
		{
			return playable_tiles.get(0);
		}
		else
		{
			return getPlayableDouble(playable_tiles);
		}
	}// End of getComputerChoice()

	/* *********************************************************************
	Function Name: noPlayableTiles
	Purpose: determine the outcome of computers turn when no playable tiles available
	Parameters:
				mexican_train, mexican train ArrayList passed by reference
				boneyard, boneyard ArrayList passed by reference
				human, Human player object passed by reference
				mexOrphan, mexican train orphan value passed by reference
	Return Value: void
	Algorithm:
				1) draw a tile depending on the size of the boneyard
				2) attempt to play to orphan double if it exists
					if it doesn't exist check playable trains for the tile drawn and select train
				3) if tile is unplayable mark train keep tile in hand
	Assistance Received: none
	********************************************************************* */
	public void noPlayableTiles(ArrayList<Tile> mex_train, ArrayList<Tile> yard, Human human, boolean mexOrphan)
	{
		// If the boneyard is empty, the player passes their turn and puts a marker at the end of their personal train
		if (yard.size() == 0)
		{
			setMarker(true);
			return;
		}
		else
		{
			// If the boneyard is not empty, the player draws a tile from the boneyard and plays it immediately.
			this.hand.add(yard.get(0));
			yard.remove(0);

			//System.out.print("Computer drew tile ");
			this.hand.get(this.hand.size()-1).printTile();
			//System.out.print(" from the boneyard\n");

			// If orphan double for human, check only that condition as it takes full priority to the computers available actions
			if (human.getOrphan() == true)
			{
				if (isTilePlayable(this.hand.get(this.hand.size()-1), human.getTrain()) == true)
				{
					// Flip tile/ Check if need to flip
					flipTileForTrain(this.hand.get(this.hand.size()-1), human.getTrain());
					// Play tile immediately to humans train
					addTileTrain(this.hand.get(this.hand.size()-1), human.train);
					// Let player know what happened
					//System.out.print("Computer played tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from boneyard to the orphan double\n");
					// Remove tile from hand
					this.hand.remove(this.hand.size()-1);
					// Change status of humans orphan double
					human.setOrphan(false);

					return;
				}
				else
				{
					// Player must add card to their hand pass turn and mark their train
					// note: card already added to hand above
					setMarker(true);
					//System.out.print("Computer could not play tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from the boneyard. It is now in computers hand.\n");
					return;
				}
			}
			// Check this computer trains orphan double case
			if (this.getOrphan() == true)
			{
				if (isTilePlayableToComputer(this.hand.get(this.hand.size()-1), this.train))
				{
					// Flip tile for this train
					flipTileForComputerTrain(this.hand.get(this.hand.size()-1), this.train);
					// Play tile immediately to this train
					addTileComputerTrain(this.hand.get(this.hand.size()-1));
					// Let player know what happened
					//System.out.print("Computer played tile ");
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
					//System.out.print("Computer could not play tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from the boneyard. It is now in computers hand.\n");
					return;
				}
			}
			if (mexOrphan == true)
			{
				if (isTilePlayable(this.hand.get(this.hand.size()-1), mex_train))
				{
					// Flip tile for mexican train
					flipTileForTrain(this.hand.get(this.hand.size()-1), mex_train);
					// Play tile immediately to this train
					addTileTrain(this.hand.get(this.hand.size()-1), mex_train);
					// Let player know what happened
					//System.out.print("Computer played tile ");
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
					//System.out.print("Computer could not play tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from the boneyard. It is now in computers hand.\n");
					return;
				}
			}
			else if (human.getMarker() == true && isTilePlayable(this.hand.get(this.hand.size()-1), human.getTrain()) == true && human.getOrphan() == false)
			{
				// Try to play to human first if possible
				
					// Flip tile/ Check if need to flip
					flipTileForTrain(this.hand.get(this.hand.size()-1), human.getTrain());
					// Play tile immediately to humans train
					addTileTrain(this.hand.get(this.hand.size()-1), human.train);
					// Let player know what happened
					//System.out.print("Computer played tile ");
					this.hand.get(this.hand.size()-1).printTile();
					//System.out.print(" from boneyard to your train\n");
					// Remove tile from hand
					this.hand.remove(this.hand.size()-1);

					return;
			}
			else if (isTilePlayable(this.hand.get(this.hand.size()-1), mex_train) == true && mexOrphan == false)
			{
				// Try to play to mexican train second, if possible

				// Flip tile for mexican train
				flipTileForTrain(this.hand.get(this.hand.size()-1), mex_train);
				// Play tile immediately to this train
				addTileTrain(this.hand.get(this.hand.size()-1), mex_train);
				// Let player know what happened
				//System.out.print("Computer played tile ");
				this.hand.get(this.hand.size()-1).printTile();
				//System.out.print(" from boneyard to the mexican train\n");
				// Remove tile from hand
				this.hand.remove(this.hand.size()-1);
				// Set the played trains orphan to false
				mexOrphan = false;

				return;
			}
			else if (isTilePlayableToComputer(this.hand.get(this.hand.size()-1), this.train) == true && this.getOrphan() == false)
			{
				// Play to computer as a last resort

				// Flip tile for this train
				flipTileForComputerTrain(this.hand.get(this.hand.size()-1), this.train);
				// Play tile immediately to this train
				addTileComputerTrain(this.hand.get(this.hand.size()-1));
				// Let player know what happened
				//System.out.print("Computer played tile ");
				this.hand.get(this.hand.size()-1).printTile();
				//System.out.print(" from boneyard to it's train\n");
				// Remove tile from hand
				this.hand.remove(this.hand.size()-1);

				this.setOrphan(false);

				// When playing to personal train always set marker to false as playing to personal train removes markers
				this.setMarker(false);
				return;
			}
			else
			{
				//System.out.print("Computer could not play tile ");
				this.hand.get(this.hand.size()-1).printTile();
				//System.out.print(" from the boneyard. It is now in its hand.\n");
				setMarker(true);
			}
		}
	}// End of noPlayableTiles()
	
}
