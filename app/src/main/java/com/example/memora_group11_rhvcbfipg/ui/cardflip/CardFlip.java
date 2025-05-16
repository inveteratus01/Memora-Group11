package com.example.memora_group11_rhvcbfipg.ui.cardflip;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.modal.WordModal;
import com.example.memora_group11_rhvcbfipg.ui.forms.WordFormActivity;
import com.example.memora_group11_rhvcbfipg.ui.score.ScoreActivity;
import com.example.memora_group11_rhvcbfipg.ui.wordlist.WordListActivity;
import com.example.memora_group11_rhvcbfipg.utils.SoundButtonListener;

import java.util.ArrayList;

public class CardFlip extends AppCompatActivity {
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;
    private View mCardFrontLayout;
    private View mCardBackLayout;
    private int currentWordIndex;
    private ArrayList<WordModal> wordModelArrayList;

    private int correctCount = 0;
    private int incorrectCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip);
        setTitle(getFolderNameFromSharedPreferences());

        findViews();
        loadAnimations();
        changeCameraDistance();
        loadWords();
        showWord();

        Button btnCorrect = findViewById(R.id.btnCorrect);
        Button btnIncorrect = findViewById(R.id.btnIncorrect);

        btnIncorrect.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        incorrectCount++;
                        showNextWordOrScore();
                    }
                }, R.raw.button_click));

        btnCorrect.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        correctCount++;
                        showNextWordOrScore();
                    }
                }, R.raw.button_click));

        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish(); // Return to the previous activity (Word List)
                    }
                }, R.raw.button_back, 0.4f));
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardFrontLayout.setCameraDistance(scale);
        mCardBackLayout.setCameraDistance(scale);
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_in_animation);
    }

    private void findViews() {
        mCardBackLayout = findViewById(R.id.card_back);
        mCardFrontLayout = findViewById(R.id.card_front);
    }

    public void flipCard(View view) {
        // Play flip sound
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.card_flip);
        mediaPlayer.setVolume(2f, 2f);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);

        if (!mIsBackVisible) {
            mCardBackLayout.setVisibility(View.VISIBLE); // Ensure back layout is visible
            mSetRightOut.setTarget(mCardFrontLayout);
            mSetLeftIn.setTarget(mCardBackLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = true;
        } else {
            mCardFrontLayout.setVisibility(View.VISIBLE); // Ensure front layout is visible
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = false;
        }
    }

    private void resetCardToFront() {
        if (mIsBackVisible) {
            // Immediately hide the back layout to prevent flash of answer
            mCardBackLayout.setVisibility(View.INVISIBLE);
            mCardFrontLayout.setVisibility(View.VISIBLE);

            // Reset animation state
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();

            mIsBackVisible = false;
        }
    }

    private void loadWords() {
        DBHandler dbHandler = new DBHandler(this);
        int folderId = getFolderIdFromSharedPreferences();
        wordModelArrayList = dbHandler.getWordsByFolderId(folderId);
        currentWordIndex = 0;
    }

    private void showWord() {
        TextView cardTextFront = findViewById(R.id.card_text_front);
        TextView cardTextBack = findViewById(R.id.card_text_back);
        cardTextFront.setText(wordModelArrayList.get(currentWordIndex).getWord());
        cardTextBack.setText(wordModelArrayList.get(currentWordIndex).getMeaning());
        showWordIndex();
    }

    private void showWordIndex() {
        TextView wordIndex = findViewById(R.id.wordIndex);
        int currentIndex = currentWordIndex + 1;
        int totalWords = wordModelArrayList.size();
        String indexText = getString(R.string.index_format, currentIndex, totalWords);
        wordIndex.setText(indexText);
    }

    private int getFolderIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.folder_preferences), MODE_PRIVATE);
        return sharedPreferences.getInt(getString(R.string.current_folder_id), -1);
    }

    private String getFolderNameFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.folder_preferences), MODE_PRIVATE);
        return sharedPreferences.getString(getString(R.string.current_folder_name), "Words");
    }

    private void showNextWordOrScore() {
        if (currentWordIndex < wordModelArrayList.size() - 1) {
            currentWordIndex++;
            resetCardToFront(); // Reset the card to the front
            showWord();         // Update the content of the card
        } else {
            showScores();        // Show the score screen
        }
    }

    private void showScores() {
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("correctCount", correctCount);
        intent.putExtra("incorrectCount", incorrectCount);
        startActivity(intent);
        finish();
    }
}