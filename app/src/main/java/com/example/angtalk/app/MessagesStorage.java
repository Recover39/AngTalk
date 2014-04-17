package com.example.angtalk.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Date;

/**
 * Created by ganghan-yong on 2014. 3. 23..
 */
public class MessagesStorage {
    private Context context;
    private SQLiteDatabase database;
    private String sql;
    private ControlMessage controlMessage;
    private Date date;

    public MessagesStorage(Context context) {   // 생성자로 테이블이 없으면 생성
        this.context = context;

        database = context.openOrCreateDatabase("angTalkDB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        sql = "CREATE TABLE IF NOT EXISTS CONVERSATION(time DATETIME, sender VARCHAR NOT NULL, content VARCHAR NOT NULL);";
        database.execSQL(sql);

        date = new Date();
    }

    public void saveMessage(String sender, String content) {    // 메세지 저장
        sql = "INSERT INTO CONVERSATION(time, sender, content) VALUES('" + date.getTime() + "', '" + sender + "', '" + content + "');";
        database.execSQL(sql);
    }

    public void clearMessage() {    // 모든 메세지 삭제
        sql = "DELETE FROM CONVERSATION; VACUUM;";
        database.execSQL(sql);

        controlMessage.getArrayList().clear();
    }

    public void getAllMessage() {   // 메세지 가져오기
        sql = "SELECT * FROM CONVERSATION;";
        Cursor cursor = database.rawQuery(sql, null);

        controlMessage = ControlMessage.getInstance();

        while(cursor.moveToNext()) {
            // Log.i("SQLiteTest", "ID : " + cursor.getString(0) + " / SENDER : " + cursor.getString(1) + " / CONTENT : " + cursor.getString(2));
            controlMessage.getArrayList().add(cursor.getString(1) + " : " + cursor.getString(2));
        }

        cursor.close();
    }
}
