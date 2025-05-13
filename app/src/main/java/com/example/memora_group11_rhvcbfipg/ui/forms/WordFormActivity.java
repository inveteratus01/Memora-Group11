package com.example.memora_group11_rhvcbfipg.ui.forms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;

public class WordFormActivity extends AppCompatActivity {
    private DBHandler dbhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_form);
        setTitle(getFolderNameFromSharedPreferences());

        dbhandler = new DBHandler(this);

        Button button = findViewById(R.id.saveButton);
        EditText wordEditText = findViewById(R.id.wordEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = wordEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                int folderId = getFolderIdFromSharedPreferences();

                if (word.isEmpty() || description.isEmpty()) {
                    Toast.makeText(WordFormActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbhandler.addWord(folderId, word, description);
                Toast.makeText(WordFormActivity.this, "Word has been added", Toast.LENGTH_SHORT).show();
                wordEditText.setText("");
                descriptionEditText.setText("");
            }
        });

        Button wordListDashButton = findViewById(R.id.wordListDashButton);
        wordListDashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to the WordListActivity
                finish(); // This closes the current activity and returns to the previous one in the stack
            }
        });
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