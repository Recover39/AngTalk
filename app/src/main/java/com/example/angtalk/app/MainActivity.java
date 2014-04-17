package com.example.angtalk.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends ActionBarActivity {

    public static final String TAG = "AngTalk";

    EditText editTextInput, editTextSender, editTextReceiver;
    String inputedText, sender, receiver, jsonData;
    Gson gson;
    MessageData msgData;
    ControlMessage controlMessage;
    MessagesStorage messagesStorage;
    private static ArrayAdapter<String> simpleAdapter;

    /* These are for GCM */
    public static final String PROPERTY_REG_ID = "nhnnext";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    String regid;
    String SENDER_ID = "477958854971";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();

        // GCM 등록과
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
        // sets the app name in the intent
        registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
        registrationIntent.putExtra("sender", SENDER_ID);
        startService(registrationIntent);
        Log.i(TAG, "Stack");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView messageList = (ListView) findViewById(R.id.listViewMessage);
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        editTextInput = (EditText) findViewById(R.id.editTextInput);
        editTextSender = (EditText) findViewById(R.id.editTextSender);
        editTextReceiver = (EditText) findViewById(R.id.editTextReceiver);

        gson = new Gson();
        msgData = new MessageData();
        controlMessage = ControlMessage.getInstance();
        messagesStorage = new MessagesStorage(this);
        simpleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, controlMessage.getArrayList());

        messageList.setAdapter(simpleAdapter);

        // this.registerReceiver(GcmBroadcastReceiver, null);

        messagesStorage.getAllMessage();    // Get all messages from DB
        simpleAdapter.notifyDataSetChanged();   // Notify data set changed
        messageList.setSelection(messageList.getCount() - 1);   // Show end of list

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputedText = editTextInput.getText().toString();    // Get text stuff
                sender = editTextSender.getText().toString();    // Get text stuff
                receiver = editTextReceiver.getText().toString();    // Get text stuff

                if (!inputedText.equals("")) {
                    if (inputedText.equals("clear db")) {
                        messagesStorage.clearMessage();
                        simpleAdapter.notifyDataSetChanged();   // notify data set changed
                    }  // DB clear

                    msgData.setData(sender, receiver, inputedText);
                    jsonData = gson.toJson(msgData);
                    controlMessage.addMesseage(0, jsonData);
                    messagesStorage.saveMessage(sender, inputedText);

                    try {
                        new MakeRequest().execute(jsonData);
                    }
                    catch (Exception e)
                    {
                        Log.e("ERR : ", e.toString());
                    }

                    editTextInput.setText("");
                }
            }
        });
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                      PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }

        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }

        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.
                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(null, null, null);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    public static ArrayAdapter<String> getSimpleAdapter() {
        return simpleAdapter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controlMessage.getArrayList().clear();
    }
}
