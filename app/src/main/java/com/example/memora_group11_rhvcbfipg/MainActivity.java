package com.example.memora_group11_rhvcbfipg;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.memora_group11_rhvcbfipg.ui.about.AboutActivity;
import com.example.memora_group11_rhvcbfipg.ui.folderlist.FolderListActivity;
import com.example.memora_group11_rhvcbfipg.utils.SoundButtonListener;

public class MainActivity extends AppCompatActivity {

    Button viewFlashCardBtn;
    Button aboutBtn;
    Button quitBtn;
    private Animation titleAnimation;
    private Animation descAnimation;
    private TextView titleTextView;
    private TextView descriptionTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        MediaPlayer startupSound = MediaPlayer.create(this, R.raw.game_start);
        startupSound.setVolume(1f, 1f); // Set volume to 70%
        startupSound.start();
        startupSound.setOnCompletionListener(MediaPlayer::release);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.correctCount), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleTextView = findViewById(R.id.textViewTitle);
        descriptionTextView = findViewById(R.id.textViewDescription);

        // Load animations
        titleAnimation = AnimationUtils.loadAnimation(this, R.anim.pixel_float);
        descAnimation = AnimationUtils.loadAnimation(this, R.anim.pixel_float);

        // Set animation properties
        titleAnimation.setRepeatCount(Animation.INFINITE);
        titleAnimation.setRepeatMode(Animation.REVERSE);
        descAnimation.setRepeatCount(Animation.INFINITE);
        descAnimation.setRepeatMode(Animation.REVERSE);
        descAnimation.setStartOffset(600);

        // Start animations
        titleTextView.startAnimation(titleAnimation);
        descriptionTextView.startAnimation(descAnimation);

        viewFlashCardBtn = findViewById(R.id.viewFlashCardBtn);
        aboutBtn = findViewById(R.id.openAboutPage);
        quitBtn = findViewById(R.id.quitBtn);

        viewFlashCardBtn.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
                        startActivity(intent);
                    }
                }, R.raw.button_click));

        aboutBtn.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                    }
                }, R.raw.button_click));

        quitBtn.setOnClickListener(new SoundButtonListener(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish(); // Close the activity and exit the app
                    }
                }, R.raw.button_back, 0.4f));
    }

}