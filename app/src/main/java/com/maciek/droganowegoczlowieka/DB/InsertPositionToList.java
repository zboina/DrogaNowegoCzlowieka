package com.maciek.droganowegoczlowieka.DB;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Geezy on 15.07.2018.
 */

public class InsertPositionToList {
    
    public static void insertTitleTypePosName(SQLiteDatabase db, String title, int type_id, int position, String name) {
        if (db == null) {
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(TouristListContract.TouristListEntry.COLUMN_AUDIO, title);
        cv.put(TouristListContract.TouristListEntry.COLUMN_POSITION, position);
        cv.put(TouristListContract.TouristListEntry.COLUMN_TYPE_ID, type_id);
        cv.put(TouristListContract.TouristListEntry.COLUMN_NAME, name);

        //insert all guests in one transaction
        try {
            db.beginTransaction();
            //clear the table first
            db.insert(TouristListContract.TouristListEntry.TABLE_NAME, null ,cv);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //too bad :(
        } finally {
            db.endTransaction();
        }

    }

    public static void insertUri(SQLiteDatabase db, String uri,String audio) {
        if (db == null) {
            return;
        }

        ContentValues cv = new ContentValues();

        cv.put(TouristListContract.TouristListEntry.COLUMN_LOCAL_URI, uri);

        try {
            db.beginTransaction();
            //clear the table first
            db.update(TouristListContract.TouristListEntry.TABLE_NAME, cv, "AUDIO=?", new String[] {audio} );
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //too bad :(
        } finally {
            db.endTransaction();
        }

    }
}
