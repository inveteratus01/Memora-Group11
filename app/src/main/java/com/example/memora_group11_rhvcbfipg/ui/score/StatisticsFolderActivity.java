package com.example.memora_group11_rhvcbfipg.ui.score;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.ui.folderlist.FolderListActivity;

public class StatisticsFolderActivity extends AppCompatActivity {
    private int folderId;
    private String folderName;
    private TextView textFolderName;
    private TextView textAttempts;
    private TextView textCorrect;
    private TextView textIncorrect;
    private TextView textAccuracy;

    private TextView textHighestScore;
    private TextView textAvgCorrect;
    private TextView textAvgIncorrect;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics_folder);

        // Initialize database handler
        dbHandler = new DBHandler(this);

        // Get folder ID and name from intent
        folderId = getIntent().getIntExtra("folderId", -1);
        folderName = getIntent().getStringExtra("folderName");

        // Initialize views
        textFolderName = findViewById(R.id.textFolderName);
        textAttempts = findViewById(R.id.textAttempts);
        textCorrect = findViewById(R.id.textCorrect);
        textIncorrect = findViewById(R.id.textIncorrect);
        textAccuracy = findViewById(R.id.textAccuracy);

        // Initialize new views
        textHighestScore = findViewById(R.id.textHighestScore);
        textAvgCorrect = findViewById(R.id.textAvgCorrect);
        textAvgIncorrect = findViewById(R.id.textAvgIncorrect);

        // Set folder name
        textFolderName.setText(folderName);

        // Load statistics
        loadStatistics();

        Button buttonReturn = findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatisticsFolderActivity.this, FolderListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button buttonResetStats = findViewById(R.id.buttonResetStats);
        buttonResetStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmResetStatistics();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadStatistics() {
        if (folderId == -1) return;

        Cursor cursor = dbHandler.getStatisticsByFolderId(folderId);

        int attempts = 0;
        int correct = 0;
        int incorrect = 0;
        int highestScore = 0;
        float avgCorrect = 0;
        float avgIncorrect = 0;
        float accuracy = 0;

        if (cursor != null && cursor.moveToFirst()) {
            int attemptsColumnIndex = cursor.getColumnIndex("attempts");
            int correctColumnIndex = cursor.getColumnIndex("correct");
            int incorrectColumnIndex = cursor.getColumnIndex("incorrect");
            int highestScoreColumnIndex = cursor.getColumnIndex("highest_score");
            int avgCorrectColumnIndex = cursor.getColumnIndex("avg_correct");
            int avgIncorrectColumnIndex = cursor.getColumnIndex("avg_incorrect");

            if (attemptsColumnIndex >= 0) attempts = cursor.getInt(attemptsColumnIndex);
            if (correctColumnIndex >= 0) correct = cursor.getInt(correctColumnIndex);
            if (incorrectColumnIndex >= 0) incorrect = cursor.getInt(incorrectColumnIndex);
            if (highestScoreColumnIndex >= 0) highestScore = cursor.getInt(highestScoreColumnIndex);
            if (avgCorrectColumnIndex >= 0) avgCorrect = cursor.getFloat(avgCorrectColumnIndex);
            if (avgIncorrectColumnIndex >= 0) avgIncorrect = cursor.getFloat(avgIncorrectColumnIndex);

            cursor.close();
        }

        // Calculate accuracy
        if (attempts > 0) {
            accuracy = (float) correct / (correct + incorrect) * 100;
        }

        // Update UI with existing statistics
        textAttempts.setText(String.valueOf(attempts));
        textCorrect.setText(String.valueOf(correct));
        textIncorrect.setText(String.valueOf(incorrect));
        textAccuracy.setText(String.format("%.1f%%", accuracy));

        // Update UI with new statistics
        textHighestScore.setText(String.valueOf(highestScore));
        textAvgCorrect.setText(String.format("%.1f", avgCorrect));
        textAvgIncorrect.setText(String.format("%.1f", avgIncorrect));
    }

    private void confirmResetStatistics() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Statistics");
        builder.setMessage("Are you sure you want to reset all statistics for this folder?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetStatistics();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void resetStatistics() {
        if (folderId != -1) {
            dbHandler.addOrUpdateStatistics(folderId, 0, 0, 0, 0, 0, 0);
            loadStatistics();
        }
    }
}