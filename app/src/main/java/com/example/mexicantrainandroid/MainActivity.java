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
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_TEXT = "com.example.mexicantrainandroid.EXTRA_TEXT";
    public static final String EXTRA_NUMBER = "com.example.mexicantrainandroid.EXTRA_NUMBER";

    private TextView txt;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Setup the welcome prompt
        String strWelcome = "Welcome to Mexican Train";
        String strQuestion = "would you like to read the rules?";

        txt = (TextView) findViewById(R.id.bigTextView);
        txt.setText(strWelcome);

        txt = (TextView) findViewById(R.id.smallTextView);
        txt.setText(strQuestion);

        btn = (Button) findViewById(R.id.leftButton);
        btn.setText("YES");

        btn = (Button) findViewById(R.id.rightButton);
        btn.setText("NO");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGame();
            }
        });
        Log.d("activity_main", "onCreate called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("activity_main", "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("activity_main", "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("activity_main", "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("activity_main", "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("activity_main", "onDestroy called");
    }

    // Will be called upon the YES button click at start of the application
    public void printRules(View view) {
        //TextView text = (TextView) findViewById(R.id.yesButton);
        setContentView(R.layout.activity_rules);
        txt = (TextView) findViewById(R.id.ruleText);

        String strRules = "Basics:\n" +
                "         Mexican Train is a domino game played by 2 players (one being you, the player, the other being the computer)\n" +
                "         in which the main objective is to score the fewest points after all the rounds\n" +
                "\n" +
                "Setup:\n" +
                "         The game uses one double-nine set. A double-nine set contains 55 tiles. Each tile has two ends, each\n" +
                "         end containing 0-9 pips. A double-nine set contains tiles with the following combination of pips:\n" +
                "\n" +
                "         0-0, 0-1, 0-2, 0-3, 0-4, 0-5, 0-6, 0-7, 0-8, 0-9,\n" +
                "         1-1, 1-2, 1-3, 1-4, 1-5, 1-6, 1-7, 1-8, 1-9,\n" +
                "         2-2, 2-3, 2-4, 2-5, 2-6, 2-7, 2-8, 2-9,\n" +
                "         3-3, 3-4, 3-5, 3-6, 3-7, 3-8, 3-9,\n" +
                "         4-4, 4-5, 4-6, 4-7, 4-8, 4-9,\n" +
                "         5-5, 5-6, 5-7, 5-8, 5-9,\n" +
                "         6-6, 6-7, 6-8, 6-9,\n" +
                "         7-7, 7-8, 7-9,\n" +
                "         8-8, 8-9,\n" +
                "         9-9\n" +
                "\n" +
                "         In the first round of the game, 9-9 is the engine or starting tile. In the next round, 8-8 is the starting\n" +
                "         tile and so on till 0-0 for the 10th round and back to 9-9 for the eleventh round.Once the engine for a\n" +
                "         round is placed on the Table, the rest of the dominoes are shuffled before being dealt to the players.\n\n" +
                "Table:\n" +
                "         The engine for the round will be top to bottom with the players train on the right and the computers\n" +
                "         on the left. e.g., in the following snapshot of the first round when 9-9 is the engine, the human\n" +
                "         has played 9-5 and the computer has played 3-9:\n" +
                "\n" +
                "                 9\n" +
                "            3-9 | 9-5\n" +
                "                 9\n" +
                "\n" +
                "        The Boneyard which is the remainder of shuffled tiles after having been dealt, will be left on the table.\n" +
                "\n" +
                "A Round:\n" +
                "         A round starts in the following order:\n" +
                "\n" +
                "         First: the engine is placed on the Table.\n" +
                "         Second: the remaining tiles are shuffled.\n" +
                "         Third: the human player is dealt 16 tiles, then the computer player is dealt 16 tiles.\n" +
                "         Fourth: the remaining tiles are placed into the Boneyard.\n" +
                "\n" +
                "         The first player is determined by who has the lowest score, if scores are even it is a coin flip.\n" +
                "         Thereafter the players alternate turns until either of the players plays their last tile, or the game is\n" +
                "         blocked because the bone yard is empty and both players pass their turns.\n\n" +
                "A Turn:\n" +
                "         A player plays at the end of an elligible train following the following logic:\n" +
                "\n" +
                "         If either player has left an orphan double tile at the end of a train, it is the only eligible train until\n" +
                "         the double tile has been played against by either player. Otherwise elligible trains are:\n" +
                "                 Players personal train: to the right of the engine for the human\n" +
                "                 Mexican train: once it has been started. Either player can start the Mexican train with a tile from\n" +
                "                         their hand that matches the engine. It is advantageous to start the Mexican train\n" +
                "                         as early as possible to increase the number of eligible trains for all subsequent turns\n" +
                "                         Once the Mexican train has been started, tiles can only be added to its tail end, i.e.,\n" +
                "                         the side of the starting tile that does not match the engine.\n" +
                "                 Opponent's personal train if it has a marker at its end.\n" +
                "\n" +
                "         A player can play a tile from his/her hand by matching the number of pips at one end of the tile with the\n" +
                "                number of pips at the open end of an eligible train. A player plays as follows:\n" +
                "\n" +
                "                 No playable tiles: If the player does not have a tile that can be played at the end of a train:\n" +
                "                         If the boneyard is empty, the player passes their turn\n" +
                "                         and puts a marker at the end of their personal train.\n" +
                "                         If the boneyard is not empty, the player draws a\n" +
                "                         tile from the boneyard and plays it immediately.\n" +
                "                                 If the player cannot play the drawn tile, the player\n" +
                "                                 must add it to their hand, pass their turnand put a\n" +
                "                                 marker at the end of their personal train.\n" +
                "\n" +
                "                 Once the player's personal train has a marker at the end:\n" +
                "                         The opponent can play on the player's personal train\n" +
                "                         as long as the marker is present. The marker is removed\n" +
                "                         only when the player plays a tile at the end of their\n" +
                "                         own personal train. On subsequent turns, the player\n" +
                "                         can play on the Mexican train or opponent's personal\n" +
                "                         train with a marker even if there is a marker on their\n" +
                "                         own personal train.\n" +
                "\n" +
                "                 If the player has a tile that they can play, they must play it as follows:\n" +
                "                         A non-double tile at the end of one of the eligible trains.\n" +
                "                         A double tile at the end of an eligible train, followed by\n" +
                "                         a non-double follow-up tile in the same turn:\n" +
                "                                 If the player's hand is exhausted, i.e., the player has no more\n" +
                "                                 tiles in the player's hand after playing the double, the game ends.\n" +
                "                                 If the player's hand is not exhausted, but the player does not\n" +
                "                                 have a non-double tile that can be played as a follow-up tile,\n" +
                "                                 he player follows the procedure above for No playable tiles.\n" +
                "                                 If the player has a tile that can be played as a follow-up tile,\n" +
                "                                  the player can play it as follows:\n" +
                "                                         Next to the double the player just played.\n" +
                "                                         At the end of any other eligible train. If so,\n" +
                "                                         the double the player just played becomes an orphan\n" +
                "                                         double. Once an orphan double is created, no trains\n" +
                "                                         are eligible for either player until the orphan\n" +
                "                                         double is played against by either player.\n" +
                "                         Two double tiles at the end of two eligible trains if (and only if):\n" +
                "                                 The player will exhaust the player's hand after playing\n" +
                "                                 the two doubles, ending the game, or\n" +
                "                                 The player can play an additional third non-double tile from the player's hand (without drawing from the boneyard). This could lead to one or both the played double tiles becoming orphan doubles. But, the player will have played three\n" +
                "                                 tiles in one turn.\n" +
                "\n" +
                "Score:\n" +
                "         When the game ends, each player gets round points equal to the sum of pips on all the tiles left in their hand.\n" +
                "         For the player who empties their hand, this sum is 0. The round points of each player are added to the game\n" +
                "         score of the player. Once the last round has ended, the winner of the game is the player with the lowest game " +
                "         score. If both the players have the same game score, the game is a draw.\n";

        txt.setText(strRules);
        txt.setMovementMethod(new ScrollingMovementMethod());

        btn = (Button) findViewById(R.id.rulesContinue);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGame();
            }
        });
    }// End of printRules()


    //public void loadGameSelection(View view) {
    //    Game userGame = new Game();
    //}// End of loadGameSelection()

    // Opens a new game activity from this activity
    public void openGame()
    {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }
}