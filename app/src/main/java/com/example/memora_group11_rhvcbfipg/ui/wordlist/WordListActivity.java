package com.example.memora_group11_rhvcbfipg.ui.wordlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.modal.WordModal;
import com.example.memora_group11_rhvcbfipg.ui.cardflip.CardFlip;
import com.example.memora_group11_rhvcbfipg.ui.folderlist.FolderListActivity;
import com.example.memora_group11_rhvcbfipg.ui.forms.WordFormActivity;

import java.util.ArrayList;

public class WordListActivity extends AppCompatActivity {

    private ArrayList<WordModal> wordModelArrayList;
    private RecyclerView recyclerView;
    private Button btnReview;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        setTitle(getFolderNameFromSharedPreferences());

        recyclerView = findViewById(R.id.wordListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wordModelArrayList = new ArrayList<>();
        dbHandler = new DBHandler(this);
        int folderId = getFolderIdFromSharedPreferences();
        wordModelArrayList = dbHandler.getWordsByFolderId(folderId);

        if (wordModelArrayList.isEmpty()) {
            findViewById(R.id.btnReview).setVisibility(View.GONE);
            findViewById(R.id.emptyListMessage).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.btnReview).setVisibility(View.VISIBLE);
            findViewById(R.id.emptyListMessage).setVisibility(View.GONE);
        }

        WordListAdapter wordListAdapter = new WordListAdapter(wordModelArrayList, this);
        recyclerView.setAdapter(wordListAdapter);

        btnReview = findViewById(R.id.btnReview);

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WordListActivity.this, CardFlip.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WordListActivity.this, WordFormActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.fabBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WordListActivity.this, FolderListActivity.class);
                startActivity(intent);
                finish(); // Close the current activity to prevent it from staying in the back stack
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh data from database
        int folderId = getFolderIdFromSharedPreferences();
        wordModelArrayList = dbHandler.getWordsByFolderId(folderId);

        // Update UI based on whether list is empty
        if (wordModelArrayList.isEmpty()) {
            findViewById(R.id.btnReview).setVisibility(View.GONE);
            findViewById(R.id.emptyListMessage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btnReview).setVisibility(View.VISIBLE);
            findViewById(R.id.emptyListMessage).setVisibility(View.GONE);
        }

        // Update adapter with new data
        WordListAdapter wordListAdapter = new WordListAdapter(wordModelArrayList, this);
        recyclerView.setAdapter(wordListAdapter);
    }

    private int getFolderIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.folder_preferences), MODE_PRIVATE);
        return sharedPreferences.getInt(getString(R.string.current_folder_id), -1);
    }

    private String getFolderNameFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.folder_preferences), MODE_PRIVATE);
        return sharedPreferences.getString(getString(R.string.current_folder_name), "Words");
    }
}