package com.maciek.droganowegoczlowieka.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.maciek.droganowegoczlowieka.DB.InsertPositionToList;
import com.maciek.droganowegoczlowieka.DB.TouristListContract;
import com.maciek.droganowegoczlowieka.DB.TuristListDbHelper;
import com.maciek.droganowegoczlowieka.DB.TuristListDbQuery;
import com.maciek.droganowegoczlowieka.R;
import com.maciek.droganowegoczlowieka.Utilities.AndroidDatabaseManager;
import com.maciek.droganowegoczlowieka.Utilities.DownloadService;
import com.maciek.droganowegoczlowieka.Utilities.VolleyGetRequest;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<byte[]>, Response.ErrorListener{

    private Button touristButton, homeChurchButton, advancedButton, oazaYouthButton, clearList;
    private ProgressBar progressBar;
    private SQLiteDatabase db;
    private int progressStatus = 0;
    private Handler mHandler = new Handler();
    private  Cursor cursor;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TuristListDbHelper turistListDbHelper = new TuristListDbHelper(this);
        db = turistListDbHelper.getWritableDatabase();
        touristButton = findViewById(R.id.button_tourist);
        homeChurchButton = findViewById(R.id.button_home_church);
        advancedButton = findViewById(R.id.button_advanced);
        oazaYouthButton = findViewById(R.id.button_oaza_youth);
        progressBar = findViewById(R.id.progress_bar);
        clearList = findViewById(R.id.button_clear_list);
        clearList.setOnClickListener(this);
        oazaYouthButton.setOnClickListener(this);
        advancedButton.setOnClickListener(this);
        touristButton.setOnClickListener(this);
        homeChurchButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent mIntent = new Intent(this, TrackListActivity.class);;
        switch(view.getId()){
            case R.id.button_tourist:
                mIntent.putExtra("type_id", 1);
                startActivity(mIntent);
                break;
            case R.id.button_home_church:
                VolleyGetRequest volleyGetRequest = new VolleyGetRequest(this, db);
                volleyGetRequest.getNameAndPosition(1);
//                volleyGetRequest.getVideoAndAudio(1);
                Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();

//                mIntent.putExtra("type_id", 3);
//                startActivity(mIntent);

                break;
            case R.id.button_advanced:
                mIntent.putExtra("type_id", 4);
                startActivity(new Intent(this, AndroidDatabaseManager.class));
                break;
            case R.id.button_oaza_youth:
                verifyStoragePermissions(this);
                mIntent.putExtra("type_id", 2);
                TuristListDbHelper turistListDbHelper = new TuristListDbHelper(this);
                db = turistListDbHelper.getReadableDatabase();
                TuristListDbQuery turistListDbQuery = new TuristListDbQuery(db);
                cursor = turistListDbQuery.getAudioCursor("1");

                if (cursor.moveToFirst()){
                    do{
                        progressBar.setVisibility(View.VISIBLE);
                        progressStatus = cursor.getPosition();
                        String data = cursor.getString(cursor.getColumnIndex("AUDIO"));
                        String mUrl="http://android.x25.pl/NowaDroga/audio/"+ data;
                        Intent intent = new Intent(this, DownloadService.class);
                        // add infos for the service which file to download and where to store
                        intent.putExtra(DownloadService.FILENAME, data);
                        intent.putExtra(DownloadService.URL,
                                mUrl);
                        intent.putExtra(DownloadService.DIRECTORY, "audio");
                        intent.putExtra(DownloadService.COUNTER, progressStatus);
                        startService(intent);
                    }while(cursor.moveToNext());
                }
                cursor = turistListDbQuery.getPictureCursor("1");
                if (cursor.moveToFirst()){
                    do{
                        progressBar.setVisibility(View.VISIBLE);
                        progressStatus = cursor.getPosition();
                        String data = cursor.getString(cursor.getColumnIndex("PICTURE"));
                        String mUrl="http://android.x25.pl/NowaDroga/foto/"+ data;
                        Intent intent = new Intent(this, DownloadService.class);
                        // add infos for the service which file to download and where to store
                        intent.putExtra(DownloadService.FILENAME, data);
                        intent.putExtra(DownloadService.URL,
                                mUrl);
                        intent.putExtra(DownloadService.DIRECTORY, "picture");
                        intent.putExtra(DownloadService.COUNTER, progressStatus);
                        startService(intent);
                    }while(cursor.moveToNext());
                }
                cursor = turistListDbQuery.getVideoCursor("1");
                if (cursor.moveToFirst()){
                    do{
                        progressBar.setVisibility(View.VISIBLE);
                        progressStatus = cursor.getPosition();
                        String data = cursor.getString(cursor.getColumnIndex("VIDEO"));
                        if(data==null||data.equals("null")){
                            if(cursor.getPosition()+1==cursor.getCount()){
                                progressStatus=50;
                            }
                            continue;
                        }
                        String mUrl="http://android.x25.pl/NowaDroga/video/"+ data;
                        Intent intent = new Intent(this, DownloadService.class);
                        // add infos for the service which file to download and where to store
                        intent.putExtra(DownloadService.FILENAME, data);
                        intent.putExtra(DownloadService.URL,
                                mUrl);
                        intent.putExtra(DownloadService.DIRECTORY, "video");
                        intent.putExtra(DownloadService.COUNTER, progressStatus);
                        startService(intent);
                    }while(cursor.moveToNext());
                }
                cursor.close();
                break;
            case R.id.button_clear_list:
                db.execSQL( "DROP TABLE IF EXISTS " + TouristListContract.TouristListEntry.TABLE_NAME);
                db.execSQL( "CREATE TABLE " + TouristListContract.TouristListEntry.TABLE_NAME + " (" +
                        TouristListContract.TouristListEntry._ID + " INTEGER PRIMARY KEY," +
                        TouristListContract.TouristListEntry.COLUMN_POSITION + " NUMBER,"+
                        TouristListContract.TouristListEntry.COLUMN_AUDIO + " TEXT," +
                        TouristListContract.TouristListEntry.COLUMN_NAME + " TEXT," +
                        TouristListContract.TouristListEntry.COLUMN_AUDIO_URI + " TEXT," +
                        TouristListContract.TouristListEntry.COLUMN_TYPE_ID + " NUMBER);");
                Toast.makeText(this, "baza przeczyszczona", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(byte[] response) {

    }



    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String string = bundle.getString(DownloadService.FILEPATH);

                int resultCode = bundle.getInt(DownloadService.RESULT);
                if (resultCode == RESULT_OK) {
                    progressStatus=bundle.getInt(DownloadService.COUNTER);
                    if(progressStatus==cursor.getCount()){
                        Toast.makeText(MainActivity.this, "Download completed", Toast.LENGTH_SHORT).show();
                    }
                    showProgressBar(cursor.getCount());
//                    Toast.makeText(getApplicationContext(),
//                            "Download complete. Download URI: " + string,
//                            Toast.LENGTH_SHORT).show();
                    if(bundle.getString(DownloadService.DIRECTORY).equals("audio")){
                        InsertPositionToList.insertAudioUri(db, bundle.getString(DownloadService.FILEPATH), bundle.getString(DownloadService.FILENAME));
                    }else if(bundle.getString(DownloadService.DIRECTORY).equals("picture")){
                        InsertPositionToList.insertPictureUri(db, bundle.getString(DownloadService.FILEPATH), bundle.getString(DownloadService.FILENAME));
                    }else {
                        InsertPositionToList.insertVideoUri(db, bundle.getString(DownloadService.FILEPATH), bundle.getString(DownloadService.FILENAME));
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Download failed",
                            Toast.LENGTH_SHORT).show();

                }
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                DownloadService.NOTIFICATION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void showProgressBar(final int cursorSize){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus<cursorSize){

                    SystemClock.sleep(10);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(2*progressStatus+2);
                        }
                    });
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(progressStatus==cursorSize){
                            progressBar.setVisibility(View.INVISIBLE);
                        }


                    }
                });
            }
        }).start();
    }

}
