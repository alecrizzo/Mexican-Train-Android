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
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class CoinActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "com.example.mexicantrainandroid.EXTRA_TEXT";
    final static public String EXTRA_GAMEMODE = "GAMEMODE";

    private ImageView coin;
    private Button headsBtn, tailsBtn, continueBtn;
    private TextView textBox;
    private String turn = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        textBox = (TextView) findViewById(R.id.coinTextView);
        textBox.setText("Select a value for the coin flip, heads or tails");
        textBox.setTextSize(20);

        // Bind the coin imageview to the value in the xml file
        coin = (ImageView) findViewById(R.id.coinImage);

        // The heads and tail buttons will flip the coin and determine turn
        headsBtn = (Button) findViewById(R.id.coinButtonHeads);
        headsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCoin(false);
                headsBtn.setVisibility(View.INVISIBLE);
                tailsBtn.setVisibility(View.INVISIBLE);
            }
        });
        tailsBtn = (Button) findViewById(R.id.coinButtonTails);
        tailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCoin(true);
                headsBtn.setVisibility(View.INVISIBLE);
                tailsBtn.setVisibility(View.INVISIBLE);
            }
        });

        // Make the continue button invisible until after the coin flip
        continueBtn = (Button) findViewById(R.id.coinContinueButton);
        continueBtn.setVisibility(View.INVISIBLE);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call new round activity with the turn value from the coinflip
                openRoundActivity();
            }
        });

        Log.d("activity_coin", "onCreate called");
    }

    private void flipCoin(boolean coinValue)
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

        Animation fade = new AlphaAnimation(1, 0);
        fade.setInterpolator(new AccelerateInterpolator());
        fade.setDuration(1000);
        fade.setFillAfter(true);
        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {            }
            @Override
            public void onAnimationEnd(Animation animation) {

                if(face == true)
                {
                    coin.setImageResource(R.drawable.tails);
                    if(coinValue == true)
                    {
                        textBox.setText("Coin is tails. You go first.");
                        turn = "Human";
                    }
                    else
                    {
                        textBox.setText("Coin is tails. Computer goes first.");
                        turn = "Computer";
                    }
                }
                else
                {
                    coin.setImageResource(R.drawable.heads);
                    if(coinValue == false)
                    {
                        textBox.setText("Coin is heads. You go first.");
                        turn = "Human";

                    }
                    else
                    {
                        textBox.setText("Coin is heads. Computer goes first.");
                        turn = "Computer";
                    }
                }

                Animation fadeIn = new AlphaAnimation(0,1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(2000);
                fadeIn.setFillAfter(true);
                coin.startAnimation(fadeIn);
                textBox.startAnimation(fadeIn);
                continueBtn.startAnimation(fadeIn);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {            }

        });

        coin.startAnimation(fade);
    }

    // Use intent to open the Round Activity with the turn value we obtained
    public void openRoundActivity()
    {
        Intent intent = new Intent(this, RoundActivity.class);
        intent.putExtra(EXTRA_TEXT, turn);
        intent.putExtra("FROM_ACTIVITY", "CoinActivity");
        startActivity(intent);
    }

}