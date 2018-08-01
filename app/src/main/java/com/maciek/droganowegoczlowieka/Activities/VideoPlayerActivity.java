package com.maciek.droganowegoczlowieka.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.maciek.droganowegoczlowieka.R;

import static com.maciek.droganowegoczlowieka.Activities.TrackListActivity.TITLE;
import static com.maciek.droganowegoczlowieka.Activities.TrackListActivity.TYPE_ID;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    String typeId;
    String title;
    private FloatingActionButton mFloatingButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Intent intent = getIntent();
        String uriToLunch = intent.getStringExtra("URI");
        VideoView videoView = findViewById(R.id.video_view);
        if(uriToLunch==null){
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.introdnc);
            mFloatingButton=findViewById(R.id.launch_main_video_activity);
            mFloatingButton.setVisibility(View.VISIBLE);
            mFloatingButton.setOnClickListener(this);

        }else {
            videoView.setVideoURI(Uri.parse("file://"+uriToLunch));
        }
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
        typeId = intent.getStringExtra(TYPE_ID);
        title = intent.getStringExtra(TITLE);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(typeId==null){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    mFloatingButton.setVisibility(View.GONE);
                }else {
                    Intent intent = new Intent(getApplicationContext(), MediaPlayerActivity.class);
                    intent.putExtra(TYPE_ID, typeId);
                    intent.putExtra(TITLE, title);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(typeId!=null && title!=null){
            Intent intent = new Intent(this, MediaPlayerActivity.class);
            intent.putExtra(TYPE_ID, typeId);
            intent.putExtra(TITLE, title);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.launch_main_video_activity:
                startActivity(new Intent(this, MainActivity.class));
                mFloatingButton.setVisibility(View.GONE);
        }
    }
}
