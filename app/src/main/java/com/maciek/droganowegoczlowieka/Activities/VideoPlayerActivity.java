package com.maciek.droganowegoczlowieka.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.maciek.droganowegoczlowieka.R;

import static com.maciek.droganowegoczlowieka.Activities.TrackListActivity.TITLE;
import static com.maciek.droganowegoczlowieka.Activities.TrackListActivity.TYPE_ID;

public class VideoPlayerActivity extends AppCompatActivity {

    String typeId;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Intent intent = getIntent();
        String uriToLunch = intent.getStringExtra("URI");
        VideoView videoView = findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse("file://"+uriToLunch));
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
        typeId = intent.getStringExtra(TYPE_ID);
        title = intent.getStringExtra(TITLE);


    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(this, MediaPlayerActivity.class);
        intent.putExtra(TYPE_ID, typeId);
        intent.putExtra(TITLE, title);
        startActivity(intent);
    }
}
