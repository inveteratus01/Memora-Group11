package com.example.memora_group11_rhvcbfipg.ui.score;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.ui.folderlist.FolderListActivity;
import com.example.memora_group11_rhvcbfipg.utils.SoundButtonListener;

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
        buttonReturn.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StatisticsFolderActivity.this, FolderListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, R.raw.button_back, 0.4f));

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
        // Create a custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.reset_statistics_dialog, null);
        TextView titleText = dialogView.findViewById(R.id.dialogTitle);
        TextView messageText = dialogView.findViewById(R.id.dialogMessage);
        Button positiveButton = dialogView.findViewById(R.id.buttonPositive);
        Button negativeButton = dialogView.findViewById(R.id.buttonNegative);
        ImageView resetIcon = dialogView.findViewById(R.id.resetIcon);

        // Set dialog content
        titleText.setText("Reset Statistics");
        messageText.setText("Are you sure you want to reset all statistics for \"" + folderName +
                "\"?\n\nThis action cannot be undone.");
        resetIcon.setImageResource(R.drawable.ic_warning);

        // Create and configure the AlertDialog with custom view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        // Prevent dialog from closing when touching outside
        alertDialog.setCanceledOnTouchOutside(false);

        // Set button click listeners
        positiveButton.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetStatistics();
                        alertDialog.dismiss();
                    }
                }, R.raw.button_success, 3.0f));

        negativeButton.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Just dismiss the dialog
                        alertDialog.dismiss();
                    }
                }, R.raw.button_back, 0.4f));

        // Show the custom dialog
        alertDialog.show();
    }

    private void resetStatistics() {
        if (folderId != -1) {
            // Reset stats in database
            dbHandler.addOrUpdateStatistics(folderId, 0, 0, 0, 0, 0, 0);

            // Reload the updated stats
            loadStatistics();

            // Play success sound after reset is complete
            new SoundButtonListener(this,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Empty because we only want the sound
                        }
                    },
                    R.raw.button_success, 3.0f
            ).onClick(null); // Manually trigger the sound

            // Optional: Show confirmation toast
            Toast.makeText(this, "Statistics reset successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}