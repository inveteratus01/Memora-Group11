package com.example.memora_group11_rhvcbfipg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.memora_group11_rhvcbfipg.ui.folderlist.FolderListActivity;

public class MainActivity extends AppCompatActivity {

    Button viewFlashCardBtn;
    Button aboutBtn;
    Button quitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.correctCount), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewFlashCardBtn = findViewById(R.id.viewFlashCardBtn);
        aboutBtn = findViewById(R.id.aboutBtn);
        quitBtn = findViewById(R.id.quitBtn);

        viewFlashCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
                startActivity(intent);
            }
        });
    }
}