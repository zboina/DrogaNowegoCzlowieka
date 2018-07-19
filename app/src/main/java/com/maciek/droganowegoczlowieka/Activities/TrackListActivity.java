package com.maciek.droganowegoczlowieka.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.maciek.droganowegoczlowieka.Adapter.TrackListAdapter;
import com.maciek.droganowegoczlowieka.DB.InsertPositionToList;
import com.maciek.droganowegoczlowieka.DB.TuristListDbHelper;
import com.maciek.droganowegoczlowieka.DB.TuristListDbQuery;
import com.maciek.droganowegoczlowieka.R;
import com.maciek.droganowegoczlowieka.Utilities.DownloadService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


public class TrackListActivity extends AppCompatActivity implements  TrackListAdapter.ListItemClickListener{
//implements Response.Listener<byte[]>, Response.ErrorListener, TrackListAdapter.ListItemClickListener

    File convertedFile;
    String TAG = "WIADOMOSC";
    FileOutputStream out;
    MediaPlayer mp;
    private SQLiteDatabase db;
    int count;
    HashMap<String,String> temp;
    private TrackListAdapter trackListAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        TuristListDbHelper turistListDbHelper = new TuristListDbHelper(this);
        db = turistListDbHelper.getReadableDatabase();
        TuristListDbQuery turistListDbQuery = new TuristListDbQuery(db);
        Cursor cursor = turistListDbQuery.getQueriedTouristList("1");
        trackListAdapter = new TrackListAdapter(this,cursor,this);
        mRecyclerView.setAdapter(trackListAdapter);
//        MySingleton.getInstance(this).getRequestQueue();
        temp = new HashMap<>();
        cursor = turistListDbQuery.getAudioCursor("1");
        MainActivity.verifyStoragePermissions(this);
        Toast.makeText(this, "Readable: " + isExternalStorageReadable() + " Writable: " + isExternalStorageWritable(), Toast.LENGTH_LONG).show();



//        if (cursor.moveToFirst()){
//            do{
//                String data = cursor.getString(cursor.getColumnIndex("AUDIO"));
//                String mUrl="http://android.x25.pl/NowaDroga/audio/"+ data;
//                Intent intent = new Intent(this, DownloadService.class);
//                // add infos for the service which file to download and where to store
//                intent.putExtra(DownloadService.FILENAME, data);
//                intent.putExtra(DownloadService.URL,
//                        mUrl);
//                startService(intent);
//            }while(cursor.moveToNext());
//        }
        cursor.close();

//        TODO: sprawdzić czy ktoś wyraził zgodę na używanie internetu// korzystanie z internal storage

    }

//    @Override
//    public void onResponse(byte[] response) {
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        try {
//            if (response!=null) {
//
//                try{
//                    long lenghtOfFile = response.length;
//                    InputStream input = new ByteArrayInputStream(response);
//                    String fileName = request.getUrl().substring(38);
//                    File file = new File(getApplicationContext().getFilesDir(), fileName );
//                    InsertPositionToList.insertUri(db, file.getAbsolutePath(), fileName );
//                    map.put("resume_path", file.toString());
//                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
//                    byte data[] = new byte[1024];
//
//                    long total = 0;
//
//                    while ((count = input.read(data)) != -1) {
//                        total += count;
//                        output.write(data, 0, count);
//                    }
//
//                    output.flush();
//
//                    output.close();
//                    input.close();
//                }catch(IOException e){
//                    e.printStackTrace();
//
//                }
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onErrorResponse(VolleyError error) {
//        Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE. ERROR:: "+error.getMessage());
//    }

    @Override
    public void onListItemClick(int clickedItemIndex, String title) throws IOException {

        File output = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC),
                title);
        FileInputStream fis = new FileInputStream(output);
        takeInputStream(fis);




//        TODO po kliknieciu na element odpala media playera i puszcza element z list sciaga URI pliki lokalnego


    }



//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                String string = bundle.getString(DownloadService.FILEPATH);
//
//                int resultCode = bundle.getInt(DownloadService.RESULT);
//                if (resultCode == RESULT_OK) {
//                    Toast.makeText(getApplicationContext(),
//                            "Download complete. Download URI: " + string,
//                            Toast.LENGTH_SHORT).show();
//                    InsertPositionToList.insertUri(db, bundle.getString(DownloadService.FILEPATH), bundle.getString(DownloadService.FILENAME));
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "Download failed",
//                            Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        }
//    };
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(receiver, new IntentFilter(
//                DownloadService.NOTIFICATION));
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(receiver);
//    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    public void takeInputStream(InputStream stream) throws IOException
    {
        //fileBeingBuffered = (FileInputStream) stream;
        //Toast.makeText(this, "sucessful stream conversion.", Toast.LENGTH_SHORT).show();
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


}
