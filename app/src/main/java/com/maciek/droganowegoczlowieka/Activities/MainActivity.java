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
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import static com.maciek.droganowegoczlowieka.Activities.TrackListActivity.TITLE;
import static com.maciek.droganowegoczlowieka.Activities.TrackListActivity.TYPE_ID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<byte[]>, Response.ErrorListener{

    private Button touristButton, homeChurchButton, debuggerButton, oazaYouthButton, advancedButton;
    private ProgressBar progressBar;
    private SQLiteDatabase db;
    private int progressStatus = 0;
    private Handler mHandler = new Handler();
    private  Cursor cursor;
    private ContentLoadingProgressBar loader;
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
        debuggerButton = findViewById(R.id.button_db_debugger);
        oazaYouthButton = findViewById(R.id.button_oaza_youth);
        advancedButton = findViewById(R.id.button_advanced);
        advancedButton.setOnClickListener(this);
        oazaYouthButton.setOnClickListener(this);
        debuggerButton.setOnClickListener(this);
        touristButton.setOnClickListener(this);
        homeChurchButton.setOnClickListener(this);
        loader = findViewById(R.id.loader);
//        Toolbar myToolbar = findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);

        if(tableIsEmpty(db)){
            VolleyGetRequest volleyGetRequest = new VolleyGetRequest(this, db);
            loader.setVisibility(View.VISIBLE);
            volleyGetRequest.getNameAndPosition(1,loader, this);
            volleyGetRequest.getNameAndPosition(2, loader, this);
            volleyGetRequest.getNameAndPosition(3, loader, this);
            volleyGetRequest.getNameAndPosition(4, loader, this);

        }

    }


    @Override
    public void onClick(View view) {
        Intent mIntent = new Intent(this, MediaPlayerActivity.class);

        switch (view.getId()) {
            case R.id.button_tourist:
//                mIntent.putExtra("type_id", "1");
//                startActivity(mIntent);
                mIntent.putExtra(TITLE, "turysta-wstep.mp3");
                mIntent.putExtra(TYPE_ID, "1");
                startActivity(mIntent);
                break;
            case R.id.button_home_church:
                mIntent.putExtra(TITLE, "domowy-kosciol-wstep.mp3");
                mIntent.putExtra("type_id", "3");
                startActivity(mIntent);
                break;
            case R.id.button_db_debugger:
                startActivity(new Intent(this, AndroidDatabaseManager.class));
                break;
            case R.id.button_advanced:
                mIntent.putExtra(TITLE, "moderator-wstep.mp3");
                mIntent.putExtra("type_id", "4");
                startActivity(mIntent);
                break;
            case R.id.button_oaza_youth:
                verifyStoragePermissions(this);
                mIntent.putExtra(TITLE, "oazowicz-wstep.mp3");
                mIntent.putExtra("type_id", "2");
                startActivity(mIntent);
                break;


        }
    }




    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(byte[] response) {

    }



    /*private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(DownloadService.RESULT);
                if (resultCode == RESULT_OK) {
                    progressStatus=bundle.getInt(DownloadService.COUNTER);

                    showProgressBar(cursor.getCount());
                    if(bundle.getString(DownloadService.DIRECTORY).equals("audio")){
                        InsertPositionToList.insertAudioUri(db, bundle.getString(DownloadService.FILEPATH), bundle.getString(DownloadService.FILENAME), bundle.getString(DownloadService.TYPE_ID) );
                    }else if(bundle.getString(DownloadService.DIRECTORY).equals("picture")){
                        InsertPositionToList.insertPictureUri(db, bundle.getString(DownloadService.FILEPATH), bundle.getString(DownloadService.FILENAME),  bundle.getString(DownloadService.TYPE_ID) );
                    }else {
                        InsertPositionToList.insertVideoUri(db, bundle.getString(DownloadService.FILEPATH), bundle.getString(DownloadService.FILENAME),  bundle.getString(DownloadService.TYPE_ID) );
                    }
                    if(progressStatus+1==cursor.getCount()){
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Download completed", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Download failed",
                            Toast.LENGTH_SHORT).show();

                }
            }
        }
    };*/


    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(receiver, new IntentFilter(
//                DownloadService.NOTIFICATION));
    }
    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(receiver);
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

    private static boolean tableIsEmpty(SQLiteDatabase db){
        String count = "SELECT count(*) FROM "+TouristListContract.TouristListEntry.TABLE_NAME;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0){
            return false;
        }else {
            return true;
        }

    }

}
