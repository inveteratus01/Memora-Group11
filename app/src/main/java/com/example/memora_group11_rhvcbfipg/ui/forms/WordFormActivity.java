package com.example.memora_group11_rhvcbfipg.ui.forms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.utils.SoundButtonListener;

public class WordFormActivity extends AppCompatActivity {
    private DBHandler dbhandler;
    private int wordIdToUpdate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_form);
        setTitle(getFolderNameFromSharedPreferences());

        dbhandler = new DBHandler(this);

        Button saveButton = findViewById(R.id.saveButton);
        EditText wordEditText = findViewById(R.id.wordEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        TextView textViewAddOrEditWord = findViewById(R.id.textViewAddOrEditWord);
        TextView textViewAddOrEditDesc = findViewById(R.id.textViewAddOrEditDesc);

        // Check if we're in edit mode
        Intent intent = getIntent();
        if (intent.getBooleanExtra("isEdit", false)) {
            wordIdToUpdate = intent.getIntExtra("wordId", -1);
            String currentWord = intent.getStringExtra("word");
            String currentDescription = intent.getStringExtra("description");

            // Populate form fields with current data
            wordEditText.setText(currentWord);
            descriptionEditText.setText(currentDescription);

            // Update UI to reflect editing mode
            textViewAddOrEditWord.setText("Edit Word");
            textViewAddOrEditDesc.setText("Edit Description");
            saveButton.setText("Update");
            setTitle("Edit Word");
        }

        saveButton.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String word = wordEditText.getText().toString();
                        String description = descriptionEditText.getText().toString();
                        int folderId = getFolderIdFromSharedPreferences();

                        if (word.isEmpty() || description.isEmpty()) {
                            Toast.makeText(WordFormActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (wordIdToUpdate != -1) {
                            // Update existing word
                            dbhandler.updateWord(wordIdToUpdate, word, description);
                            Toast.makeText(WordFormActivity.this, "Word has been updated", Toast.LENGTH_SHORT).show();
                            finish(); // Return to word list
                        } else {
                            // Add new word
                            dbhandler.addWord(folderId, word, description);
                            Toast.makeText(WordFormActivity.this, "Word has been added", Toast.LENGTH_SHORT).show();
                            wordEditText.setText("");
                            descriptionEditText.setText("");
                        }
                    }
                }, R.raw.button_success, 3.0f));

        Button wordListDashButton = findViewById(R.id.wordListDashButton);
        wordListDashButton.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish(); // Return to the previous activity (Word List)
                    }
                }, R.raw.button_back, 0.3f));
    }

    private int getFolderIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("FolderPreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("currentFolderId", -1);
    }

    private String getFolderNameFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.folder_preferences), MODE_PRIVATE);
        return sharedPreferences.getString(getString(R.string.current_folder_name), "Words");
    }
}