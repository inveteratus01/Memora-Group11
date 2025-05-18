package com.example.memora_group11_rhvcbfipg.ui.about;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.utils.SoundButtonListener;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Set the title for the activity
        setTitle("About Memora");

        // Set up back button functionality
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish(); // Close this activity and return to the previous one
                    }
                }, R.raw.button_back, 0.3f));
    }
}