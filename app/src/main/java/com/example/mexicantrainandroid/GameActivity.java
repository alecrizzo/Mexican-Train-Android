/************************************************************
 * Name:  Alec Rizzo                                        *
 * Project: Project 4 - Mexican Train Java Android          *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date: 12/8/2021                                          *
 ************************************************************/
package com.example.mexicantrainandroid;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.internal.ParcelableSparseArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Collections;
import java.util.Scanner;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "com.example.mexicantrainandroid.EXTRA_TEXT";
    final static public String EXTRA_GAMEMODE = "GAMEMODE";
    final static public String EXTRA_MEX_TRAIN_KEY = "MEXTRAIN";
    final static public String EXTRA_BONEYARD_KEY = "BONEYARD";
    final static public String EXTRA_HUMAN_KEY = "HUMAN";
    final static public String EXTRA_COMPUTER_KEY = "COMPUTER";
    final static public String EXTRA_TURN_KEY = "TURN";
    final static public String EXTRA_ROUND_KEY = "ROUND";

    private TextView txt;
    private Button btn;
    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        btn = (Button) findViewById(R.id.gameLoadSaveButton);
        btn.setVisibility(View.INVISIBLE);
        edit = (EditText) findViewById(R.id.gameSaveFileEditText);
        edit.setVisibility(View.INVISIBLE);

        loadOrRound();

        Log.d("activity_game", "onCreate called");
    }

    /* *********************************************************************
    Function Name: loadOrRound
    Purpose: to let the user load a save or start a new round
    Parameters: none
    Return Value: void
    Algorithm:
                1) ask user to load game or start round
                2) create round based on their input
    Assistance Received: none
    ********************************************************************* */
    public void loadOrRound() {
        // Setup the welcome prompt
        String strQuestion = "Would you like to start a new game or load an existing save file?";

        txt = (TextView) findViewById(R.id.gameTextView);
        txt.setTextSize(20);
        txt.setText(strQuestion);

        btn = (Button) findViewById(R.id.gameLeftButton);
        btn.setText("NEW");
        // Setup the create new game functionality
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On new game prompt user for coin flip to determine the first turn
                Intent intent = new Intent(GameActivity.this, CoinActivity.class);
                startActivity(intent);
            }
        });

        btn = (Button) findViewById(R.id.gameRightButton);
        btn.setText("LOAD");
        // Setup the load new game functionality
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On load game simply get the filename and load in the values to the RoundActivity
                // Make the previous display invisible
                btn = (Button) findViewById(R.id.gameRightButton);
                btn.setVisibility(View.INVISIBLE);
                btn = (Button) findViewById(R.id.gameLeftButton);
                btn.setVisibility(View.INVISIBLE);
                txt = (TextView) findViewById(R.id.gameTextView);
                txt.setText("Enter the name of the game save file above");

                // Make the load button and the edit text field visible
                btn = (Button) findViewById(R.id.gameLoadSaveButton);
                btn.setText("LOAD");
                btn.setVisibility(View.VISIBLE);

                edit = (EditText) findViewById(R.id.gameSaveFileEditText);
                edit.setVisibility(View.VISIBLE);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Load the game at the given file if it exists
                        String save = edit.getText().toString() + ".txt";
                        readSave(save);
                    }
                });

            }
        });
    }

    /* *********************************************************************
    Function Name: readSave
    Purpose: lets the user load a savegame into a round
    Parameters: none
    Return Value: Round object
    Algorithm:
                1) call readSaveInput() to get the filename we need to load
                2) go through line by line and assign values to temporary ArrayLists
                3) read through the lines until the semicolons while assigning the values
                4) at the end we initialize all the temporary values into our actual members and objects
                    that we will use for a round
    Assistance Received: none
    ********************************************************************* */
    public void readSave(String filename)
    {
        int i = 0;
        int roundNum = 0;
        String temp = "";
        String temp2 = "";
        String line = "";
        String turn = "";
        Computer computer = new Computer();
        Human human = new Human();

        ArrayList<Tile> mexican_train = new ArrayList<Tile>();
        ArrayList<Tile> boneyard = new ArrayList<Tile>();
        ArrayList<Tile> computer_hand = new ArrayList<Tile>();
        ArrayList<Tile> computer_train = new ArrayList<Tile>();
        ArrayList<Tile> human_hand = new ArrayList<Tile>();
        ArrayList<Tile> human_train = new ArrayList<Tile>();

        // Default constructor value sets to 0-0
        Tile tempTile = new Tile();

        // Prompt for user input on save name
        //String filename = readSaveInput();

        // pass the path to the file as a parameter
        FileInputStream fis = null;

        try{
            fis = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            txt = (TextView) findViewById(R.id.gameTextView);
            txt.setText("File Read");

            while((text = br.readLine()) != null)
            {
                line = text;
                // Condition for if line is empty, don't increase i to keep
                // the switch logic in check for next fetched line
                if (line.equals(""))
                {
                    continue;
                }
                else
                {
                    switch (i)
                    {
                        // Round number
                        case 0:
                            for (int z = line.length()-1; z > 5; z--)
                            {
                                // Reads if no space is with the first character read
                                if (z == line.length()-1 && line.charAt(z) != ' ')
                                {
                                    temp = String.valueOf(line.charAt(z));
                                    int r = 1;
                                    while (line.charAt(z-r) != ' ' && line.charAt(z-r) != ':')
                                    {
                                        temp = line.charAt(z-r) + temp;
                                        r++;
                                    }
                                    continue;
                                }
                                if (line.charAt(z) == ':') { break; }
                                // Gathers string between 2 space characters
                                if (line.charAt(z) == ' ')
                                {
                                    int r = 1;
                                    while (line.charAt(z-r) != ' ' && line.charAt(z-r) != ':')
                                    {
                                        temp = line.charAt(z-r) + temp;
                                        r++;
                                    }
                                }
                            }
                            // Set the round number to the integer value of temp string (convert string to int)
                            roundNum = Integer.parseInt(temp);
                            temp = "";
                            break;
                        // Computer
                        case 1:
                            break;
                        // C Score
                        case 2:
                            for (int z = line.length()-1; z > 6; z--)
                            {
                                // reads if no space is with the first character read
                                if (z == line.length()-1 && line.charAt(z) != ' ')
                                {
                                    temp = String.valueOf(line.charAt(z));
                                    int r = 1;
                                    while (line.charAt(z-r) != ' ' && line.charAt(z-r) != ':')
                                    {
                                        temp = line.charAt(z-r) + temp;
                                        r++;
                                    }
                                    continue;
                                }
                                if (line.charAt(z) == ':') { break; }
                                // gathers string between 2 space characters
                                if (line.charAt(z) == ' ')
                                {
                                    int r = 1;
                                    while (line.charAt(z-r) != ' ' && line.charAt(z-r) != ':')
                                    {
                                        temp = line.charAt(z-r) + temp;
                                        r++;
                                    }
                                }
                            }
                            // Write score to a computer player
                            computer.setScore(Integer.parseInt(temp));
                            temp = "";

                            break;
                        // C Hand
                        case 3:
                            // Loops until z is == to the start of the line, "Hand:"
                            for (int z = line.length()-1; z > 5; z--)
                            {
                                // If we hit the : we know thats the end of the relevant data in the file
                                if (line.charAt(z) == ':') { break; }

                                // If we hit a '-' in the line, get the values on each side as a tile and push to hand
                                if (line.charAt(z) == '-')
                                {
                                    // Would have done this in 1 line but the compiler did not like that so I had to set
                                    // temp variables to use string to int here
                                    temp = String.valueOf(line.charAt(z-1));
                                    temp2 = String.valueOf(line.charAt(z+1));

                                    //tempTile.setTile(Integer.parseInt(temp), Integer.parseInt(temp2));
                                    computer_hand.add(new Tile(Integer.parseInt(temp), Integer.parseInt(temp2)));

                                    temp = "";
                                    temp2 = "";
                                }
                            }

                            break;
                        // C Train
                        case 4:
                            for (int z = line.length()-1; z > 6; z--)
                            {
                                if (line.charAt(z) == 'M')
                                {
                                    computer.setMarker(true);
                                }
                                else
                                {
                                    // If we hit the : we know thats the end of the relevant data in the file
                                    if (line.charAt(z) == ':') { break; }

                                    // If we hit a '-' in the line, get the values on each side as a tile and push to hand
                                    if (line.charAt(z) == '-')
                                    {
                                        // Would have done this in 1 line but the compiler did not like that so I had to set
                                        // temp variables to use string to int here
                                        temp = String.valueOf(line.charAt(z-1));
                                        temp2 = String.valueOf(line.charAt(z+1));

                                        //tempTile.setTile(Integer.parseInt(temp), Integer.parseInt(temp2));
                                        computer_train.add(new Tile(Integer.parseInt(temp), Integer.parseInt(temp2)));

                                        temp = "";
                                        temp2 = "";
                                    }
                                }
                            }
                            break;
                        // Human
                        case 5:
                            break;
                        // H Score
                        case 6:
                            for (int z = line.length()-1; z > 6; z--)
                            {
                                // reads if no space is with the first character read
                                if (z == line.length()-1 && line.charAt(z) != ' ')
                                {
                                    temp = String.valueOf(line.charAt(z));
                                    int r = 1;
                                    while (line.charAt(z-r) != ' ' && line.charAt(z-r) != ':')
                                    {
                                        temp = line.charAt(z-r) + temp;
                                        r++;
                                    }
                                    continue;
                                }
                                if (line.charAt(z) == ':') { break; }
                                // gathers string between 2 space characters
                                if (line.charAt(z) == ' ')
                                {
                                    int r = 1;
                                    while (line.charAt(z-r) != ' ' && line.charAt(z-r) != ':')
                                    {
                                        temp = line.charAt(z-r) + temp;
                                        r++;
                                    }
                                }
                            }
                            // Write score to a human player
                            human.setScore(Integer.parseInt(temp));
                            temp = "";

                            break;
                        // H Hand
                        case 7:
                            // Loops until z is == to the start of the line, "Hand:"
                            for (int z = line.length()-1; z > 5; z--)
                            {
                                // If we hit the : we know thats the end of the relevant data in the file
                                if (line.charAt(z) == ':') { break; }

                                // If we hit a '-' in the line, get the values on each side as a tile and push to hand
                                if (line.charAt(z) == '-')
                                {
                                    // Would have done this in 1 line but the compiler did not like that so I had to set
                                    // temp variables to use string to int here
                                    temp = String.valueOf(line.charAt(z-1));
                                    temp2 = String.valueOf(line.charAt(z+1));

                                    //tempTile.setTile(Integer.parseInt(temp), Integer.parseInt(temp2));
                                    human_hand.add(new Tile(Integer.parseInt(temp), Integer.parseInt(temp2)));

                                    temp = "";
                                    temp2 = "";
                                }
                            }
                            break;
                        // H Train
                        case 8:
                            for (int z = line.length()-1; z > 6; z--)
                            {
                                if (line.charAt(z) == 'M')
                                {
                                    human.setMarker(true);
                                }
                                else
                                {
                                    // If we hit the : we know thats the end of the relevant data in the file
                                    if (line.charAt(z) == ':') { break; }

                                    // If we hit a '-' in the line, get the values on each side as a tile and push to hand
                                    if (line.charAt(z) == '-')
                                    {
                                        // Would have done this in 1 line but the compiler did not like that so I had to set
                                        // temp variables to use string to int here
                                        temp = String.valueOf(line.charAt(z-1));
                                        temp2 = String.valueOf(line.charAt(z+1));

                                        //tempTile.setTile(Integer.parseInt(temp), Integer.parseInt(temp2));
                                        human_train.add(new Tile(Integer.parseInt(temp), Integer.parseInt(temp2)));

                                        temp = "";
                                        temp2 = "";
                                    }
                                }
                            }
                            break;
                        // Mexican Train
                        case 9:
                            for (int z = line.length()-1; z > 14; z--)
                            {
                                // If we hit the : we know thats the end of the relevant data in the file
                                if (line.charAt(z) == ':') { break; }

                                // If we hit a '-' in the line, get the values on each side as a tile and push to hand
                                if (line.charAt(z) == '-')
                                {
                                    // Would have done this in 1 line but the compiler did not like that so I had to set
                                    // temp variables to use string to int here
                                    temp = String.valueOf(line.charAt(z-1));
                                    temp2 = String.valueOf(line.charAt(z+1));

                                    //tempTile.setTile(Integer.parseInt(temp), Integer.parseInt(temp2));
                                    mexican_train.add(new Tile(Integer.parseInt(temp), Integer.parseInt(temp2)));

                                    temp = "";
                                    temp2 = "";
                                }
                            }
                            break;
                        // Boneyard
                        case 10:
                            for (int z = line.length()-1; z > 9; z--)
                            {
                                // If we hit the : we know thats the end of the relevant data in the file
                                if (line.charAt(z) == ':') { break; }

                                // If we hit a '-' in the line, get the values on each side as a tile and push to hand
                                if (line.charAt(z) == '-')
                                {
                                    // Would have done this in 1 line but the compiler did not like that so I had to set
                                    // temp variables to use string to int here
                                    temp = String.valueOf(line.charAt(z-1));
                                    temp2 = String.valueOf(line.charAt(z+1));

                                    //tempTile.setTile(Integer.parseInt(temp), Integer.parseInt(temp2));
                                    boneyard.add(new Tile(Integer.parseInt(temp), Integer.parseInt(temp2)));

                                    temp = "";
                                    temp2 = "";
                                }
                            }
                            break;
                        // Next player
                        case 11:
                            if (line.charAt(13) == 'C')
                            {
                                turn = "Computer";
                            }
                            else if (line.charAt(13) == 'H')
                            {
                                turn = "Human";
                            }
                            break;
                        default:
                            break;
                    }
                }
                i++;
            }

            // Close the input stream reader
            isr.close();

            // Flip the values of all ArrayLists to have beginning and end swapped
            // (note: they are read from file in the wrong order)
            Collections.reverse(mexican_train);
            Collections.reverse(human_hand);
            Collections.reverse(computer_hand);
            Collections.reverse(human_train);
            Collections.reverse(computer_train);
            Collections.reverse(boneyard);
            human.setHand(human_hand);
            human.setTrain(human_train);
            computer.setHand(computer_hand);
            computer.setTrain(computer_train);


            // Return a round created with the values we loaded
            //return new Round(turn, roundNum, human, computer, mexican_train, boneyard);

            Intent intent = new Intent(getApplicationContext(), RoundActivity.class);
            intent.putExtra(EXTRA_COMPUTER_KEY, computer);
            intent.putExtra(EXTRA_HUMAN_KEY, human);
            intent.putExtra(EXTRA_MEX_TRAIN_KEY, mexican_train);
            intent.putExtra(EXTRA_BONEYARD_KEY, boneyard);
            intent.putExtra(EXTRA_TURN_KEY, turn);
            intent.putExtra(EXTRA_ROUND_KEY, roundNum);
            intent.putExtra("FROM_ACTIVITY", "GameActivity");
            startActivity(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (fis != null)
            {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }// End of readSave()
}