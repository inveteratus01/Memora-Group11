package com.example.memora_group11_rhvcbfipg.ui.folderlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.memora_group11_rhvcbfipg.MainActivity;
import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.modal.FolderModal;
import com.example.memora_group11_rhvcbfipg.ui.forms.FolderFormActivity;
import com.example.memora_group11_rhvcbfipg.utils.SoundButtonListener;

import java.util.ArrayList;

public class FolderListActivity extends AppCompatActivity implements FolderListAdapter.OnFolderDeletedListener {
    private ArrayList<FolderModal> folderModalArrayList;
    private DBHandler dbHandler;
    private FolderListAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);

        folderModalArrayList = new ArrayList<>();
        dbHandler = new DBHandler(this);
        folderModalArrayList = dbHandler.getFolderNames();

        if (folderModalArrayList.isEmpty()) {
            findViewById(R.id.emptyFolderListMessage).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.emptyFolderListMessage).setVisibility(View.GONE);
        }

        RecyclerView recyclerViewDashboard = findViewById(R.id.foldersListRecyclerView);
        recyclerViewDashboard.setLayoutManager(new LinearLayoutManager(this));

        cardAdapter = new FolderListAdapter(folderModalArrayList, this, this);
        recyclerViewDashboard.setAdapter(cardAdapter);

        Button createFolderButton = findViewById(R.id.createFolderButton);
        createFolderButton.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FolderListActivity.this, FolderFormActivity.class);
                        startActivity(intent);
                    }
                }, R.raw.button_click));

        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FolderListActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the back stack
                        startActivity(intent);
                        finish(); // Close the current activity
                    }
                }, R.raw.button_back, 0.3f));
    }
    @Override
    public void onFolderDeleted(ArrayList<FolderModal> updatedData) {
        cardAdapter.updateData(updatedData);
    }
}