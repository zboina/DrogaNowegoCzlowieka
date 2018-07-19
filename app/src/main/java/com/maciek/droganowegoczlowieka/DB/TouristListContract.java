package com.maciek.droganowegoczlowieka.DB;

import android.provider.BaseColumns;

/**
 * Created by Geezy on 15.07.2018.
 */

public class TouristListContract {


    public static  class TouristListEntry implements BaseColumns {
        public static final String TABLE_NAME = "TURIST_LIST";
        public static final String COLUMN_POSITION = "POSITION";
        public static final String COLUMN_AUDIO = "AUDIO";
        public static final String COLUMN_LOCAL_URI = "LOCAL_URI";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_TYPE_ID = "TYPE_ID";

    }

}
