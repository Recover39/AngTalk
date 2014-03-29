package com.example.angtalk.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ganghan-yong on 2014. 3. 23..
 */
public class MessagesStorage {
    private Context context;
    private SQLiteDatabase database;
    public MessagesStorage(Context context) {
        this.context = context;

        database = context.openOrCreateDatabase("angTalkDB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    }
}
