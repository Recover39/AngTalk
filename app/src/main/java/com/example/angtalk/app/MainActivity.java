package com.example.angtalk.app;

import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    EditText editTextInput;
    ArrayList<String> arrayList;
    ArrayAdapter<String> simpleAdapter;
    MessageData msgData;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView messageList = (ListView) findViewById(R.id.listViewMessage);
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        editTextInput = (EditText) findViewById(R.id.editTextInput);

        arrayList = new ArrayList<String>();
        simpleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        msgData = new MessageData();
        gson = new Gson();

        messageList.setAdapter(simpleAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputedText = editTextInput.getText().toString();    // Get text stuff

                if(!inputedText.equals("")) {
                    arrayList.add("나 : " + inputedText);
                    msgData.setData("나", "너", inputedText);

                    try {
                        new MakeRequest().execute(gson.toJson(msgData));
                    }
                    catch(Exception e)
                    {
                        Log.e("ERR : ", e.toString());
                    }

                    simpleAdapter.notifyDataSetChanged();
                    editTextInput.setText("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


