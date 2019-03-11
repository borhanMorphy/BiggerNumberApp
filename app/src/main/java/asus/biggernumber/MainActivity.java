package asus.biggernumber;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Timer timer;

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    endGame(true);
                }
            });
        }
    };

    // game variables
    private static int
            CONSECUTIVE_RIGHT_ANSWERS = 0,
            LEVEL = 1,
            SCORE = 0,
            BEST_SCORE = 0
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getBestScore();
        setLayoutsByLevel();
    }

    private void setLayoutsByLevel(){
        // set score board layout
        handleScoreboard();

        // set selection buttons layout
        int buttonCount, maxValue, minValue;
        switch (LEVEL) {
            case 1: buttonCount=2; minValue=0; maxValue=10; break;
            case 2: buttonCount=3; minValue=0; maxValue=20; break;
            case 3: buttonCount=3; minValue=0; maxValue=30; break;
            case 4: buttonCount=4; minValue=0; maxValue=40; break;
            case 5: buttonCount=4; minValue=-40; maxValue=40; break;
            case 6: buttonCount=4; minValue=-60; maxValue=60; break;
            case 7: buttonCount=4; minValue=-100; maxValue=100; break;
            case 8: buttonCount=5; minValue=-100; maxValue=100; break;
            case 9: buttonCount=6; minValue=-100; maxValue=100; break;
            case 10: buttonCount=8; minValue=-100; maxValue=100; break;
            default: buttonCount=8; minValue=-100; maxValue=100; break; // assuming level 10
        }
        handleButtons(buttonCount, maxValue, minValue);

        handleTimer();
    }

    private void handleButtons(int buttonCount, int maxValue, int minValue){
        LinearLayout selectionButtonsLayout = (LinearLayout)findViewById(R.id.selectionButtonsCenterLayout);
        selectionButtonsLayout.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        for ( Integer value : getDistinctIntegerList(buttonCount, maxValue, minValue) ){
            Button btn = new Button(this);
            btn.setId(value);
            btn.setText(""+value);
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    gameEngine( checkIfBigger(view) );
                }
            });
            selectionButtonsLayout.addView(btn, lp);
        }
    }

    private void gameEngine(boolean success){
        if(success){ // gave right answer
            CONSECUTIVE_RIGHT_ANSWERS++;
            if(CONSECUTIVE_RIGHT_ANSWERS % 5 == 0){ // level up
                CONSECUTIVE_RIGHT_ANSWERS = 0;
                updateStats(true);
            }else {
                updateStats(false);
            }
        }else{ // gave wrong answer
            endGame(false);
        }
    }

    private void updateStats(boolean levelup){
        if(levelup){
            LEVEL ++;
            SCORE +=5;
        }
        SCORE +=5;
        BEST_SCORE = ( (SCORE > BEST_SCORE) ? SCORE : BEST_SCORE );
        setLayoutsByLevel();
    }

    private void endGame(boolean timeout){
        saveBestScore();
        setContentView(R.layout.endgame_activity);
        TextView endgameLevelView = (TextView)findViewById(R.id.endgameLevelText);
        TextView endgameScoreView = (TextView)findViewById(R.id.endgameScoreText);
        TextView endgameBestScoreView = (TextView)findViewById(R.id.endgameBestScoreText);

        endgameLevelView.setText("level "+LEVEL);
        endgameScoreView.setText("score: "+SCORE);
        endgameBestScoreView.setText("best score: "+BEST_SCORE);
    }

    private ArrayList<Integer> getDistinctIntegerList(int count, int maxValue, int minValue){
        Random rand = new Random();
        ArrayList<Integer> selectedValues = new ArrayList<>(count);
        for(int i = 0 ; i < count ;){
            int value = rand.nextInt(maxValue - minValue + 1) + minValue;
            if(!selectedValues.contains(value)){
                selectedValues.add(value);
                i++;
            }
        }
        return selectedValues;
    }

    private boolean checkIfBigger(View view){
        LinearLayout selectionButtonsLayout = (LinearLayout)findViewById(R.id.selectionButtonsCenterLayout);
        int count = selectionButtonsLayout.getChildCount();
        int clicked = view.getId();
        int max = clicked;
        for (int i = 0; i < count; i++) {
            if( selectionButtonsLayout.getChildAt(i).getId() > max ){
                max = selectionButtonsLayout.getChildAt(i).getId();
            }
        }
        return ((max == clicked) ? true : false);
    }

    private void handleScoreboard(){
        TextView currentLevelView = (TextView)findViewById(R.id.currentLevelText);
        TextView currentScoreView = (TextView)findViewById(R.id.currentScoreText);
        TextView bestScoreView = (TextView)findViewById(R.id.bestScoreText);

        currentLevelView.setText("level "+LEVEL);
        currentScoreView.setText("score: "+SCORE);
        bestScoreView.setText("best score: "+BEST_SCORE);
    }

    private void handleTimer(){
//        timer = new Timer();
//        timer.schedule(timerTask,1000,10000);
    }

    private void saveBestScore(){
        try {
            FileOutputStream fOut = openFileOutput("bestscore", Context.MODE_PRIVATE);
            fOut.write(BEST_SCORE);
            fOut.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getBestScore(){
        try {
            FileInputStream fin = openFileInput("bestscore");
            BEST_SCORE = fin.read();
            fin.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
