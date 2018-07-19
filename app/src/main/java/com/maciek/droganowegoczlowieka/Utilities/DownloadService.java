package com.maciek.droganowegoczlowieka.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
/**
 * Created by Geezy on 18.07.2018.
 */

public class DownloadService extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.vogella.android.service.receiver";

    public DownloadService() {
        super("DownloadService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        String urlPath = intent.getStringExtra(URL);
        String fileName = intent.getStringExtra(FILENAME);
        File output = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC),
                fileName);
        if (output.exists()) {
            output.delete();
        }

        try{
            URL u = new URL(urlPath);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(output);


            InputStream in = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ( (len1 = in.read(buffer)) > 0 ) {
                f.write(buffer,0, len1);
            }
            f.close();
            result = -1;
        }catch (Exception e){

        }

        publishResults(output.getAbsolutePath(), result, fileName);
    }

    private void publishResults(String outputPath, int result, String fileName) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(FILENAME, fileName);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}
