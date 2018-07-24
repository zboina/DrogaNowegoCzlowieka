package com.maciek.droganowegoczlowieka.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.maciek.droganowegoczlowieka.DB.TouristListContract;
import com.maciek.droganowegoczlowieka.DB.TuristListDbHelper;
import com.maciek.droganowegoczlowieka.DB.TuristListDbQuery;
import com.maciek.droganowegoczlowieka.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MediaPlayerActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private SQLiteDatabase db;
    private TuristListDbQuery turistListDbQuery;
    private TuristListDbHelper turistListDbHelper;
    private SeekBar mSeekBar;
    private Cursor cursor;
    Timer timer;
    ArrayList<String> listOfTitles;
    int i = 0;
    File convertedFile;
    String TAG = "WIADOMOSC";
    FileOutputStream out;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        Intent intent = getIntent();
        String title = intent.getStringExtra(TrackListActivity.TITLE);
        turistListDbHelper = new TuristListDbHelper(this);

        db = turistListDbHelper.getReadableDatabase();
        turistListDbQuery = new TuristListDbQuery(db);
        cursor = turistListDbQuery.getPosition(title);
        cursor.moveToFirst();
        String position = cursor.getString(cursor.getColumnIndex(TouristListContract.TouristListEntry.COLUMN_POSITION));
        listOfTitles = new ArrayList<>();
        cursor = turistListDbQuery.getAudioCursor("1");



        mSeekBar = findViewById(R.id.seek_bar);
        File output = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC),
                title);
        timer = new Timer();
        try {
            FileInputStream fis = new FileInputStream(output);
            takeInputStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            listOfTitles.add(cursor.getString(cursor.getColumnIndex(TouristListContract.TouristListEntry.COLUMN_AUDIO)));
        }
        int playListSize=cursor.getCount()-Integer.parseInt(position);

        if(playListSize>1) playNext();


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mp != null && b){
                    mp.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

//                playNext(cursor, mediaPlayer, i);

//                mediaPlayer.start();
            }
        });


    }

    private void updateSeekBar(final MediaPlayer mediaPlayer){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                    mSeekBar.setProgress(mCurrentPosition);
                    mSeekBar.setMax(mediaPlayer.getDuration()/1000);
                }
                mHandler.postDelayed(this, 500);
            }
        });
    }

    public void playFile()
    {
        try {
            mp = new MediaPlayer();
            FileInputStream fis = new FileInputStream(convertedFile);
            mp.setDataSource(fis.getFD());

            Toast.makeText(this, "Success, Path has been set", Toast.LENGTH_SHORT).show();

            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepare();
            Toast.makeText(this, "Media Player prepared", Toast.LENGTH_SHORT).show();

            mp.start();

            updateSeekBar(mp);
            Toast.makeText(this, "Media Player playing", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

    }//end playFile

    public void takeInputStream(InputStream stream) throws IOException
    {

        try
        {
            convertedFile = File.createTempFile("convertedFile", ".dat", getDir("filez", 0));
            Toast.makeText(this, "Successful file and folder creation.", Toast.LENGTH_SHORT).show();

            out = new FileOutputStream(convertedFile);
            Toast.makeText(this, "Success out set as output stream.", Toast.LENGTH_SHORT).show();

            //RIGHT AROUND HERE -----------

            byte buffer[] = new byte[16384];
            int length = 0;
            while ( (length = stream.read(buffer)) != -1 )
            {
                out.write(buffer,0, length);
            }

            //stream.read(buffer);
            Toast.makeText(this, "Success buffer is filled.", Toast.LENGTH_SHORT).show();
            out.close();

            playFile();
        }catch(Exception e)
        {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }//end catch
    }//end grabBuffer


    @Override
    protected void onPause() {
        super.onPause();
        if(mp!=null){
            mp.stop();
        }
    }

//    private void playNext(Cursor cursor, MediaPlayer mediaPlayer, int i){
//        i++;
//        mediaPlayer.seekTo(0);
//        updateSeekBar(mediaPlayer);
//        cursor.moveToFirst();
//        String position = cursor.getString(cursor.getColumnIndex(TouristListContract.TouristListEntry.COLUMN_POSITION));
//        cursor.close();
//        if(position!=null){
//            try {
//                cursor = turistListDbQuery.getAudioTitle(position+i);
//                cursor.moveToFirst();
//                String title = cursor.getString(cursor.getColumnIndex(TouristListContract.TouristListEntry.COLUMN_AUDIO));
//                File output = new File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_MUSIC),
//                        title);
//                FileInputStream fis = new FileInputStream(output);
//                takeInputStream(fis);
//            }catch (Exception e){
//
//            }
//        }
//
//    }

    public void playNext() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mp.reset();
                File output =
                new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC),
                        listOfTitles.get(++i));
                FileInputStream fis = null;
                mp =MediaPlayer.create(MediaPlayerActivity.this, Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+listOfTitles.get(++i)));
                mp.start();

                if (listOfTitles.size() > i+1) {
                    playNext();
                }
            }
        },mp.getDuration()+100);
    }

    @Override
    public void onDestroy() {
        if (mp.isPlaying())
            mp.stop();
        timer.cancel();
        super.onDestroy();
    }

}
