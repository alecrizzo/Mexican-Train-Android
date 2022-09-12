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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

// !ALEC
// Currently this Activity is not launching from startActivity(intent)
// Do not understand why

public class HumanTurnActivity extends AppCompatActivity {

    //TextView humanScoreText, computerScoreText, txt;
    private Human human = new Human();
    private Computer computer = new Computer();
    protected ArrayList<Tile> boneyard = new ArrayList<Tile>();
    private ArrayList<Tile> mexican_train = new ArrayList<Tile>();
    private Tile engine = new Tile();
    private boolean gameEnd;
    private boolean mexOrphan;
    private TextView console = (TextView) findViewById(R.id.turnConsoleText);
    private Button continueBtn = (Button) findViewById(R.id.turnContinueButton);
    private int prevTrain = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_turn);

        // Receive gameplay data from RoundActivity to let the user select a train to play to
        // as well as which tile
        Intent intent = getIntent();
        if(intent.getStringExtra("FROM_ACTIVITY") != null)
        {
            String prevActivity = intent.getStringExtra("FROM_ACTIVITY");
            if(getIntent() != null && getIntent().getExtras() != null)
            {
                if(prevActivity.equals("RoundActivity"))
                {
                    this.human = (Human) getIntent().getSerializableExtra(RoundActivity.EXTRA_HUMAN_KEY);
                    this.computer = (Computer) getIntent().getSerializableExtra(RoundActivity.EXTRA_COMPUTER_KEY);
                    this.boneyard = (ArrayList<Tile>) getIntent().getSerializableExtra(RoundActivity.EXTRA_BONEYARD_KEY);
                    this.mexican_train = (ArrayList<Tile>) getIntent().getSerializableExtra(RoundActivity.EXTRA_MEX_TRAIN_KEY);
                    this.mexOrphan = getIntent().getBooleanExtra(RoundActivity.EXTRA_MEX_TRAIN_ORPHAN_KEY, false);
                    this.gameEnd = getIntent().getBooleanExtra(RoundActivity.EXTRA_GAME_END_KEY, false);
                    this.engine = (Tile) getIntent().getSerializableExtra(RoundActivity.EXTRA_ENGINE_KEY);

                    updateDisplay();

                    Button btn = (Button) findViewById(R.id.turnButton1);
                    btn.setText("MEXICAN");
                    btn.setVisibility(View.INVISIBLE);
                    btn = (Button) findViewById(R.id.turnButton2);
                    btn.setText("HUMAN");
                    btn.setVisibility(View.INVISIBLE);
                    btn = (Button) findViewById(R.id.turnButton3);
                    btn.setText("COMPUTER");
                    btn.setVisibility(View.INVISIBLE);


                    TextView txt = (TextView) findViewById(R.id.turnMexicanTrainText);
                    txt.setText("Mexican Train: ");
                    txt.setTextSize(15);

                    txt = (TextView)  findViewById(R.id.turnHumanHandText);
                    txt.setText("Hand: ");
                    txt.setTextSize(15);

                    continueBtn.setVisibility(View.INVISIBLE);
                    console.setMovementMethod(new ScrollingMovementMethod());

                    //playTurn();

                }
            }
        }

        Log.d("activity_human_turn", "onCreate called");
    }

    private void updateDisplay()
    {
        printEngine();
        printBoneyard();
        printComputerScore();
        printHumanHand();
        printComputerTrain();
        printHumanScore();
        printHumanTrain();
        printMarkers();
        printOrphan();
        printMexicanTrain();
    }

    // Determines if computer train is playable with humans hand and is marked
    private boolean isComputerTrainPlayable()
    {
        if(computer.getMarker() == false)
        {
            return false;
        }
        else if(computer.getMarker() == true
                && human.isHandElligibleComputerTrain(computer.getTrain()) == true)
        {
            return true;
        }
        return false;
    }

    // Determines if mexican train is playable with humans hand
    private boolean isMexicanTrainPlayable()
    {
        if(human.isHandElligibleTrain(this.mexican_train) == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Determines if humans train is playable with human hand
    private boolean isHumanTrainPlayable()
    {
        if(human.isHandElligibleTrain(human.getTrain()) == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Checks if there exists an orphan double
    // Returns "Human", "Computer" or "Mexican" or "None" to show where orphan is
    private String checkOrphans()
    {
        if(human.getOrphan() == true)
        {
            return "Human";
        }
        else if(computer.getOrphan() == true)
        {
            return "Computer";
        }
        else if(mexOrphan == true)
        {
            return "Mexican";
        }
        else
        {
            return "None";
        }
    }

    // !ALEC
    private void playTurn()
    {
        String orphanTrain = checkOrphans();

        // No playable tiles: the player does not have a tile that can be played at the end of any eligible train
        if(isHumanTrainPlayable() == false && isComputerTrainPlayable() == false
                && isMexicanTrainPlayable() == false)
        {
            noPlayableTiles();
        }else if(isHumanTrainPlayable() == true
                || isComputerTrainPlayable() == true
                || isMexicanTrainPlayable() == true)
        {
            if (orphanTrain == "Human")
            {
                console.append("There is an Orphan Double on your train, you can only play to that train \n");
                if(isHumanTrainPlayable() == false)
                {
                    console.append("No playable tiles to the orphan double\n");
                    noPlayableTiles();
                }
                else
                {
                    // let user play to orphan double
                    Button btn = (Button) findViewById(R.id.roundButton2);
                    btn.setText("HUMAN");
                    btn.setVisibility(View.VISIBLE);
                    ArrayList<Tile> playable = human.playableTilesToTrain(human.getTrain());
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // let user select one of playable tiles from hand
                            pickTile(playable, human.getTrain(), 2, false);
                        }
                    });
                }
            }
            else if (orphanTrain == "Computer")
            {
                // let user play to orphan double
                console.append("There is an Orphan Double on computer's train, you can only play to that train \n");
                if(isComputerTrainPlayable() == false)
                {
                    console.append("No playable tiles to the orphan double\n");
                    noPlayableTiles();
                }
                else
                {
                    // let user play to orphan double
                    Button btn = (Button) findViewById(R.id.roundButton3);
                    btn.setText("COMPUTER");
                    btn.setVisibility(View.VISIBLE);
                    ArrayList<Tile> playable = human.playableTilesToComputerTrain(computer.getTrain());
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // let user select one of playable tiles from hand
                            pickTile(playable, computer.getTrain(), 3, false);
                        }
                    });
                }
            }
            else if (orphanTrain == "Mexican")
            {
                // let user play to orphan double
                console.append("There is an Orphan Double on computer's train, you can only play to that train \n");
                if(isMexicanTrainPlayable() == false)
                {
                    console.append("No playable tiles to the orphan double\n");
                    noPlayableTiles();
                }
                else
                {
                    // let user play to orphan double
                    Button btn = (Button) findViewById(R.id.roundButton1);
                    btn.setText("MEXICAN");
                    btn.setVisibility(View.VISIBLE);
                    ArrayList<Tile> playable = human.playableTilesToTrain(mexican_train);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // let user select one of playable tiles from hand
                            pickTile(playable, computer.getTrain(), 3, false);
                        }
                    });
                }

            }
            else if (orphanTrain == "None")
            {
                if(isMexicanTrainPlayable() == true)
                {
                    Button btn = (Button) findViewById(R.id.roundButton1);
                    btn.setText("MEXICAN");
                    btn.setVisibility(View.VISIBLE);
                    ArrayList<Tile> playable = human.playableTilesToTrain(mexican_train);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // let user select one of playable tiles from hand
                            pickTile(playable, computer.getTrain(), 3, false);
                        }
                    });
                }
                if(isHumanTrainPlayable() == true)
                {
                    Button btn = (Button) findViewById(R.id.roundButton2);
                    btn.setText("HUMAN");
                    btn.setVisibility(View.VISIBLE);
                    ArrayList<Tile> playable = human.playableTilesToTrain(human.getTrain());
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // let user select one of playable tiles from hand
                            pickTile(playable, human.getTrain(), 2, false);
                        }
                    });
                }
                if(isComputerTrainPlayable() == true)
                {
                    Button btn = (Button) findViewById(R.id.roundButton3);
                    btn.setText("COMPUTER");
                    btn.setVisibility(View.VISIBLE);
                    ArrayList<Tile> playable = human.playableTilesToComputerTrain(computer.getTrain());
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // let user select one of playable tiles from hand
                            pickTile(playable, computer.getTrain(), 3, false);
                        }
                    });
                }
            }
        }
    }

    // Lets user select tiles from the hand that are playable to the passed_train
    private void pickTile(ArrayList<Tile> playable_tiles, ArrayList<Tile> passed_train,
                          int trainChoice, boolean playedDouble)
    {
        if(passed_train.size() > 0)
        {
            // Learned how to do this from https://www.youtube.com/watch?v=hl0AcuplFwE
            LinearLayout handLayout = findViewById(R.id.turnHumanHandLayout);
            LayoutInflater inflater = LayoutInflater.from(this);
            for(int i = 0; i < playable_tiles.size(); i++ )
            {
                View view = inflater.inflate(R.layout.tile, handLayout, false);
                ImageView tileImage = view.findViewById(R.id.tileImageView);
                setTileImage(tileImage, playable_tiles.get(i));
                tileImage.setClickable(true);
                int finalI = i;
                tileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playTile(playable_tiles.get(finalI), trainChoice, playedDouble);
                    }
                });
                handLayout.addView(view);
            }
            console.append("Select a tile from hand you would like to play to the train");
        }
        return;
    }

    private void playTile(Tile tile, int trainChoice, boolean playedDouble)
    {
        ArrayList<Tile> played_train = new ArrayList<Tile>();

        // Set the temp train played_train equal to the train the user is playing to
        switch(trainChoice)
        {
            case 1:
                played_train = mexican_train;
                break;
            case 2:
                played_train = human.getTrain();
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
            human.removeTileHand(tile);

            console.append("Played non double tile\n");

            // Assign the values that have been changed on our temp played_train back to the original train
            switch (trainChoice)
            {
                case 1:
                    mexican_train = played_train;
                    break;
                case 2:
                    human.setTrain(played_train);
                    human.setMarker(false);
                    break;
                case 3:
                    computer.setTrain(played_train);
                    Collections.reverse(played_train);
                    // !ALEC this doesn't seem right ^
                    break;
            }

            // return from the recursive non double follow up case
            if(playedDouble == true)
            {
                return;
            }
        }
        if (tile.isDouble() == true)
        {
            // Push the double tile onto the train and remove it from the hand
            played_train.add(tile);
            human.removeTileHand(tile);

            console.append("Played double tile\n");

            playedDouble = true;

            // Assign the values that have been changed on our temp played_train back to the original train
            switch (trainChoice)
            {
                case 1:
                    mexican_train = played_train;
                    break;
                case 2:
                    human.setTrain(played_train);
                    human.setMarker(false);
                    break;
                case 3:
                    computer.setTrain(played_train);
                    Collections.reverse(played_train);
                    break;
            }
        }
        if (tile.isDouble() == true && human.getHand().size() == 1 && human.twoPlayableDoubles(mexican_train, computer.train) == true)
        {
            // Continues the output from if above for this case to print 2 tiles
            console.append("Also played the last tile, Game Over\n");

            // Play the 2nd playable double and end the game
            if (human.isTilePlayable(human.getHand().get(0), mexican_train) == true)
            {
                human.addTileTrain(human.getHand().get(0), mexican_train);
                human.removeTileHand(human.getHand().get(0));
            }
            else if (human.isTilePlayable(human.getHand().get(0), human.getTrain()) == true)
            {
                human.addTileTrain(human.getHand().get(0), human.getTrain());
                human.removeTileHand(human.getHand().get(0));
                // always set marker to false after playing to human train
                human.setMarker(false);
            }
            else if (human.isTilePlayableToComputer(human.getHand().get(0), computer.train) == true)
            {
                human.addTileComputerTrain(human.getHand().get(0), computer);
                human.removeTileHand(human.getHand().get(0));
            }
            gameEnd = true;
        }else if (tile.isDouble() == true && human.getHand().size() == 0)
        {
            // if played double and hand is empty end game
            gameEnd = true;
            console.append("Played double as last tile, Game Over\n");
        }
        else if (tile.isDouble() == true && ((human.isHandElligibleComputerTrain(computer.getTrain())
                == true && computer.getMarker() == true)
                || human.isHandElligibleTrain(mexican_train) == true
                || human.isHandElligibleTrain(human.getTrain()) == true) && playedDouble == false)
        {
            // If any a double is played and the hand is eligible to any train
            // activate the buttons to let user pick a train
            console.append("You have a non double follow up, select a train to play it to\nIf it's a different train to the double it will create an orphan double\n");
            if(isMexicanTrainPlayable() == true)
            {
                // let user play to train
                Button btn = (Button) findViewById(R.id.roundButton1);
                btn.setVisibility(View.VISIBLE);
                btn.setText("MEXICAN");
                ArrayList<Tile> playable = human.playableTilesToTrain(mexican_train);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // let user select one of playable tiles from hand
                        pickTile(playable, human.getTrain(), 1, true);
                    }
                });
            }
            if(isHumanTrainPlayable() == true)
            {
                // let user play to train
                Button btn = (Button) findViewById(R.id.roundButton2);
                btn.setVisibility(View.VISIBLE);
                btn.setText("HUMAN");
                ArrayList<Tile> playable = human.playableTilesToTrain(human.getTrain());
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // let user select one of playable tiles from hand
                        pickTile(playable, human.getTrain(), 2, true);
                    }
                });
            }
            if (isComputerTrainPlayable() == true)
            {
                // let user play to train
                Button btn = (Button) findViewById(R.id.roundButton3);
                btn.setText("COMPUTER");
                ArrayList<Tile> playable = human.playableTilesToComputerTrain(computer.getTrain());
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // let user select one of playable tiles from hand
                        pickTile(playable, human.getTrain(), 3, true);
                    }
                });
            }

            if(prevTrain != trainChoice && prevTrain != 0)
            {
                switch(prevTrain)
                {
                    case 1:
                        mexOrphan = true;
                        break;
                    case 2:
                        human.setOrphan(true);
                        break;
                    case 3:
                        computer.setOrphan(true);
                        break;
                    default:
                        break;
                }
            }
        }
        else if (playedDouble == false && tile.isDouble() == true
                && (human.isHandElligibleComputerTrain(computer.train) == false
                && human.isHandElligibleTrain(mexican_train) == false
                        && human.isHandElligibleTrain(human.getTrain()) == false))
        {
            // Use playedDouble to avoid drawing after a case where the double tile is played and reevaluates the hands after being
            // removed causing to draw when user is not supposed to since they did sucessfully play a tile
            // Follow procedure for no tiles
            noPlayableTiles();
        }

    }

    private void noPlayableTiles()
    {
        if(boneyard.size() == 0)
        {
            human.setMarker(true);
            return;
        }
        else
        {
            boolean mexPlayable = false;
            boolean computerPlayable = false;
            boolean humanPlayable = false;

            // If the boneyard is not empty, the player draws a tile from the boneyard and plays it immediately.
            human.addTileHand(boneyard.get(0));
            boneyard.remove(0);

            if(human.isTilePlayableToComputer(human.getHand().get(human.getHand().size()-1),
                    computer.getTrain()) == true)
            {
                computerPlayable = true;
            }
            if(human.isTilePlayable(human.getHand().get(human.getHand().size()-1),
                    mexican_train) == true)
            {
                mexPlayable = true;
            }
            if(human.isTilePlayable(human.getHand().get(human.getHand().size()-1),
                    human.getTrain()) == true)
            {
                humanPlayable = true;
            }

            // If orphan double, check only that condition as it takes full priority to the players available actions
            if (computer.getOrphan() == true)
            {
                if (computerPlayable == true)
                {
                    // Flip tile/ Check if need to flip
                    human.flipTileForComputerTrain(human.getHand().get(human.getHand().size()-1), computer.getTrain());
                    // Play tile immediately to computers train
                    human.addTileComputerTrain(human.getHand().get(human.getHand().size()-1), computer);
                    // Let player know what happened
                    console.append("Played tile from boneyard to the orphan double\n");
                    // Remove tile from hand
                    human.hand.remove(human.hand.size()-1);
                    // Change status of computers orphan double
                    computer.setOrphan(false);
                }
                else
                {
                    // Player must add card to their hand pass turn and mark their train
                    // note: card already added to hand above
                    human.setMarker(true);
                    console.append("Could not play tile from the boneyard. It is now in your hand.\n");
                }
                return;
            }
            // Check this trains orphan double case
            if (human.getOrphan() == true)
            {
                if (humanPlayable == true)
                {
                    // Flip tile for this train
                    human.flipTileForTrain(human.getHand().get(human.getHand().size()-1), human.getTrain());
                    // Play tile immediately to this train
                    human.addTileTrain(human.getHand().get(human.getHand().size()-1), human.getTrain());
                    // Let player know what happened
                    console.append("Played tile from the boneyard to the orphan double\n");
                    // Remove tile from hand
                    human.hand.remove(human.hand.size()-1);
                    human.setOrphan(false);

                    // When playing to personal train always set marker to false as playing to personal train removes markers
                    human.setMarker(false);
                }
                else
                {
                    // Player must add card to their hand pass turn and mark their train
                    // note: card already added to hand above
                    human.setMarker(true);
                    console.append("Could not play tile from the boneyard. It is now in your hand.\n");
                } return;
            }
            if (mexOrphan == true)
            {
                if (mexPlayable == true)
                {
                    // Flip tile for mexican train
                    human.flipTileForTrain(human.getHand().get(human.getHand().size()-1), mexican_train);
                    // Play tile immediately to this train
                    human.addTileTrain(human.getHand().get(human.getHand().size()-1), mexican_train);
                    // Let player know what happened
                    console.append("Played tile from boneyard to the orphan double\n");
                    // Remove tile from hand
                    human.hand.remove(human.hand.size()-1);
                    // Set the played trains orphan to false
                    mexOrphan = false;
                }
                else
                {
                    // Player must add card to their hand pass turn and mark their train
                    // note: card already added to hand above
                    human.setMarker(true);
                    console.append("Could not play tile from the boneyard. It is now in your hand.\n");
                }
                return;
            }
            else if (computerPlayable == true || humanPlayable == true || mexPlayable == true)
            {
                // let user pick train to play it onto via select train method
                console.append("Select a train to play on\n");
                // If a train is elligible ask the user the card they would like to play and onto which train
                if (human.isTilePlayable(human.getHand().get(human.getHand().size()-1), mexican_train) == true)
                {
                    //System.out.print(" (1) Mexican Train");
                    Button btn = (Button) findViewById(R.id.turnButton1);
                    btn.setVisibility(View.VISIBLE);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Flip tile for mexican train
                            human.flipTileForTrain(human.getHand().get(human.getHand().size()-1), mexican_train);
                            // Play tile immediately to this train
                            human.addTileTrain(human.getHand().get(human.getHand().size()-1), mexican_train);
                            // Let player know what happened
                            console.append("Played tile from the boneyard to the mexican train\n");
                            // Remove tile from hand
                            human.hand.remove(human.hand.size()-1);
                            // Set the played trains orphan to false
                            mexOrphan = false;
                        }
                    });
                }
                if (human.isTilePlayable(human.getHand().get(human.getHand().size()-1), human.getTrain()) == true)
                {
                    //System.out.print(" (2) Your Train");
                    Button btn = (Button) findViewById(R.id.turnButton2);
                    btn.setVisibility(View.VISIBLE);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Flip tile for this train
                            human.flipTileForTrain(human.getHand().get(human.getHand().size()-1), human.getTrain());
                            // Play tile immediately to this train
                            human.addTileTrain(human.getHand().get(human.getHand().size()-1), human.getTrain());
                            // Let player know what happened
                            console.append("Played tile from the boneyard to your train\n");
                            // Remove tile from hand
                            human.hand.remove(human.hand.size()-1);
                            human.setOrphan(false);
                            // When playing to personal train always set marker to false as playing to personal train removes markers
                            human.setMarker(false);
                        }
                    });
                }
                if (human.isTilePlayableToComputer(human.getHand().get(human.getHand().size()-1), computer.getTrain()) == true && computer.getMarker() == true)
                {
                    //System.out.print(" (3) Computers Train");
                    Button btn = (Button) findViewById(R.id.turnButton3);
                    btn.setVisibility(View.VISIBLE);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Flip tile/ Check if need to flip
                            human.flipTileForComputerTrain(human.getHand().get(human.getHand().size()-1), computer.getTrain());
                            // Play tile immediately to computers train
                            human.addTileComputerTrain(human.getHand().get(human.getHand().size()-1), computer);
                            // Let player know what happened
                            console.append("Played tile from the boneyard to the computer's train\n");
                            // Remove tile from hand
                            human.hand.remove(human.hand.size()-1);
                            // Change status of computers orphan double
                            computer.setOrphan(false);
                        }
                    });
                }

            }
            else
            {
                console.append("Could not play tile from the boneyard\nIt is now in your hand\n");
                human.setMarker(true);
            }
        }
    }

    private void printMarkers() {
        if(human.getMarker() == true)
        {
            TextView txt = (TextView) findViewById(R.id.turnHumanMarkerText);
            txt.setText("Human Marked!");
        }
        if(computer.getMarker() == true)
        {
            TextView txt = (TextView) findViewById(R.id.turnComputerMarkerText);
            txt.setText("Computer Marked!");
        }
    }

    private void printBoneyard()
    {
        ImageView boneyardImage = (ImageView) findViewById(R.id.turnImageBoneyard);
        setTileImage(boneyardImage, boneyard.get(0));
    }

    private void printOrphan()
    {
        if(human.getOrphan() == true)
        {
            TextView txt = (TextView) findViewById(R.id.turnHumanOrphanText);
            txt.setTextSize(15);
            txt.setText("ORPHAN DOUBLE");
        }
        if(computer.getOrphan() == true)
        {
            TextView txt = (TextView) findViewById(R.id.turnComputerOrphanText);
            txt.setTextSize(15);
            txt.setText("ORPHAN DOUBLE");
        }
        if(mexOrphan == true)
        {
            TextView txt = (TextView) findViewById(R.id.turnMexicanTrainText);
            txt.setTextSize(15);
            txt.setText("Mexican Train: \nORPHAN DOUBLE");
        }
    }

    // Displays the values tiles of the human hand to the activity in a horizontal layout
    private void printHumanHand()
    {
        // Learned how to do this from https://www.youtube.com/watch?v=hl0AcuplFwE
        LinearLayout humanHandLayout = findViewById(R.id.turnHumanHandLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        for(int i = 0; i < human.getHand().size(); i++ )
        {
            View view = inflater.inflate(R.layout.tile, humanHandLayout, false);
            ImageView tileImage = view.findViewById(R.id.tileImageView);
            // Set image to whichever matches our required tile
            //tileImage.setImageResource(R.drawable.d0_0);
            setTileImage(tileImage, human.getHand().get(i));
            humanHandLayout.addView(view);
        }
    }

    // Displays the values tiles of the human train to the activity in a horizontal layout
    private void printHumanTrain()
    {
        // Learned how to do this from https://www.youtube.com/watch?v=hl0AcuplFwE
        LinearLayout humanTrainLayout = findViewById(R.id.turnHumanTrainLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        for(int i = 0; i < human.getTrain().size(); i++ )
        {
            if(human.getTrain().get(i).getSideA() == engine.getSideA() &&
                    human.getTrain().get(i).getSideB() == engine.getSideB())
            {
                continue;
            }else
            {
                View view = inflater.inflate(R.layout.tile, humanTrainLayout, false);
                ImageView tileImage = view.findViewById(R.id.tileImageView);
                // Set image to whichever matches our required tile
                //tileImage.setImageResource(R.drawable.d0_0);
                setTileImage(tileImage, human.getTrain().get(i));
                humanTrainLayout.addView(view);
            }
        }
    }

    // Displays the values tiles of the human train to the activity in a horizontal layout
    private void printMexicanTrain()
    {
        // Learned how to do this from https://www.youtube.com/watch?v=hl0AcuplFwE
        LinearLayout mexicanTrainLayout = findViewById(R.id.turnMexicanTrainLayout);
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
        LinearLayout computerTrainLayout = findViewById(R.id.turnComputerTrainLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        for(int i = 0; i < computer.getTrain().size(); i++ )
        {
            if(computer.getTrain().get(i).getSideA() == engine.getSideA() &&
                    computer.getTrain().get(i).getSideB() == engine.getSideB())
            {
                continue;
            }else
            {
                View view = inflater.inflate(R.layout.tile, computerTrainLayout, false);
                ImageView tileImage = view.findViewById(R.id.tileImageView);
                // Set image to whichever matches our required tile
                //tileImage.setImageResource(R.drawable.d0_0);
                setTileImage(tileImage, computer.getTrain().get(i));
                computerTrainLayout.addView(view);
            }
        }

        // Weighs the scrollview to the right so that it displays values closest to the train
        // before the user scrolls at all: https://stackoverflow.com/questions/28804932/how-to-make-horizontalscrollview-right-to-left-scroll-android/28805094
        HorizontalScrollView computerTrainScrollView = findViewById(R.id.turnComputerTrainScrollView);
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

    // Print human score text to display to turnHumanScoreText in bottom right of activity
    private void printHumanScore()
    {
        TextView humanScoreText = (TextView) findViewById(R.id.turnHumanScoreText);
        humanScoreText.setText("Human: " + human.getScore());
    }

    // Set computer score text to display to turnComputerScoreText in the bottom left of activity
    private void printComputerScore()
    {
        TextView computerScoreText = (TextView) findViewById(R.id.turnComputerScoreText);
        computerScoreText.setText("Computer: " + computer.getScore());
    }

    // Function to set the engine image based on the sideA int of the current engine
    private void printEngine()
    {
        ImageView engineImage = (ImageView) findViewById(R.id.turnImageEngine);
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

}