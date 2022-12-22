package com.simple.fullcallrecorder.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.simple.fullcallrecorder.R;
import com.simple.fullcallrecorder.databinding.ActivityPlayRecordBinding;

import java.io.IOException;

public class PlayRecordActivity extends AppCompatActivity {
    private ActivityPlayRecordBinding binding;
    private String path = "";
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        if(intent != null){
            path = intent.getStringExtra("path");
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (IOException e) {
                Log.d("TAG","MediaPlayer Error " + e.getMessage());
            }
        }


        binding.playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(path != null){
                   if(!mediaPlayer.isPlaying()){
                       binding.playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
                       mediaPlayer.prepareAsync();
                   } else {
                       binding.playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                   }
                }
            }
        });

    }
}