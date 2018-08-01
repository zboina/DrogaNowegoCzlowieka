package com.maciek.droganowegoczlowieka.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maciek.droganowegoczlowieka.DB.TouristListContract;
import com.maciek.droganowegoczlowieka.DB.TuristListDbHelper;
import com.maciek.droganowegoczlowieka.DB.TuristListDbQuery;
import com.maciek.droganowegoczlowieka.MediaPlayer.MediaPlayerService;
import com.maciek.droganowegoczlowieka.MediaPlayer.StorageUtil;
import com.maciek.droganowegoczlowieka.R;
import com.maciek.droganowegoczlowieka.Utilities.OnSwipeTouchListener;

import java.net.URL;
import java.util.ArrayList;

import static com.maciek.droganowegoczlowieka.Activities.TrackListActivity.TITLE;
import static com.maciek.droganowegoczlowieka.Activities.TrackListActivity.TYPE_ID;
import static com.maciek.droganowegoczlowieka.MediaPlayer.MediaPlayerService.ACTION_PLAY;

public class MediaPlayerActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private SQLiteDatabase db;
    private TuristListDbQuery turistListDbQuery;
    private TuristListDbHelper turistListDbHelper;
    private SeekBar mSeekBar;
    private FloatingActionButton mFloatingActionButton;
    private TextView mTextView, trackTitleTextView;
    private Cursor cursor;
    ArrayList<String> listOfTitles;
    private int audioIndex = -1;
    IntentFilter filterRefreshUpdate;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.valdioveliu.valdio.audioplayer.PlayNewAudio";

    private ImageButton previous, next, start;
    private Button showList, goToMainMenu;
    private ImageView imageView;
    String position;
    private MediaPlayerService player;
    boolean serviceBound = false;
    String title;
    private String typeId;
    private Boolean isAudioGood=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        Intent intent = getIntent();
        title = intent.getStringExtra(TITLE);
        turistListDbHelper = new TuristListDbHelper(this);

        db = turistListDbHelper.getReadableDatabase();
        turistListDbQuery = new TuristListDbQuery(db);
        cursor = turistListDbQuery.getPosition(title);
        cursor.moveToFirst();
        position = cursor.getString(cursor.getColumnIndex(TouristListContract.TouristListEntry.COLUMN_POSITION));
        listOfTitles = new ArrayList<>();
        cursor = turistListDbQuery.getAudioUriByTypeId(intent.getStringExtra(TYPE_ID));
        typeId = intent.getStringExtra(TYPE_ID);

        //guziczki
        previous = findViewById(R.id.button_previous);
        next = findViewById(R.id.next_button);
        start = findViewById(R.id.start_stop_button);
        mTextView = findViewById(R.id.text_view_current_song);
        mFloatingActionButton = findViewById(R.id.launch_media_player);
        trackTitleTextView = findViewById(R.id.track_name_text_view);
        goToMainMenu = findViewById(R.id.GoToMainMenuButton);
        goToMainMenu.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        start.setOnClickListener(this);
        imageView = findViewById(R.id.image_view_media_player);
        showList = findViewById(R.id.showList);
        showList.setOnClickListener(this);
        imageView.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                player.skipToPrevious();
                ispressed = true;
                start.setImageResource(R.drawable.ic_play_white);
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                player.skipToNext();
                ispressed = true;
                start.setImageResource(R.drawable.ic_play_white);
            }



        });

        switch (typeId){
            case "1":
                trackTitleTextView.setText("Turysta");
                break;
            case "2":
                trackTitleTextView.setText("Oaza");
                break;
            case "3":
                trackTitleTextView.setText("Domowy kościół");
                break;
            case "4":
                trackTitleTextView.setText("Zaawansowani");
                break;
        }


//        mSeekBar = findViewById(R.id.seek_bar);


        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            listOfTitles.add(cursor.getString(cursor.getColumnIndex(TouristListContract.TouristListEntry.COLUMN_AUDIO_URI)));
        }
        int pos = Integer.parseInt(position);
        changeImageView(listOfTitles.get(pos));
//        playAudio(listOfTitles.get(pos));

//play the first audio in the ArrayList
        playAudio(pos);
        isAudioGood=true;
        filterRefreshUpdate = new IntentFilter();
        filterRefreshUpdate.addAction("andorid.mybroadcast");



       /* mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(serviceBound==true){
                    if(player.getMediaPlayer() != null && b){
                        player.getMediaPlayer().seekTo(i);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/





    }
    Integer progress,duration;
    private Handler mHandler = new Handler();


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(MediaPlayerActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

   /* private void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
    }*/






    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(listOfTitles);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    public void doBindService(){
        Intent playerIntent = new Intent(this, MediaPlayerService.class);
        startService(playerIntent);
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        playerIntent = new Intent(ACTION_PLAY);
        sendBroadcast(playerIntent);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            duration = intent.getIntExtra("DURATION", 0);
//            progress = intent.getIntExtra("PROGRESS", 0);
//            updateSeekBar(progress, duration);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        playMusic();
        musicMethodsHandler.post(musicRun);
    }

    @Override
    protected void onPause() {
        musicMethodsHandler.removeCallbacks(musicRun);
        if(serviceBound) {
            player.stopMedia();

        }
        super.onPause();
//        unregisterReceiver(receiver);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    String currentSong;
    String tempSong;
    Handler musicMethodsHandler = new Handler();
    Runnable musicRun = new Runnable() {
        @Override
        public void run() {
            if (serviceBound == true){
                // Check if service bounded
//                if (duration == null){ // Put data in it one time
//                    duration = player.getDuration();
//                    Log.v("Music dur: ", duration.toString());
//                    mSeekBar.setMax(duration);
//                }
                playMusic();
                currentSong=player.getActiveAudio();
//                progress = player.getCurrentPos();
//                Log.v("Music prog:", progress.toString());
//                mSeekBar.setProgress(progress);
                changeImageView(currentSong);
                changeTextView(currentSong);
                playVideo(currentSong);


            }else if(serviceBound == false){ // if service is not bounded log it
                Log.v("Still waiting to bound", Boolean.toString(serviceBound));
            }
            musicMethodsHandler.postDelayed(this, 100);
//            duration=null;


        }

    };
    private void changeTextView(String currentSong){
        cursor = turistListDbQuery.getAudioTitle(currentSong);
        cursor.moveToFirst();
        String stringTitle = cursor.getString(0);
        cursor = turistListDbQuery.getPostionByAudioUri(currentSong);
        cursor.moveToFirst();
        String position = cursor.getString(0);
        if(stringTitle!=null){
            mTextView.setText(position+". "+stringTitle);
        }


    }
    private void changeImageView(String currentSong){
        cursor = turistListDbQuery.getPictureUriByAudioUri(currentSong);
        cursor.moveToFirst();
        String stringUrl = cursor.getString(0);

        if(stringUrl.contains("null")){
            stringUrl="/storage/emulated/0/Pictures/turysta-dialog-malzenski.jpg";
        }
        try{
            URL url = new URL("file://"+stringUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(bitmap);
        }catch (Exception e){

        }

    }
    String title2;
    String stringVideoUrl;
    private void playVideo(String currentSong){
        cursor = turistListDbQuery.getVideoUriByAudioUri(currentSong);
        cursor.moveToFirst();
        stringVideoUrl =cursor.getString(0);
        if(stringVideoUrl!=null){
            try {
                mFloatingActionButton.setVisibility(View.VISIBLE);
                mFloatingActionButton.setClickable(true);
                cursor = turistListDbQuery.getAudioByAudioUri(currentSong);
                cursor.moveToFirst();
                title2 =cursor.getString(0);
                mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
                        intent.putExtra("URI", stringVideoUrl);
                        int pos = Integer.parseInt(position);

                        intent.putExtra(TITLE, title2);
                        intent.putExtra(TYPE_ID, typeId);
                        startActivity(intent);
                    }
                });
            }catch (Exception e){

            }
        }else {
            mFloatingActionButton.setClickable(false);
            mFloatingActionButton.setVisibility(View.GONE);
        }
    }
    int currentPosition;
    boolean ispressed = true;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.next_button:
                if(serviceBound==true){
                    player.skipToNext();
                    ispressed = true;
                    start.setImageResource(R.drawable.ic_play_white);
                }
                break;
            case R.id.start_stop_button:
                if(serviceBound==true){

                    if(ispressed){
                        ispressed= false;
                        start.setImageResource(R.drawable.ic_pause_circle);
                        player.resumeMedia();
                    }else {
                        ispressed=true;
                        start.setImageResource(R.drawable.ic_play_white);
                        player.pauseMedia();

                    }
                }
                break;
            case R.id.button_previous:
                if(serviceBound==true){
                    player.skipToPrevious();
                    ispressed = true;
                    start.setImageResource(R.drawable.ic_play_white);
                }
                break;
            case R.id.showList:
                Intent intent = new Intent(this, TrackListActivity.class);
                intent.putExtra(TYPE_ID, typeId);
                String audioUri = player.getActiveAudio();
                cursor = turistListDbQuery.getAudioByAudioUri(audioUri);
                cursor.moveToFirst();
                title2 =cursor.getString(0);
                intent.putExtra(TITLE, title2);
                startActivity(intent);
                break;
            case R.id.GoToMainMenuButton:
                startActivity(new Intent(this, MainActivity.class));
                break;

        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
    }

    private void playMusic(){
        int pos = Integer.parseInt(position);
        changeImageView(listOfTitles.get(pos));
        playAudio(pos);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
       return false;
    }



}
