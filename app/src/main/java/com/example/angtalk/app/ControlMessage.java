package com.example.angtalk.app;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ControlMessage {
    private static ControlMessage instance;
    private Gson gson = new Gson();
    private ArrayList<String> arrayList = new ArrayList<String>();
    private String jsonData;
    public void addMesseage(int status, String sender, String receiver, String inputedText) {

        switch(status) {
            case 0:
                arrayList.add(sender + " : " + inputedText);
                break;

            case 1:
                arrayList.add(receiver + " - " + inputedText);
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
