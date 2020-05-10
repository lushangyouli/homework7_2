package com.bytedance.videoplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button play;
    private Button pause;
    private TextView tv_cur;
    private TextView tv_end;
    private SeekBar seekBar;
    private Timer timer;
    private boolean isSeekBarChange;


    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = (VideoView) findViewById(R.id.videoView);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        tv_cur = findViewById(R.id.tv_cur);
        tv_end = findViewById(R.id.tv_end);
        seekBar = findViewById(R.id.seekBar);
        mediaController = new MediaController(this);
        play.setOnClickListener(new mClick());
        pause.setOnClickListener(new mClick());

        Intent intent = getIntent();
        videoView.setVideoURI(intent.getData());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int duration = videoView.getDuration() / 1000;
                int position = videoView.getCurrentPosition();
                tv_cur.setText(calculateTime(position / 1000));
                tv_end.setText(calculateTime(duration));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChange = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarChange = false;
                videoView.seekTo(seekBar.getProgress());
                tv_cur.setText(calculateTime(videoView.getCurrentPosition() / 1000));
            }
        });

//       全屏只需旋转手机
    }

    class mClick implements OnClickListener {


        private int duration;
        @Override
        public void onClick(final View view) {
 //           String uri = "android.resource://" + getPackageName() + "/" + R.raw.bytedance;
 //           videoView.setVideoURI(Uri.parse(uri));
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    duration = videoView.getDuration() / 1000;
                    tv_end.setText(calculateTime(duration));

                    int position = videoView.getCurrentPosition();
                    tv_cur.setText(calculateTime(position / 1000));

                    seekBar.setMax(duration * 1000);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(!isSeekBarChange) {
                                seekBar.setProgress(videoView.getCurrentPosition());
                            }
                        }
                    },0,50);

                }
            });
            videoView.setMediaController(mediaController);
            mediaController.setMediaPlayer(videoView);
            if(view == play) {
                videoView.requestFocus();
                videoView.start();
                videoView.seekTo(seekBar.getProgress());
            }
            else if (view == pause)
                videoView.pause();
        }
    }

    public String calculateTime(int time){
        int minute;
        int second;
        int hour;
        String TAG = "MS";
        Log.i(TAG, "calculateTime: " + time);
        if(time >= 3600) {
            hour = time / 3600;
            minute = (time - hour*3600) / 60;
            second = time % 60;
            if (time >= 60) {
                if (minute >= 0 && minute < 10) {
                    if (second >= 0 && second < 10)
                        return hour + ":0" + minute + ":" + "0" + second;
                    else
                        return hour + ":0" + minute + ":" + second;
                } else {
                    if (second >= 0 && second < 10)
                        return hour + ":" + minute + ":" + "0" + second;
                    else
                        return hour + ":" + minute + ":" + second;
                }
            } else if (time < 60) {
                second = time;
                Log.i(TAG, "calculateTime: " + "00:" + second);
                if (second >= 0 && second < 10)
                    return hour + ":00:" + "0" + second;
                else
                    return hour + ":00:" + second;
            }
        }
        else{
            if (time >= 60) {
                minute = time / 60;
                second = time % 60;
                if (minute >= 0 && minute < 10) {
                    if (second >= 0 && second < 10)
                        return "0" + minute + ":" + "0" + second;
                    else
                        return "0" + minute + ":" + second;
                } else {
                    if (second >= 0 && second < 10)
                        return minute + ":" + "0" + second;
                    else
                        return minute + ":" + second;
                }
            } else if (time < 60) {
                second = time;
                Log.i(TAG, "calculateTime: " + "00:" + second);
                if (second >= 0 && second < 10)
                    return "00:" + "0" + second;
                else
                    return "00:" + second;
            }
        }


        return null;
    }
}
