package com.example.angtalk.app;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ControlMessage {
    private static ControlMessage instance;
    private Gson gson = new Gson();
    private MessageData md = new MessageData();
    private ArrayList<String> arrayList = new ArrayList<String>();

    public void addMesseage(int status, String json) {

        switch(status) {
            case 0:
                md = gson.fromJson(json, MessageData.class);

                arrayList.add(md.getSender() + " : " + md.getMessageData());
                break;

            case 1:
                arrayList.add(md.getReceiver() + " : " + md.getMessageData());
                break;
        }

        MainActivity.getSimpleAdapter().notifyDataSetChanged();
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public static ControlMessage getInstance() {
        if(instance == null) {
            instance = new ControlMessage();
        }

        return instance;
    }
}
