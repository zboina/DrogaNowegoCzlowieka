package com.maciek.droganowegoczlowieka.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.maciek.droganowegoczlowieka.DB.InsertPositionToList;
import com.maciek.droganowegoczlowieka.DB.TuristListDbHelper;
import com.maciek.droganowegoczlowieka.DB.TuristListDbQuery;
import com.maciek.droganowegoczlowieka.R;
import com.maciek.droganowegoczlowieka.Utilities.DownloadService;

public class DownloaderActivity extends AppCompatActivity implements   Response.Listener<byte[]>, Response.ErrorListener, View.OnClickListener{

    private int progressStatus;
    private int cursorMax;
    private ProgressBar progressBar;
    private SQLiteDatabase db;
    private Button acceptButton, rejectButton;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);
        TuristListDbHelper turistListDbHelper = new TuristListDbHelper(this);
        db = turistListDbHelper.getReadableDatabase();
        TuristListDbQuery turistListDbQuery = new TuristListDbQuery(db);
        Cursor cursor = turistListDbQuery.getQueriedTouristList("1");
        MainActivity.verifyStoragePermissions(this);
        textView = findViewById(R.id.downloader_textView);
        cursorMax = cursor.getCount()*3-3;
        progressBar = findViewById(R.id.progress_bar_downloader);
        acceptButton = findViewById(R.id.accept_download_button);
        rejectButton = findViewById(R.id.reject_download_button);
        acceptButton.setOnClickListener(this);
        rejectButton.setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        registerReceiver(receiver, new IntentFilter(
                DownloadService.NOTIFICATION));
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }



    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(DownloadService.RESULT);
                if (resultCode == RESULT_OK) {
                    progressBar.setProgress(bundle.getInt(DownloadService.COUNTER));
                    if(bundle.getString(DownloadService.DIRECTORY).equals("audio")){
                        InsertPositionToList.insertAudioUri(db, bundle.getString(DownloadService.FILEPATH), bundle.getString(DownloadService.FILENAME), bundle.getString(DownloadService.TYPE_ID) );
                    }else if(bundle.getString(DownloadService.DIRECTORY).equals("picture")){
                        InsertPositionToList.insertPictureUri(db, bundle.getString(DownloadService.FILEPATH), bundle.getString(DownloadService.FILENAME),  bundle.getString(DownloadService.TYPE_ID) );
                    }else {
                        InsertPositionToList.insertVideoUri(db, bundle.getString(DownloadService.FILEPATH), bundle.getString(DownloadService.FILENAME),  bundle.getString(DownloadService.TYPE_ID) );
                    }if ((bundle.getInt(DownloadService.COUNTER)==cursorMax)){
                        progressBar.setVisibility(View.INVISIBLE);
                        Integer test = Integer.parseInt(bundle.getString(DownloadService.TYPE_ID));
                        test++;
                        if(test<=4)
                            downloadConent(test.toString());
                        else {
                            textView.setText("Gitara sciągnałeś wszystko i się aplikacja nie wysypała, idź słuchaj");
                        }
                    }

//                    Toast.makeText(TrackListActivity.this, "Musze przemyslec jak dac znac ze skonczylo sie pobierac, ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Download failed",
                            Toast.LENGTH_SHORT).show();

                }
            }
        }
    };



    public void downloadConent(String typeId){
        Cursor cursor;
        progressStatus=0;
        TuristListDbHelper turistListDbHelper = new TuristListDbHelper(this);
        db = turistListDbHelper.getReadableDatabase();
        TuristListDbQuery turistListDbQuery = new TuristListDbQuery(db);
        cursor = turistListDbQuery.getAudioCursor(typeId);
        switch (typeId){
            case "1":
                textView.setText("Teraz pobieram: Turysta");
                break;
            case "2":
                textView.setText("Teraz pobieram: Oaza");
                break;
            case "3":
                textView.setText("Teraz pobieram: Domowy cośtam");
                break;
            case "4":
                textView.setText("Teraz pobieram: Zaawansowyany");
                break;
        }

        progressBar.setMax(cursor.getCount()*3);
        progressBar.setVisibility(View.VISIBLE);
        if (cursor.moveToFirst()){
            do{
                String data = cursor.getString(cursor.getColumnIndex("AUDIO"));
                String mUrl="http://android.x25.pl/NowaDroga/audio/"+ data;
                Intent intent = new Intent(this, DownloadService.class);
                // add infos for the service which file to download and where to store
                intent.putExtra(DownloadService.FILENAME, data);
                intent.putExtra(DownloadService.URL,
                        mUrl);
                intent.putExtra(DownloadService.DIRECTORY, "audio");
                intent.putExtra(DownloadService.TYPE_ID, typeId);
                intent.putExtra(DownloadService.COUNTER, progressStatus++);
                startService(intent);
            }while(cursor.moveToNext());
        }
        cursor = turistListDbQuery.getPictureCursor(typeId);
        if (cursor.moveToFirst()){
            do{
                String data = cursor.getString(cursor.getColumnIndex("PICTURE"));
                String mUrl="http://android.x25.pl/NowaDroga/foto/"+ data;
                Intent intent = new Intent(this, DownloadService.class);
                // add infos for the service which file to download and where to store
                intent.putExtra(DownloadService.FILENAME, data);
                intent.putExtra(DownloadService.URL,
                        mUrl);
                intent.putExtra(DownloadService.DIRECTORY, "picture");
                intent.putExtra(DownloadService.TYPE_ID, typeId);
                intent.putExtra(DownloadService.COUNTER, progressStatus++);
                startService(intent);
            }while(cursor.moveToNext());
        }
        cursor = turistListDbQuery.getVideoCursor(typeId);
        if (cursor.moveToFirst()){
            do{
                String data = cursor.getString(cursor.getColumnIndex("VIDEO"));
                if(data==null){
                    data="null";
                }
                String mUrl="http://android.x25.pl/NowaDroga/video/"+ data;
                Intent intent = new Intent(this, DownloadService.class);
                // add infos for the service which file to download and where to store
                intent.putExtra(DownloadService.FILENAME, data);
                intent.putExtra(DownloadService.URL,
                        mUrl);
                intent.putExtra(DownloadService.DIRECTORY, "video");
                intent.putExtra(DownloadService.COUNTER, progressStatus++);
                intent.putExtra(DownloadService.TYPE_ID, typeId);
                startService(intent);
            }while(cursor.moveToNext());
        }
        cursor.close();

    }


    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(byte[] response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.accept_download_button:
                downloadConent("1");
                break;
        }
    }
}