/************************************************************
 * Name:  Alec Rizzo                                        *
 * Project: Project 4 - Mexican Train Java Android          *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date: 12/8/2021                                          *
 ************************************************************/
package com.example.mexicantrainandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RoundActivity extends AppCompatActivity {

    final static public String EXTRA_MEX_TRAIN_KEY = "MEXTRAIN";
    final static public String EXTRA_BONEYARD_KEY = "BONEYARD";
    final static public String EXTRA_MEX_TRAIN_ORPHAN_KEY = "MEXORPHAN";
    final static public String EXTRA_GAME_END_KEY = "GAMEEND";
    final static public String EXTRA_COMPUTER_KEY = "COMPUTER";
    final static public String EXTRA_HUMAN_KEY = "HUMAN";
    final static public String EXTRA_ENGINE_KEY = "ENGINE";
    final static public String EXTRA_TURN_KEY = "TURN";
    final static public String EXTRA_ROUND_KEY = "ROUND";

    private TextView txt, roundNumTxt, compScoreTxt, humScoreTxt, turnTxt;
    private Button btn, saveBtn, playBtn, helpBtn;
    private ImageView engineImage, boneyardImage;

    private String computerLogic = "";

    protected ArrayList<Tile> boneyard = new ArrayList<Tile>();
    private ArrayList<Tile> mexican_train = new ArrayList<Tile>();

    private Deck roundDeck = new Deck();
    private Tile engine = new Tile();

    private Human hum = new Human();
    private Computer comp = new Computer();

    private String turn = new String();
    private int roundNum;
    boolean coin;

    private boolean mexTrainOrphanDouble;
    private boolean gameEnd;
    private boolean isFirstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);

        Intent intent = getIntent();
        String prevActivity = intent.getStringExtra("FROM_ACTIVITY");
        if(getIntent() != null && getIntent().getExtras() != null)
        {
            // Use the extra text from game activity to determine if we are loading a save file or not
            // NEW will emulate a new game with empty constructor
            if(prevActivity.equals("CoinActivity"))
            {
                this.turn = intent.getStringExtra(CoinActivity.EXTRA_TEXT);
                this.roundNum = 1;
                this.boneyard.clear();
                this.mexican_train.clear();
                this.mexTrainOrphanDouble = false;
                this.gameEnd = false;

                txt = (TextView) findViewById(R.id.roundConsoleText);
                txt.setText("NEW GAME");
                txt.setTextSize(10);

                setUpRound();

            }else if (prevActivity.equals("GameActivity"))
            {
                // LOAD will emulate the constructor to load an existing saved round
                // get all the data from GameActivity here!
                txt = (TextView) findViewById(R.id.roundConsoleText);
                txt.setText("LOAD GAME\n");;
                txt.setTextSize(10);
                txt.setMovementMethod(new ScrollingMovementMethod());

                this.hum = (Human)getIntent().getSerializableExtra(GameActivity.EXTRA_HUMAN_KEY);
                this.comp = (Computer) getIntent().getSerializableExtra(GameActivity.EXTRA_COMPUTER_KEY);
                this.boneyard = (ArrayList<Tile>) getIntent().getSerializableExtra(GameActivity.EXTRA_BONEYARD_KEY);
                this.mexican_train = (ArrayList<Tile>) getIntent().getSerializableExtra(GameActivity.EXTRA_MEX_TRAIN_KEY);
                this.turn = getIntent().getStringExtra(GameActivity.EXTRA_TURN_KEY);
                this.roundNum = getIntent().getIntExtra(GameActivity.EXTRA_ROUND_KEY, 1);
                this.gameEnd = false;

                try {
                    // Set engine to first element of human train (engine)
                    setEngine(this.hum.getTrain().get(0));
                }catch (Exception e){
                    // Set engine to last element of computer train (engine)
                    setEngine(this.comp.getTrain().get(this.comp.getTrain().size()-1));
                }
            }else if(prevActivity.equals("RoundActivity"))
            {
                // Set up for next round
                this.hum = (Human)getIntent().getSerializableExtra(RoundActivity.EXTRA_HUMAN_KEY);
                this.comp = (Computer) getIntent().getSerializableExtra(RoundActivity.EXTRA_COMPUTER_KEY);
                this.boneyard = (ArrayList<Tile>) getIntent().getSerializableExtra(RoundActivity.EXTRA_BONEYARD_KEY);
                this.mexican_train = (ArrayList<Tile>) getIntent().getSerializableExtra(RoundActivity.EXTRA_MEX_TRAIN_KEY);
                this.turn = getIntent().getStringExtra(RoundActivity.EXTRA_TURN_KEY);
                this.roundNum = getIntent().getIntExtra(RoundActivity.EXTRA_ROUND_KEY, 1);
                //this.engine = (Tile)getIntent().getSerializableExtra(RoundActivity.EXTRA_ENGINE_KEY);
                // engine will be set based on round number anyways
                this.roundNum++;

                this.mexTrainOrphanDouble = false;
                this.gameEnd = false;
                this.boneyard.clear();
                this.mexican_train.clear();

                this.hum.newRoundReset();
                this.comp.newRoundReset();

                setUpRound();
                //determineFirstTurn();
                //gameplayLoop();
            }


            // Set the text to display where the mexican train is
            txt = (TextView) findViewById(R.id.roundMexicanTrainText);
            txt.setText("Mexican Train: ");
            txt.setTextSize(15);

            txt = (TextView) findViewById(R.id.roundHumanHandText);
            txt.setText("Hand: ");
            txt.setTextSize(15);

            saveBtn = (Button) findViewById(R.id.roundButton1);
            saveBtn.setText("SAVE");
            helpBtn = (Button) findViewById(R.id.roundButton2);
            helpBtn.setText("HELP");
            helpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    helpClick();
                }
            });
            playBtn = (Button) findViewById(R.id.roundButton3);
            playBtn.setText("PLAY");
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gameplay loop implementation in play button
                    playRound();
                }
            });

            // Print all our values to the user
            updateDisplay();
        }

        Log.d("activity_round", "onCreate called");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("activity_round", "onResume called");
    }

    private void helpClick()
    {
        String helpString = hum.help(mexican_train, comp, mexTrainOrphanDouble);
        txt = (TextView) findViewById(R.id.roundConsoleText);
        txt.setText(helpString);
    }

    // use all print functions to update the display
    private void updateDisplay()
    {
        printEngine();
        printHumanTrain();
        printComputerTrain();
        printMexicanTrain();
        printHumanHand();
        printOrphan();
        printRoundNum();
        printComputerScore();
        printHumanScore();
        printTurn();
        printBoneyard();
        printBoneyardText();
        printMarkers();
    }
    private void clearDisplay()
    {
        clearEngine();
        clearHumanTrain();
        clearComputerTrain();
        clearMexicanTrain();
        clearHumanHand();
        clearOrphan();
        clearRoundNum();
        clearComputerScore();
        clearHumanScore();
        clearTurn();
        clearBoneyard();
        clearBoneyardText();
        clearMarkers();
    }

    private void clearMarkers()
    {
        txt = (TextView) findViewById(R.id.roundHumanMarkerText);
        txt.setText("");
        txt = (TextView) findViewById(R.id.roundComputerMarkerText);
        txt.setText("");
        txt = null;
    }

    private void clearBoneyardText()
    {
        TextView boneyardTxt = (TextView) findViewById(R.id.roundBoneyardText);
        boneyardTxt.setText("");
    }

    private void clearBoneyard()
    {
        boneyardImage = (ImageView) findViewById(R.id.roundImageBoneyard);
        setTileImage(boneyardImage, new Tile(0,0));
    }

    private void clearTurn()
    {
        turnTxt = (TextView) findViewById(R.id.roundTurnText);
        turnTxt.setText("");
    }

    private void clearHumanScore()
    {
        humScoreTxt = (TextView) findViewById(R.id.roundHumanScoreText);
        humScoreTxt.setText("");
    }

    private void clearComputerScore()
    {
        compScoreTxt = (TextView) findViewById(R.id.roundComputerScoreText);
        compScoreTxt.setText("");
    }

    private void clearEngine()
    {
        engineImage = (ImageView) findViewById(R.id.roundImageEngine);
        engineImage.setImageResource(R.drawable.d0_0);
    }

    private void clearHumanTrain()
    {
        LinearLayout trainLayout = findViewById(R.id.roundHumanTrainLayout);
        trainLayout.removeAllViews();
    }

    private void clearComputerTrain()
    {
        LinearLayout trainLayout = findViewById(R.id.roundComputerTrainLayout);
        trainLayout.removeAllViews();
    }

    private void clearMexicanTrain()
    {
        LinearLayout trainLayout = findViewById(R.id.roundMexicanTrainLayout);
        trainLayout.removeAllViews();
    }

    private void clearHumanHand()
    {
        LinearLayout handLayout = findViewById(R.id.roundHumanHandLayout);
        handLayout.removeAllViews();
    }

    private void clearOrphan()
    {
            txt = (TextView) findViewById(R.id.roundHumanOrphanText);
            txt.setTextSize(15);
            txt.setText("");

            txt = (TextView) findViewById(R.id.roundComputerOrphanText);
            txt.setTextSize(15);
            txt.setText("");

            txt = (TextView) findViewById(R.id.roundMexicanTrainText);
            txt.setTextSize(15);
            txt.setText("Mexican Train:");
    }

    // Play through the current turn and open new activity to get input for human turn
    private void playRound()
    {
        // Determine which players turn based on this->turn
        if (this.turn.equals("Human"))
        {
            try {

                // This call to startActivity() is not opening properly
                // Immediately closes upon open. But never calls onCreate() in HumanTurnActivity
                /*
                // Pass all necessary turn data to the human turn activity
                Intent turnIntent = new Intent(RoundActivity.this, HumanTurnActivity.class);
                turnIntent.putExtra("FROM_ACTIVITY", "RoundActivity");
                turnIntent.putExtra(EXTRA_COMPUTER_KEY, comp);
                turnIntent.putExtra(EXTRA_HUMAN_KEY, hum);
                turnIntent.putExtra(EXTRA_MEX_TRAIN_KEY, mexican_train);
                turnIntent.putExtra(EXTRA_BONEYARD_KEY, boneyard);
                turnIntent.putExtra(EXTRA_GAME_END_KEY, gameEnd);
                turnIntent.putExtra(EXTRA_MEX_TRAIN_ORPHAN_KEY, mexTrainOrphanDouble);
                turnIntent.putExtra(EXTRA_ENGINE_KEY, engine);
                startActivity(turnIntent);
                */

                this.turn = "Computer";
            }catch(Exception e)
            {
                TextView txt = (TextView) findViewById(R.id.roundConsoleText);
                txt.append("Could not play Human turn\n");
                txt.setMovementMethod(new ScrollingMovementMethod());
            }

        }
        else if (this.turn.equals("Computer"))
        {
            // Computer plays
            // !ALEC String computerLogic was supposed to get the logic from the computer for its play but
                // I think it can't assign to computerLogic in playTurn. data is blank upon return
            if(gameEnd == true)
            {
                clearDisplay();
                txt = (TextView) findViewById(R.id.roundConsoleText);
                txt.setText("Game Over");
                saveBtn = (Button)findViewById(R.id.roundButton1);
                saveBtn.setVisibility(View.INVISIBLE);
                helpBtn = (Button)findViewById(R.id.roundButton2);
                helpBtn.setVisibility(View.INVISIBLE);
                playBtn = (Button)findViewById(R.id.roundButton3);
                playBtn.setText("NEW?");
                playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Case for starting new game after game over
                        Intent intent = new Intent(getApplicationContext(), RoundActivity.class);
                        intent.putExtra(EXTRA_COMPUTER_KEY, comp);
                        intent.putExtra(EXTRA_HUMAN_KEY, hum);
                        intent.putExtra(EXTRA_MEX_TRAIN_KEY, mexican_train);
                        intent.putExtra(EXTRA_BONEYARD_KEY, boneyard);
                        intent.putExtra(EXTRA_TURN_KEY, turn);
                        intent.putExtra(EXTRA_ROUND_KEY, roundNum);
                        intent.putExtra("FROM_ACTIVITY", "RoundActivity");
                        startActivity(intent);
                    }
                });

            }
            else
            {
                this.computerLogic = "";
                this.comp.playTurn(this.mexican_train, this.boneyard, this.hum,
                        this.mexTrainOrphanDouble, this.gameEnd, this.computerLogic);
                this.turn = "Human";
                clearDisplay();
                updateDisplay();
                txt = (TextView) findViewById(R.id.roundConsoleText);
                txt.setText(computerLogic);
                // !ALEC this call to playTurn seems to be playing more than 2 tiles
                // Fixed with the clearDisplay() function, was not a data related issue just display
            }
        }
    }

    private void printMarkers() {
        if(hum.getMarker() == true)
        {
            txt = (TextView) findViewById(R.id.roundHumanMarkerText);
            txt.setText("Human Marked!");
        }
        if(comp.getMarker() == true)
        {
            txt = (TextView) findViewById(R.id.roundComputerMarkerText);
            txt.setText("Computer Marked!");
        }
        txt = null;
    }

    private void printBoneyard()
    {
        boneyardImage = (ImageView) findViewById(R.id.roundImageBoneyard);
        setTileImage(boneyardImage, boneyard.get(0));
    }

    private void printOrphan()
    {
        if(hum.getOrphan() == true)
        {
            txt = (TextView) findViewById(R.id.roundHumanOrphanText);
            txt.setTextSize(15);
            txt.setText("ORPHAN DOUBLE");
        }
        if(comp.getOrphan() == true)
        {
            txt = (TextView) findViewById(R.id.roundComputerOrphanText);
            txt.setTextSize(15);
            txt.setText("ORPHAN DOUBLE");
        }
        if(mexTrainOrphanDouble == true)
        {
            txt = (TextView) findViewById(R.id.roundMexicanTrainText);
            txt.setTextSize(15);
            txt.setText("Mexican Train: \nORPHAN DOUBLE");
        }
    }

    // Displays the values tiles of the human hand to the activity in a horizontal layout
    private void printHumanHand()
    {
        // Learned how to do this from https://www.youtube.com/watch?v=hl0AcuplFwE
        LinearLayout humanHandLayout = findViewById(R.id.roundHumanHandLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        for(int i = 0; i < hum.getHand().size(); i++ )
        {
            View view = inflater.inflate(R.layout.tile, humanHandLayout, false);
            ImageView tileImage = view.findViewById(R.id.tileImageView);
            // Set image to whichever matches our required tile
            //tileImage.setImageResource(R.drawable.d0_0);
            setTileImage(tileImage, hum.getHand().get(i));
            humanHandLayout.addView(view);
        }
    }

    // Displays the values tiles of the human train to the activity in a horizontal layout
    private void printHumanTrain()
    {
        // Learned how to do this from https://www.youtube.com/watch?v=hl0AcuplFwE
        LinearLayout humanTrainLayout = findViewById(R.id.roundHumanTrainLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        for(int i = 0; i < hum.getTrain().size(); i++ )
        {
            if(hum.getTrain().get(i).getSideA() == engine.getSideA() &&
                    hum.getTrain().get(i).getSideB() == engine.getSideB())
            {
                continue;
            }else
            {
                View view = inflater.inflate(R.layout.tile, humanTrainLayout, false);
                ImageView tileImage = view.findViewById(R.id.tileImageView);
                // Set image to whichever matches our required tile
                //tileImage.setImageResource(R.drawable.d0_0);
                setTileImage(tileImage, hum.getTrain().get(i));
                humanTrainLayout.addView(view);
            }
        }
    }

    // Displays the values tiles of the human train to the activity in a horizontal layout
    private void printMexicanTrain()
    {
        // Learned how to do this from https://www.youtube.com/watch?v=hl0AcuplFwE
        LinearLayout mexicanTrainLayout = findViewById(R.id.roundMexicanTrainLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        for(int i = 0; i < this.mexican_train.size(); i++ )
        {
            if(mexican_train.get(i).getSideA() == engine.getSideA() &&
                    mexican_train.get(i).getSideB() == engine.getSideB())
            {
                continue;
            }else
            {
                View view = inflater.inflate(R.layout.tile, mexicanTrainLayout, false);
                ImageView tileImage = view.findViewById(R.id.tileImageView);
                // Set image to whichever matches our required tile
                //tileImage.setImageResource(R.drawable.d0_0);
                setTileImage(tileImage, this.mexican_train.get(i));
                mexicanTrainLayout.addView(view);
            }
        }
    }

    // Displays the values of the computer train to the activity in a horizontal layout
    // Reversed logic due to the way computer train is displayed and stored as a ArrayList
    private void printComputerTrain()
    {
        // Learned how to do this from https://www.youtube.com/watch?v=hl0AcuplFwE
        LinearLayout computerTrainLayout = findViewById(R.id.roundComputerTrainLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        for(int i = 0; i < comp.getTrain().size(); i++ )
        {
            if(comp.getTrain().get(i).getSideA() == engine.getSideA() &&
                    comp.getTrain().get(i).getSideB() == engine.getSideB())
            {
                continue;
            }else
            {
                View view = inflater.inflate(R.layout.tile, computerTrainLayout, false);
                ImageView tileImage = view.findViewById(R.id.tileImageView);
                // Set image to whichever matches our required tile
                //tileImage.setImageResource(R.drawable.d0_0);
                setTileImage(tileImage, comp.getTrain().get(i));
                computerTrainLayout.addView(view);
            }
        }

        // Weighs the scrollview to the right so that it displays values closest to the train
        // before the user scrolls at all: https://stackoverflow.com/questions/28804932/how-to-make-horizontalscrollview-right-to-left-scroll-android/28805094
        HorizontalScrollView computerTrainScrollView = findViewById(R.id.roundComputerTrainScrollView);
        computerTrainScrollView.postDelayed(new Runnable() {
            public void run() {
                computerTrainScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 0L);
    }

    // Function to set the proper tile image resource to the imgView based on tile A and B values
    private void setTileImage(ImageView imgView, Tile tile)
    {
        // Go through every value in order and set image resource accordingly
        if(tile.getSideA() == 0 && tile.getSideB() == 0)
        {
            imgView.setImageResource(R.drawable.d0_0);
            return;
        }
        else if(tile.getSideA() == 0 && tile.getSideB() == 1)
        {
            imgView.setImageResource(R.drawable.d0_1);
            return;
        }
        else if(tile.getSideA() == 0 && tile.getSideB() == 2)
        {
            imgView.setImageResource(R.drawable.d0_2);
            return;
        }
        else if(tile.getSideA() == 0 && tile.getSideB() == 3)
        {
            imgView.setImageResource(R.drawable.d0_3);
            return;
        }
        else if(tile.getSideA() == 0 && tile.getSideB() == 4)
        {
            imgView.setImageResource(R.drawable.d0_4);
            return;
        }
        else if(tile.getSideA() == 0 && tile.getSideB() == 5)
        {
            imgView.setImageResource(R.drawable.d0_5);
            return;
        }
        else if(tile.getSideA() == 0 && tile.getSideB() == 6)
        {
            imgView.setImageResource(R.drawable.d0_6);
            return;
        }
        else if(tile.getSideA() == 0 && tile.getSideB() == 7)
        {
            imgView.setImageResource(R.drawable.d0_7);
            return;
        }
        else if(tile.getSideA() == 0 && tile.getSideB() == 8)
        {
            imgView.setImageResource(R.drawable.d0_8);
            return;
        }
        else if(tile.getSideA() == 0 && tile.getSideB() == 9)
        {
            imgView.setImageResource(R.drawable.d0_9);
            return;
        }
        else if(tile.getSideA() == 1 && tile.getSideB() == 1)
        {
            imgView.setImageResource(R.drawable.d1_1);
            return;
        }
        else if(tile.getSideA() == 1 && tile.getSideB() == 2)
        {
            imgView.setImageResource(R.drawable.d1_2);
            return;
        }
        else if(tile.getSideA() == 1 && tile.getSideB() == 3)
        {
            imgView.setImageResource(R.drawable.d1_3);
            return;
        }
        else if(tile.getSideA() == 1 && tile.getSideB() == 4)
        {
            imgView.setImageResource(R.drawable.d1_4);
            return;
        }
        else if(tile.getSideA() == 1 && tile.getSideB() == 5)
        {
            imgView.setImageResource(R.drawable.d1_5);
            return;
        }
        else if(tile.getSideA() == 1 && tile.getSideB() == 6)
        {
            imgView.setImageResource(R.drawable.d1_6);
            return;
        }
        else if(tile.getSideA() == 1 && tile.getSideB() == 7)
        {
            imgView.setImageResource(R.drawable.d1_7);
            return;
        }
        else if(tile.getSideA() == 1 && tile.getSideB() == 8)
        {
            imgView.setImageResource(R.drawable.d1_8);
            return;
        }
        else if(tile.getSideA() == 1 && tile.getSideB() == 9)
        {
            imgView.setImageResource(R.drawable.d1_9);
            return;
        }
        else if(tile.getSideA() == 2 && tile.getSideB() == 2)
        {
            imgView.setImageResource(R.drawable.d2_2);
            return;
        }
        else if(tile.getSideA() == 2 && tile.getSideB() == 3)
        {
            imgView.setImageResource(R.drawable.d2_3);
            return;
        }
        else if(tile.getSideA() == 2 && tile.getSideB() == 4)
        {
            imgView.setImageResource(R.drawable.d2_4);
            return;
        }
        else if(tile.getSideA() == 2 && tile.getSideB() == 5)
        {
            imgView.setImageResource(R.drawable.d2_5);
            return;
        }
        else if(tile.getSideA() == 2 && tile.getSideB() == 6)
        {
            imgView.setImageResource(R.drawable.d2_6);
            return;
        }
        else if(tile.getSideA() == 2 && tile.getSideB() == 7)
        {
            imgView.setImageResource(R.drawable.d2_7);
            return;
        }
        else if(tile.getSideA() == 2 && tile.getSideB() == 8)
        {
            imgView.setImageResource(R.drawable.d2_8);
            return;
        }
        else if(tile.getSideA() == 2 && tile.getSideB() == 9)
        {
            imgView.setImageResource(R.drawable.d2_9);
            return;
        }
        else if(tile.getSideA() == 3 && tile.getSideB() == 3)
        {
            imgView.setImageResource(R.drawable.d3_3);
            return;
        }
        else if(tile.getSideA() == 3 && tile.getSideB() == 4)
        {
            imgView.setImageResource(R.drawable.d3_4);
            return;
        }
        else if(tile.getSideA() == 3 && tile.getSideB() == 5)
        {
            imgView.setImageResource(R.drawable.d3_5);
            return;
        }
        else if(tile.getSideA() == 3 && tile.getSideB() == 6)
        {
            imgView.setImageResource(R.drawable.d3_6);
            return;
        }
        else if(tile.getSideA() == 3 && tile.getSideB() == 7)
        {
            imgView.setImageResource(R.drawable.d3_7);
            return;
        }
        else if(tile.getSideA() == 3 && tile.getSideB() == 8)
        {
            imgView.setImageResource(R.drawable.d3_8);
            return;
        }
        else if(tile.getSideA() == 3 && tile.getSideB() == 9)
        {
            imgView.setImageResource(R.drawable.d3_9);
            return;
        }
        else if(tile.getSideA() == 4 && tile.getSideB() == 4)
        {
            imgView.setImageResource(R.drawable.d4_4);
            return;
        }
        else if(tile.getSideA() == 4 && tile.getSideB() == 5)
        {
            imgView.setImageResource(R.drawable.d4_5);
            return;
        }
        else if(tile.getSideA() == 4 && tile.getSideB() == 6)
        {
            imgView.setImageResource(R.drawable.d4_6);
            return;
        }
        else if(tile.getSideA() == 4 && tile.getSideB() == 7)
        {
            imgView.setImageResource(R.drawable.d4_7);
            return;
        }
        else if(tile.getSideA() == 4 && tile.getSideB() == 8)
        {
            imgView.setImageResource(R.drawable.d4_8);
            return;
        }
        else if(tile.getSideA() == 4 && tile.getSideB() == 9)
        {
            imgView.setImageResource(R.drawable.d4_9);
            return;
        }
        else if(tile.getSideA() == 5 && tile.getSideB() == 5)
        {
            imgView.setImageResource(R.drawable.d5_5);
            return;
        }
        else if(tile.getSideA() == 5 && tile.getSideB() == 6)
        {
            imgView.setImageResource(R.drawable.d5_6);
            return;
        }
        else if(tile.getSideA() == 5 && tile.getSideB() == 7)
        {
            imgView.setImageResource(R.drawable.d5_7);
            return;
        }
        else if(tile.getSideA() == 5 && tile.getSideB() == 8)
        {
            imgView.setImageResource(R.drawable.d5_8);
            return;
        }
        else if(tile.getSideA() == 5 && tile.getSideB() == 9)
        {
            imgView.setImageResource(R.drawable.d5_9);
            return;
        }
        else if(tile.getSideA() == 6 && tile.getSideB() == 6)
        {
            imgView.setImageResource(R.drawable.d6_6);
            return;
        }
        else if(tile.getSideA() == 6 && tile.getSideB() == 7)
        {
            imgView.setImageResource(R.drawable.d6_7);
            return;
        }
        else if(tile.getSideA() == 6 && tile.getSideB() == 8)
        {
            imgView.setImageResource(R.drawable.d6_8);
            return;
        }
        else if(tile.getSideA() == 6 && tile.getSideB() == 9)
        {
            imgView.setImageResource(R.drawable.d6_9);
            return;
        }
        else if(tile.getSideA() == 7 && tile.getSideB() == 7)
        {
            imgView.setImageResource(R.drawable.d7_7);
            return;
        }
        else if(tile.getSideA() == 7 && tile.getSideB() == 8)
        {
            imgView.setImageResource(R.drawable.d7_8);
            return;
        }
        else if(tile.getSideA() == 7 && tile.getSideB() == 9)
        {
            imgView.setImageResource(R.drawable.d7_9);
            return;
        }
        else if(tile.getSideA() == 8 && tile.getSideB() == 8)
        {
            imgView.setImageResource(R.drawable.d8_8);
            return;
        }
        else if(tile.getSideA() == 8 && tile.getSideB() == 9)
        {
            imgView.setImageResource(R.drawable.d8_9);
            return;
        }
        else if(tile.getSideA() == 9 && tile.getSideB() == 9)
        {
            imgView.setImageResource(R.drawable.d9_9);
            return;
        }
        else if(tile.getSideA() == 1 && tile.getSideB() == 0)
        {
            imgView.setImageResource(R.drawable.d0_1);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 2 && tile.getSideB() == 0)
        {
            imgView.setImageResource(R.drawable.d0_2);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 3 && tile.getSideB() == 0)
        {
            imgView.setImageResource(R.drawable.d0_3);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 4 && tile.getSideB() == 0)
        {
            imgView.setImageResource(R.drawable.d0_4);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 5 && tile.getSideB() == 0)
        {
            imgView.setImageResource(R.drawable.d0_5);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 6 && tile.getSideB() == 0)
        {
            imgView.setImageResource(R.drawable.d0_6);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 7 && tile.getSideB() == 0)
        {
            imgView.setImageResource(R.drawable.d0_7);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 8 && tile.getSideB() == 0)
        {
            imgView.setImageResource(R.drawable.d0_8);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 9 && tile.getSideB() == 0)
        {
            imgView.setImageResource(R.drawable.d0_9);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 2 && tile.getSideB() == 1)
        {
            imgView.setImageResource(R.drawable.d1_2);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 3 && tile.getSideB() == 1)
        {
            imgView.setImageResource(R.drawable.d1_3);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 4 && tile.getSideB() == 1)
        {
            imgView.setImageResource(R.drawable.d1_4);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 5 && tile.getSideB() == 1)
        {
            imgView.setImageResource(R.drawable.d1_5);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 6 && tile.getSideB() == 1)
        {
            imgView.setImageResource(R.drawable.d1_6);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 7 && tile.getSideB() == 1)
        {
            imgView.setImageResource(R.drawable.d1_7);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 8 && tile.getSideB() == 1)
        {
            imgView.setImageResource(R.drawable.d1_8);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 9 && tile.getSideB() == 1)
        {
            imgView.setImageResource(R.drawable.d1_9);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 3 && tile.getSideB() == 2)
        {
            imgView.setImageResource(R.drawable.d2_3);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 4 && tile.getSideB() == 2)
        {
            imgView.setImageResource(R.drawable.d2_4);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 5 && tile.getSideB() == 2)
        {
            imgView.setImageResource(R.drawable.d2_5);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 6 && tile.getSideB() == 2)
        {
            imgView.setImageResource(R.drawable.d2_6);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 7 && tile.getSideB() == 2)
        {
            imgView.setImageResource(R.drawable.d2_7);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 8 && tile.getSideB() == 2)
        {
            imgView.setImageResource(R.drawable.d2_8);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 9 && tile.getSideB() == 2)
        {
            imgView.setImageResource(R.drawable.d2_9);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 4 && tile.getSideB() == 3)
        {
            imgView.setImageResource(R.drawable.d3_4);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 5 && tile.getSideB() == 3)
        {
            imgView.setImageResource(R.drawable.d3_5);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 6 && tile.getSideB() == 3)
        {
            imgView.setImageResource(R.drawable.d3_6);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 7 && tile.getSideB() == 3)
        {
            imgView.setImageResource(R.drawable.d3_7);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 8 && tile.getSideB() == 3)
        {
            imgView.setImageResource(R.drawable.d3_8);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 9 && tile.getSideB() == 3)
        {
            imgView.setImageResource(R.drawable.d3_9);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 5 && tile.getSideB() == 4)
        {
            imgView.setImageResource(R.drawable.d4_5);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 6 && tile.getSideB() == 4)
        {
            imgView.setImageResource(R.drawable.d4_6);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 7 && tile.getSideB() == 4)
        {
            imgView.setImageResource(R.drawable.d4_7);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 8 && tile.getSideB() == 4)
        {
            imgView.setImageResource(R.drawable.d4_8);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 9 && tile.getSideB() == 4)
        {
            imgView.setImageResource(R.drawable.d4_9);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 6 && tile.getSideB() == 5)
        {
            imgView.setImageResource(R.drawable.d5_6);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 7 && tile.getSideB() == 5)
        {
            imgView.setImageResource(R.drawable.d5_7);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 8 && tile.getSideB() == 5)
        {
            imgView.setImageResource(R.drawable.d5_8);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 9 && tile.getSideB() == 5)
        {
            imgView.setImageResource(R.drawable.d5_9);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 7 && tile.getSideB() == 6)
        {
            imgView.setImageResource(R.drawable.d6_7);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 8 && tile.getSideB() == 6)
        {
            imgView.setImageResource(R.drawable.d6_8);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 9 && tile.getSideB() == 6)
        {
            imgView.setImageResource(R.drawable.d6_9);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 8 && tile.getSideB() == 7)
        {
            imgView.setImageResource(R.drawable.d7_8);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 9 && tile.getSideB() == 7)
        {
            imgView.setImageResource(R.drawable.d7_9);
            imgView.setRotation(90);
            return;
        }
        else if(tile.getSideA() == 9 && tile.getSideB() == 8)
        {
            imgView.setImageResource(R.drawable.d8_9);
            imgView.setRotation(90);
            return;
        }
    }

    // Print the turn to display to roundTurnText in the top right of our activity
    private void printTurn()
    {
        turnTxt = (TextView) findViewById(R.id.roundTurnText);
        turnTxt.setText("Turn: " + this.turn);
    }

    // Print human score text to display to roundHumanScoreText in bottom right of activity
    private void printHumanScore()
    {
        humScoreTxt = (TextView) findViewById(R.id.roundHumanScoreText);
        humScoreTxt.setText("Human: " + hum.getScore());
    }

    // Set computer score text to display to roundComputerScoreText in the bottom left of activity
    private void printComputerScore()
    {
        compScoreTxt = (TextView) findViewById(R.id.roundComputerScoreText);
        compScoreTxt.setText("Computer: " + comp.getScore());
    }

    // Set the round number to display to roundRoundNumText in the top left of activity
    private void printRoundNum()
    {
        roundNumTxt = (TextView) findViewById(R.id.roundRoundNumText);
        roundNumTxt.setText("Round: " + this.roundNum);
    }

    // Set the round number to display to roundRoundNumText in the top left of activity
    private void clearRoundNum()
    {
        roundNumTxt = (TextView) findViewById(R.id.roundRoundNumText);
        roundNumTxt.setText("");
    }




    /* *********************************************************************
    Function Name: setUpRound
    Purpose: set up a new round
    Parameters: none
    Return Value: void
    Algorithm: sets up a new round in order
                1) engine is determined based on the number of rounds played
                2) tiles are shuffled
                3) deal 16 tiles to human
                4) deal 16 tiles to computer
                5) place remaining tiles in boneyard
                6) set human and computer train to start with the current engine
    Assistance Received: none
    ********************************************************************* */
    public void setUpRound()
    {
        // The engine for the round is determined from the rounds played
        Tile currentEngine = getEngine();
        setEngine(currentEngine);
        //printEngine();
        removeEngineFromDeck(currentEngine);
        this.mexican_train.add(currentEngine);

        // The remaining tiles are shuffled
        this.roundDeck.shuffleDeck();

        // The human player is dealt 16 tiles
        this.roundDeck.popNumTiles(this.hum.hand, 16);

        // The computer player is dealt 16 tiles
        this.roundDeck.popNumTiles(this.comp.hand, 16);

        // The remaining tiles are placed in the boneyard
        this.roundDeck.popNumTiles(this.boneyard, roundDeck.double_nine_set.size());

        // Starts the human and computers personal trains off with the current engine
        this.hum.addTileTrain(currentEngine, this.hum.train);
        this.comp.addTileComputerTrain(currentEngine);
    }

    // Function to set the engine image based on the sideA int of the current engine
    private void printEngine()
    {
        engineImage = (ImageView) findViewById(R.id.roundImageEngine);
        switch(this.engine.getSideA())
        {
            case 0:
                engineImage.setImageResource(R.drawable.d0_0);
                break;
            case 1:
                engineImage.setImageResource(R.drawable.d1_1);
                break;
            case 2:
                engineImage.setImageResource(R.drawable.d2_2);
                break;
            case 3:
                engineImage.setImageResource(R.drawable.d3_3);
                break;
            case 4:
                engineImage.setImageResource(R.drawable.d4_4);
                break;
            case 5:
                engineImage.setImageResource(R.drawable.d5_5);
                break;
            case 6:
                engineImage.setImageResource(R.drawable.d6_6);
                break;
            case 7:
                engineImage.setImageResource(R.drawable.d7_7);
                break;
            case 8:
                engineImage.setImageResource(R.drawable.d8_8);
                break;
            case 9:
                engineImage.setImageResource(R.drawable.d9_9);
                break;
            default:
                break;
        }

    }

    /* *********************************************************************
    Function Name: getEngine
    Purpose: determines the engine based on round number
    Parameters: none
    Return Value: Tile
    Algorithm: use roundNum % 10 to get a number 0-9 for the engine
                return a tile with the number obtained on both sides
    Assistance Received: none
    ********************************************************************* */
    // Determines the current engine from a double nine set
    private Tile getEngine()
    {
        int engineVal = -1;

        // Determine the last digit of the round number to get the current engine
        switch (this.roundNum % 10)
        {
            case 0: engineVal = 0;
                break;
            case 1: engineVal = 9;
                break;
            case 2: engineVal = 8;
                break;
            case 3: engineVal = 7;
                break;
            case 4: engineVal = 6;
                break;
            case 5: engineVal = 5;
                break;
            case 6: engineVal = 4;
                break;
            case 7: engineVal = 3;
                break;
            case 8: engineVal = 2;
                break;
            case 9: engineVal = 1;
                break;
            default: engineVal = -1;
                break;

        }

        // Set up the tile of the current engine engineVal-engineVal
        Tile engine = new Tile(engineVal, engineVal);
        return engine;
    }// End of getEngine()

    /* *********************************************************************
    Function Name: setEngine
    Purpose: set the member engine to the passed tile
    Parameters: eng, Tile to assign to engine
    Return Value: void
    Algorithm: check that eng is a double
                assign eng to engine
    Assistance Received: none
    ********************************************************************* */
    // Fuction to change the current engine to a new value (to be used mainly with loading saves)
    private void setEngine(Tile eng)
    {
        // Check for equivalent sides on the engine, then set engine
        if (eng.getSideA() == eng.getSideB())
        {
            this.engine = eng;
        }
    }//End of setEngine()

    /* *********************************************************************
    Function Name: removeEngineFromDeck
    Purpose: remove the engine tile passed from the deck
    Parameters: eng, tile to search through the roundDeck and remove
    Return Value: void
    Algorithm: search through the roundDeck and remove the tile that matches
                eng in the deck
    Assistance Received: none
    ********************************************************************* */
    // Fucntion to remove the current engine from the deck so it is not in play
    private void removeEngineFromDeck(Tile eng)
    {
        for (int i = 0; i < roundDeck.getDOUBLE_NINE_SIZE(); i++)
        {
            int tempSideA = this.roundDeck.double_nine_set.get(i).getSideA();
            int tempSideB = this.roundDeck.double_nine_set.get(i).getSideB();

            // If condition for matching engine tile (i.e. Tile eng 0-0 and tempA and tempB are 0-0)
            if (tempSideA == eng.getSideA() && tempSideB == eng.getSideB())
            {
                // Remove the engine tiles matching tile from the roundDeck
                this.roundDeck.double_nine_set.remove(i);
                break;
            }
        }
    }// End of removeEngineFromDeck()

    /* *********************************************************************
    Function Name: flipCoin
    Purpose: flips the coin returning 1 for tails or 0 for heads
    Parameters: none
    Return Value: void
    Algorithm: seed random number generator, call rand(), if random is 1 set
                value of our coin face to tails, else if its 2 set it to 0
    Assistance Received: https://www.w3schools.in/java-program/coin-toss/
    ********************************************************************* */
    // Flip the coin randomly, return 0 for heads, 1 for tails
    private boolean flipCoin(boolean userVal)
    {
        boolean face;
        if (Math.random() < 0.5)
        {
            // value is tails
            face = true;
        }
        else
        {
            // value is heads
            face = false;
        }
        return face;
    }// End of flipCoin()

    /* *********************************************************************
    Function Name: printFirstTurn
    Purpose: prints the first turn based on the Round member turn
    Parameters: none
    Return Value: void
    Algorithm: output the first turn
    Assistance Received: none
    ********************************************************************* */
    // Prints which player goes first based on local turn value of round
    private void printFirstTurn()
    {
        //setContentView(R.layout.activity_round);
        // Determine the text for the GUI output
        String strGUI = "";
        if (coin == false)
        {
            strGUI = "Coin is heads";
        }
        else if (coin == true)
        {
            strGUI = "Coin is tails";
        }

        strGUI = strGUI + " " + this.turn + " goes first";

        // Show the user the result of the coin flip in the GUI
        txt = (TextView) findViewById(R.id.roundConsoleText);
        txt.setTextSize(20);
        txt.setText(strGUI);

        // Make both the buttons invisible
        btn = (Button) findViewById(R.id.roundButton1);
        btn.setVisibility(View.INVISIBLE);
        btn = (Button) findViewById(R.id.roundButton2);
        btn.setVisibility(View.INVISIBLE);
        btn = (Button) findViewById(R.id.roundButton3);
        btn.setText("CONTINUE");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start round
            }
        });

    }// End of printFirstTurn()

    /* *********************************************************************
    Function Name: printBoneyard
    Purpose: prints the boneyard to output
    Parameters: none
    Return Value: void
    Algorithm: output all values of the boneyard
    Assistance Received: none
    ********************************************************************* */
    // Prints the boneyard and the amount of tiles remaining if boneyard has values
    private void printBoneyardText()
    {
        txt = (TextView) findViewById(R.id.roundBoneyardText);
        if (this.boneyard.size() > 0)
        {
            txt.setText("Boneyard:\n (" + this.boneyard.size() + " tiles remaining)\n");
            txt.setTextSize(10);
        }
        txt = null;
    }// End of printBoneyard()

    /* *********************************************************************
    Function Name: changeTurn()
    Purpose: swap the users turn based on current turn
    Parameters: none
    Return Value: void
    Algorithm: changes turn to human if computer, computer if human
    Assistance Received: none
    ********************************************************************* */
    private void changeTurn()
    {
        if (this.turn == "Human")
        {
            this.turn = "Computer";
        }
        else if (this.turn == "Computer")
        {
            this.turn = "Human";
        }
        else
        {
            //System.out.print("Error changing turn");
            //System.exit(1);
        }
    }// End of changeTurn()

    /* *********************************************************************
    Function Name: writeSave
    Purpose: writes a save file to the format of readSave
    Parameters: none
    Return Value: void
    Algorithm: write all our values to sfile and save
    Assistance Received: none
    ********************************************************************* */
    // Writes a current round as a save game then exits
    /**
     * setup to work for java, not implemented yet for android
    public void writeSave()
    {
        ArrayList<Tile> temp_tiles = new ArrayList<Tile>();

        try	{
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter("A:\\School\\OPL 2021\\Mexican Train Java\\src\\" + writeSaveInput()));

            bw.write("Round: ");
            bw.write(roundNum +"\n\n");

            bw.write("Computer: \n");
            bw.write("\tScore: " + this.comp.getScore() + "\n");

            bw.write("\tHand: ");


            temp_tiles.clear();
            temp_tiles = comp.getHand();
            for (int i = 0; i < temp_tiles.size(); i++)
            {
                bw.write(temp_tiles.get(i).stringTile() + " ");
            }

            bw.write("\n");

            bw.write("\tTrain: ");

            temp_tiles.clear();
            temp_tiles = comp.getTrain();
            // For computer marker goes on the end
            if (comp.getMarker() == true) {	bw.write("M "); }
            for (int i = 0; i < temp_tiles.size(); i++)
            {
                bw.write( temp_tiles.get(i).stringTile() + " ");
            }
            bw.write("\n\n");

            bw.write("Human: \n");
            bw.write("\tScore: " + this.hum.getScore() + "\n");

            bw.write("\tHand: ");
            temp_tiles.clear();
            temp_tiles = hum.getHand();
            for (int i = 0; i < temp_tiles.size(); i++)
            {
                bw.write(temp_tiles.get(i).stringTile() + " ");
            }
            bw.write("\n");

            bw.write("\tTrain: ");
            temp_tiles.clear();
            temp_tiles = hum.getTrain();
            // For computer marker goes on the end
            for (int i = 0; i < temp_tiles.size(); i++)
            {
                bw.write( temp_tiles.get(i).stringTile() + " ");
            }
            if (hum.getMarker() == true) {	bw.write(" M"); }
            bw.write("\n\n");

            bw.write("Mexican Train: ");
            temp_tiles.clear();
            temp_tiles = this.mexican_train;
            for (int i = 0; i < temp_tiles.size(); i++)
            {
                bw.write(temp_tiles.get(i).stringTile() + " ");
            }
            bw.write("\n\n");

            bw.write("Boneyard: ");
            temp_tiles.clear();
            temp_tiles = this.boneyard;
            for (int i = 0; i < temp_tiles.size(); i++)
            {
                bw.write(temp_tiles.get(i).stringTile() + " ");
            }
            bw.write("\n\n");

            bw.write("Next Player: " + this.turn);

            bw.close();
        }catch(Exception ex) {

        }

        System.exit(0);
    }// End of writeSave()
    */

    /* *********************************************************************
    Function Name: writeSaveInput()
    Purpose: gets the users input for what to name the save file
    Parameters: none
    Return Value: string
    Algorithm: prompt user for input, validate that they entered something, then add .txt
    Assistance Received: none
    ********************************************************************* */
    /**
     * setup to work for java, not android
    public String writeSaveInput()
    {
        Scanner user = new Scanner(System.in);
        String filename = "";
        while (filename == "")
        {
            System.out.print("Enter a name for the file, afterwards the program will close: ");
            filename = user.nextLine();
        }
        filename = filename + ".txt";

        return filename;
    }// End of writeSaveInput()
    */
}