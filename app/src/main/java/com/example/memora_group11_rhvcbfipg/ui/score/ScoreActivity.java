package com.example.memora_group11_rhvcbfipg.ui.score;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.ui.cardflip.CardFlip;
import com.example.memora_group11_rhvcbfipg.ui.wordlist.WordListActivity;
import com.example.memora_group11_rhvcbfipg.utils.SoundButtonListener;
public class ScoreActivity extends AppCompatActivity {
    private int correctCount;
    private int incorrectCount;
    private int folderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Get scores from intent
        correctCount = getIntent().getIntExtra("correctCount", 0);
        incorrectCount = getIntent().getIntExtra("incorrectCount", 0);
        folderId = getFolderIdFromSharedPreferences();

        // Display scores
        TextView correctCount = findViewById(R.id.correctCount);
        TextView incorrectCount = findViewById(R.id.incorrectCount);
        correctCount.setText(String.valueOf(this.correctCount));
        incorrectCount.setText(String.valueOf(this.incorrectCount));

        // Save statistics to database
        saveStatistics();

        Button btnReviewAgain = findViewById(R.id.reviewAgainButton);
        btnReviewAgain.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Start CardFlip activity fresh (it will automatically reshuffle)
                        Intent intent = new Intent(ScoreActivity.this, CardFlip.class);
                        startActivity(intent);
                        finish();
                    }
                }, R.raw.button_click));

        // Set up button listeners
        Button btnReturnToWordList = findViewById(R.id.wordListScoreDashButton);
        btnReturnToWordList.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ScoreActivity.this, WordListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, R.raw.button_click));
    }

    private void saveStatistics() {
        // Only save statistics if we have a valid folder ID
        if (folderId != -1) {
            DBHandler dbHandler = new DBHandler(this);
            // Use the updateStatisticsAfterAttempt method which handles all the complex logic
            dbHandler.updateStatisticsAfterAttempt(folderId, correctCount, incorrectCount);
        }
    }

    private int getFolderIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.folder_preferences), MODE_PRIVATE);
        return sharedPreferences.getInt(getString(R.string.current_folder_id), -1);
    }
}