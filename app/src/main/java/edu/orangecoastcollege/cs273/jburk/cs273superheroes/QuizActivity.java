package edu.orangecoastcollege.cs273.jburk.cs273superheroes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/***
 * Entry point for Superheroes application. Defines all the variables and constants used,
 * as well as the widgets used. The onCreate method loads the data from a JSON file, and instantiates
 * the widgets.
 *
 * Written by Jim Burk
 * October 16, 2017
 */

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "Superhero Quiz";

    private static final int HEROES_IN_QUIZ = 10;

    private Button[] mButtons = new Button[4];

    private List<SuperHeroes> mAllHeroesList;
    private List<SuperHeroes> mQuizHeroesList;
    private SuperHeroes mCorrectHero;
    private int mTotalGuesses;
    private int mCorrectGuesses;
    private SecureRandom rng;
    private Handler handler;

    private int gameType = 1;

    private TextView mQuestionNumberTextView;
    private TextView mGuessWhatTextView;
    private ImageView mHeroImageView;
    private TextView mAnswerTextView;

    private Animation shakeAnim;

    private static final String CHOICES = "pref_quizTypes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        try {
            mAllHeroesList = JSONLoader.loadJSONFromAsset(this);
        } catch (IOException e) {
            Log.e("CS273 Superoes", "Error Loading from JSON", e);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);

        mQuizHeroesList = new ArrayList<>(HEROES_IN_QUIZ);
        rng = new SecureRandom();
        handler = new Handler();

        mHeroImageView = (ImageView) findViewById(R.id.heroImageView);
        mQuestionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        mAnswerTextView = (TextView) findViewById(R.id.answerTextView);
        mGuessWhatTextView = (TextView) findViewById(R.id.guessWhatTextView);

        mQuestionNumberTextView.setText(getString(R.string.question, 1, HEROES_IN_QUIZ));

        mButtons[0] = (Button) findViewById(R.id.button);
        mButtons[1] = (Button) findViewById(R.id.button2);
        mButtons[2] = (Button) findViewById(R.id.button3);
        mButtons[3] = (Button) findViewById(R.id.button4);

        resetQuiz();
    }

    /***
     * The resetQuiz() method sets the number of guesses, correct and total, to zero. It thin creates
     * a list of superheroes to use in the quiz, and sets the question type text view. It then requests
     * the next hero to be used.
     */

    public void resetQuiz() {
        int randomPosition;

        mCorrectGuesses = 0;
        mTotalGuesses = 0;
        mQuizHeroesList.clear();

        int size = mAllHeroesList.size();
        while (mQuizHeroesList.size() < HEROES_IN_QUIZ) {
            randomPosition = rng.nextInt(size);

            SuperHeroes randomHero = mAllHeroesList.get(randomPosition);
            if (!mQuizHeroesList.contains(randomHero))
                mQuizHeroesList.add(randomHero);
        }

        switch (gameType) {
            case 1: {
                mGuessWhatTextView.setText(R.string.guess_superhero);
                break;
            }
            case 2: {
                mGuessWhatTextView.setText(R.string.guess_super_power);
                break;
            }
            case 3: {
                mGuessWhatTextView.setText(R.string.guess_one_thing);
                break;
            }
            default: {
                Log.e(TAG, "Game type not valid");
            }
        }

        loadNextHero();
    }

    private void loadNextHero() {
        mCorrectHero = mQuizHeroesList.remove(0);

        mAnswerTextView.setText("");
        int questionNumber = HEROES_IN_QUIZ - mQuizHeroesList.size();
        mQuestionNumberTextView.setText(getString(R.string.question, questionNumber, HEROES_IN_QUIZ));

        AssetManager am = getAssets();
        try {
            InputStream stream = am.open(mCorrectHero.getImageName());
            Drawable image = Drawable.createFromStream(stream, mCorrectHero.getName());
            mHeroImageView.setImageDrawable(image);
        }
        catch (IOException e) {
            Log.e(TAG, "Error loading image: " + mCorrectHero.getImageName(), e);
        }

        do {
            Collections.shuffle(mAllHeroesList);
        }
        while(mAllHeroesList.subList(0, mButtons.length).contains(mCorrectHero));

        for (int i = 0; i < mButtons.length; i++) {
            mButtons[i].setEnabled(true);
            switch (gameType) {
                case 1: {
                    mButtons[i].setText(mAllHeroesList.get(i).getName());
                    break;
                }
                case 2: {
                    mButtons[i].setText(mAllHeroesList.get(i).getSuperpower());
                    break;
                }
                case 3: {
                    mButtons[i].setText(mAllHeroesList.get(i).getOneThing());
                    break;
                }
                default: {
                    Log.e(TAG, "Game type not valid");
                }
            }

        }
        switch (gameType) {
            case 1: {
                mButtons[rng.nextInt(mButtons.length)].setText(mCorrectHero.getName());
                break;
            }
            case 2: {
                mButtons[rng.nextInt(mButtons.length)].setText(mCorrectHero.getSuperpower());
                break;
            }
            case 3: {
                mButtons[rng.nextInt(mButtons.length)].setText(mCorrectHero.getOneThing());
                break;
            }
            default: {
                Log.e(TAG, "Game type not valid");
            }
        }
    }

    /***
     * When the user makes a guess, this method determines if the guess is correct or not, and displays
     * the corresponding information. Upon a a correct guess to the last question the score is displayed.
     * With a wrong answer, the image is "shaken".
     *
     * @param v
     */

    public void makeGuess(View v) {
        Button clickedButton = (Button) v;

        String guess = clickedButton.getText().toString();

        mTotalGuesses++;

        String buttonValue = "";
        switch (gameType) {
            case 1: {
                buttonValue = mCorrectHero.getName();
                break;
            }
            case 2: {
                buttonValue = mCorrectHero.getSuperpower();
                break;
            }
            case 3: {
                buttonValue = mCorrectHero.getOneThing();
                break;
            }
            default: {
                Log.e(TAG, "Game type not valid");
            }
        }

        if (guess.equals(buttonValue)) {
            for (Button b : mButtons)
                b.setEnabled(false);

            mCorrectGuesses++;
            mAnswerTextView.setText(buttonValue);
            mAnswerTextView.setTextColor(ContextCompat.getColor(this, R.color.correct_answer));

            if (mCorrectGuesses < HEROES_IN_QUIZ) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextHero();
                    }
                }, 2000);
            }
            else {
                // Show an alert dialog and reset quiz
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.results, mTotalGuesses, (double) ( 100 * mCorrectGuesses) / mTotalGuesses));
                builder.setCancelable(false);
                builder.setPositiveButton(getString(R.string.reset_quiz), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetQuiz();
                    }
                });
                builder.create();
                builder.show();
            }
        }
        else {
            shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
            mHeroImageView.startAnimation(shakeAnim);
            mAnswerTextView.setText(getString(R.string.incorrect_answer));
            mAnswerTextView.setTextColor(ContextCompat.getColor(this, R.color.incorrect_answer));
            clickedButton.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
        return super.onOptionsItemSelected(item);
    }

    SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // lets figure out which key changed
            if (key.equals(CHOICES)) {
                    String gameString = sharedPreferences.getString(CHOICES, "Superhero Name");
                    if (gameString.equals("Superhero Name"))
                        gameType = 1;
                    else if (gameString.equals("Superpower"))
                        gameType = 2;
                    else
                        gameType = 3;

                    resetQuiz();
            }
            Toast.makeText(QuizActivity.this, R.string.restarting_quiz, Toast.LENGTH_SHORT).show();
        }
    };
}