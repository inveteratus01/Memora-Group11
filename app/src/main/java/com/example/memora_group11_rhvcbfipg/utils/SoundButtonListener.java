package com.example.memora_group11_rhvcbfipg.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;

public class SoundButtonListener implements View.OnClickListener {
    private Context context;
    private View.OnClickListener wrappedListener;
    private int soundResourceId;
    private float volume = 0.5f; // Default volume level (0.0 to 1.0)

    public SoundButtonListener(Context context, View.OnClickListener listener, int soundResourceId) {
        this.context = context;
        this.wrappedListener = listener;
        this.soundResourceId = soundResourceId;
    }

    public SoundButtonListener(Context context, View.OnClickListener listener, int soundResourceId, float volume) {
        this.context = context;
        this.wrappedListener = listener;
        this.soundResourceId = soundResourceId;
        this.volume = Math.max(0.0f, Math.min(1.0f, volume)); // Ensure volume is between 0.0 and 1.0
    }

    @Override
    public void onClick(View view) {
        // Play sound effect with adjusted volume
        MediaPlayer mediaPlayer = MediaPlayer.create(context, soundResourceId);
        mediaPlayer.setVolume(volume, volume); // Set left and right channel volumes
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);

        // Execute the original click listener
        if (wrappedListener != null) {
            wrappedListener.onClick(view);
        }
    }
}