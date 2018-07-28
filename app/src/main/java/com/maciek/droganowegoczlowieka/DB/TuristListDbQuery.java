package com.maciek.droganowegoczlowieka.DB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Geezy on 15.07.2018.
 */

public class TuristListDbQuery {

    private SQLiteDatabase mDb;
    public TuristListDbQuery(SQLiteDatabase db){
        mDb = db;
    }
    public Cursor getQueriedTouristList(String type_id){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_POSITION, TouristListContract.TouristListEntry.COLUMN_NAME, TouristListContract.TouristListEntry.COLUMN_AUDIO_URI, TouristListContract.TouristListEntry.COLUMN_AUDIO};
        String selection = TouristListContract.TouristListEntry.COLUMN_TYPE_ID + " = ?";
        String[] selectionArgs = {type_id};
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }
    public Cursor getAudioCursor(String type_id){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_AUDIO};
        String selection = TouristListContract.TouristListEntry.COLUMN_TYPE_ID + " = ?";
        String[] selectionArgs = {type_id};
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    public Cursor getPictureCursor(String type_id){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_PICTURE};
        String selection = TouristListContract.TouristListEntry.COLUMN_TYPE_ID + " = ?";
        String[] selectionArgs = {type_id};
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    public Cursor getVideoCursor(String type_id){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_VIDEO};
        String selection = TouristListContract.TouristListEntry.COLUMN_TYPE_ID + " = ?";
        String[] selectionArgs = {type_id};
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    public Cursor getAudioTitle(String position){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_POSITION, TouristListContract.TouristListEntry.COLUMN_NAME, TouristListContract.TouristListEntry.COLUMN_AUDIO_URI, TouristListContract.TouristListEntry.COLUMN_AUDIO};
        String selection = TouristListContract.TouristListEntry.COLUMN_POSITION + " = ?";
        String[] selectionArgs = {position};
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    public Cursor getUriByPosition (){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_AUDIO_URI};
      /*  String selection = TouristListContract.TouristListEntry.COLUMN_POSITION + " = ?";
        String[] selectionArgs = {};*/
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                null,
                null,
                null,
                null,
                null);
    }

    public Cursor getPosition(String title){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_POSITION, TouristListContract.TouristListEntry.COLUMN_NAME, TouristListContract.TouristListEntry.COLUMN_AUDIO_URI, TouristListContract.TouristListEntry.COLUMN_AUDIO};
        String selection = TouristListContract.TouristListEntry.COLUMN_AUDIO + " = ?";
        String[] selectionArgs = {title};
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                selection,
                selectionArgs,
                null,
                null,
                null);

    }
    public Cursor getPictureUriByAudioUri(String audio){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_PICTURE_URI};
        String selection = TouristListContract.TouristListEntry.COLUMN_AUDIO_URI + " = ?";
        String[] selectionArgs = {audio};
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }
    public Cursor getVideoUriByAudioUri(String audio){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_VIDEO_URI};
        String selection = TouristListContract.TouristListEntry.COLUMN_AUDIO_URI + " = ?";
        String[] selectionArgs = {audio};
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

}
