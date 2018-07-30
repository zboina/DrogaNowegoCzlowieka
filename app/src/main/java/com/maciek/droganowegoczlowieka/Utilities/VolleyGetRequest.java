package com.maciek.droganowegoczlowieka.Utilities;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.maciek.droganowegoczlowieka.Activities.DownloaderActivity;
import com.maciek.droganowegoczlowieka.Activities.MainActivity;
import com.maciek.droganowegoczlowieka.DB.InsertPositionToList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Geezy on 16.07.2018.
 */

public class VolleyGetRequest {

    private Context context;
    SQLiteDatabase db;
    RequestQueue mRequestQueue;
    boolean isDone;
    public VolleyGetRequest(Context context, SQLiteDatabase db){
        this.context=context;
        this.db=db;
    }

    public void getNameAndPosition(final int typeId, final ContentLoadingProgressBar loader, final Context mContext){

        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        isDone=false;

        String url = "http://android.x25.pl/NowaDroga/GET/getTitleAndPictureById.php?typeId="+typeId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = (JSONArray)jsonObject.get("punkty");

                            for(int i =0; i<jsonArray.length(); i++){
                                String audio = jsonArray.getJSONObject(i).getString("audio");
                                int position = jsonArray.getJSONObject(i).getInt("position");
                                String name = jsonArray.getJSONObject(i).getString("nazwa");
                                String jpgname = jsonArray.getJSONObject(i).getString("foto");
                                String isActiveString = jsonArray.getJSONObject(i).getString("aktywny");
                                boolean isActive =true;
                                if(isActiveString.equals("1")){
                                    isActive=true;
                                }else {
                                    isActive=false;
                                }

                                InsertPositionToList.insertAudiJpgDataByPos(db,audio, typeId, position, name, jpgname, isActive);

                            }
                            getVideoAndAudio(typeId, loader, mContext);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isDone = false;
            }
        });
        mRequestQueue.add(stringRequest);
    }

    public void getVideoAndAudio(final int typeId, final ContentLoadingProgressBar loader, final Context mContext) {
        String url = "http://android.x25.pl/NowaDroga/GET/getVideoByTitle.php?typeId="+typeId;
        isDone=false;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = (JSONArray)jsonObject.get("punkty");

                            for(int i =0; i<jsonArray.length(); i++){
                                String audio = jsonArray.getJSONObject(i).getString("audio");
                                String video = jsonArray.getJSONObject(i).getString("plik");
                                if(video.equals("null"))
                                    video=null;
                                InsertPositionToList.insertVideo(db,video,audio);

                            }

                            if(typeId==4){
                                loader.setVisibility(View.GONE);
                                mContext.startActivity(new Intent(mContext, DownloaderActivity.class));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mRequestQueue.add(stringRequest);

    }




}
