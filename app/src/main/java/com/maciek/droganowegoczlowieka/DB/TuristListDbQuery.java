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
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_POSITION, TouristListContract.TouristListEntry.COLUMN_NAME, TouristListContract.TouristListEntry.COLUMN_LOCAL_URI, TouristListContract.TouristListEntry.COLUMN_AUDIO};
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

    public Cursor getAudioUri(){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_LOCAL_URI};
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                null,
                null,
                null,
                null,
                null);
    }

    public Cursor getUriByPosition (String position){
        String[] ary = new String[] {TouristListContract.TouristListEntry.COLUMN_LOCAL_URI};
        String selection = TouristListContract.TouristListEntry.COLUMN_POSITION + " = ?";
        String[] selectionArgs = {position};
        return mDb.query(TouristListContract.TouristListEntry.TABLE_NAME,
                ary,
                null,
                null,
                null,
                null,
                null);
    }
}
