package com.example.memora_group11_rhvcbfipg.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.example.memora_group11_rhvcbfipg.R;

public class SoundManager {
    private static SoundManager instance;
    private SoundPool soundPool;
    private SparseIntArray soundMap;
    private boolean soundEnabled = true;

    private SoundManager(Context context) {
        // Configure audio attributes
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        // Create sound pool with maximum of 5 simultaneous sounds
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attributes)
                .build();

        // Map sounds to resource IDs
        soundMap = new SparseIntArray();
        soundMap.put(R.raw.button_click, soundPool.load(context, R.raw.button_click, 1));
        soundMap.put(R.raw.button_back, soundPool.load(context, R.raw.button_back, 1));
        soundMap.put(R.raw.button_success, soundPool.load(context, R.raw.button_success, 1));
        // Add more sounds as needed
    }

    public static SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context.getApplicationContext());
        }
        return instance;
    }

    public void playSound(int soundResourceId) {
        if (soundEnabled) {
            int soundId = soundMap.get(soundResourceId);
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void toggleSound(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }
}