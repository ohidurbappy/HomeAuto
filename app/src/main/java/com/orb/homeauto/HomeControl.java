package com.orb.homeauto;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;

import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;
import com.vikramezhil.droidspeech.OnDSPermissionsListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class HomeControl extends AppCompatActivity implements OnDSListener{
    // declaring the required vars
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public TextToSpeech ttobj;//required for text to speech
    public String voiceData;
    private String customCmd;
    // declaring the widgets
    Button btnOn, btnOff, btnConnect, btnDisconnect;
    private ProgressDialog progress;
    public TextView logTxt, homeauto;
    ScrollView logScroll;
    EditText cText;
    //voice input request code
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    DroidSpeech droidSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting intents from the first device list activity
        Intent recvIntent = getIntent();
        address = recvIntent.getStringExtra(DeviceList.EXTRA_ADDRESS);

        droidSpeech = new DroidSpeech(this, null);
        droidSpeech.setOnDroidSpeechListener(this);

        setContentView(R.layout.activity_home_control);
        speakText();


        // attaching the widgets to object;
        btnOn = (Button) findViewById(R.id.btnOn);
        btnOff = (Button) findViewById(R.id.btnOff);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnDisconnect = (Button) findViewById(R.id.btndisconnect);
        logTxt = (TextView) findViewById(R.id.logTxt);
        cText = (EditText) findViewById(R.id.customCmd);


        homeauto = (TextView) findViewById(R.id.homeAuto);
        Typeface customfont = Typeface.createFromAsset(getAssets(), "fonts/KOFFEEW.TTF");
        homeauto.setTypeface(customfont);


        logScroll = (ScrollView) findViewById(R.id.logScroll);
        btConnect();


        cText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cText.setFocusableInTouchMode(true);
            }
        });


    }










    @Override
    public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages)
    {
        //Log.i(TAG, "Current speech language = " + currentSpeechLanguage);
        //Log.i(TAG, "Supported speech languages = " + supportedSpeechLanguages.toString());

        if(supportedSpeechLanguages.contains("bn-BD"))
        {
            // Setting the droid speech preferred language as tamil if found
            droidSpeech.setPreferredLanguage("bn-BD");

            // Setting the confirm and retry text in tamil
            droidSpeech.setOneStepVerifyConfirmText("OK");
            droidSpeech.setOneStepVerifyRetryText("RETRY");
        }
    }





    @Override
    public void onDroidSpeechRmsChanged(float rmsChangedValue)
    {
        // Log.i(TAG, "Rms change value = " + rmsChangedValue);
    }

    @Override
    public void onDroidSpeechLiveResult(String liveSpeechResult)
    {
        //Log.i(TAG, "Live speech result = " + liveSpeechResult);
        Toast.makeText(this,liveSpeechResult,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDroidSpeechFinalResult(String finalSpeechResult)
    {
        // Do whatever you want with the speech result

    }

    @Override
    public void onDroidSpeechClosedByUser()
    {
        //stop.setVisibility(View.GONE);
        //start.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDroidSpeechError(String errorMsg)
    {
        // Speech error
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();

    }







    // start listening for input
    private void startVoiceInput() {

        droidSpeech.startDroidSpeechRecognition();

    /*    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getText(R.string.voiceinputmsg));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceData = result.get(0);
                    analyzevoicedata();// check the voice data
                }
                break;
            }

        }
    }









    public void analyzevoicedata() {
        switch (voiceData) {
            case "light on":
                sendData(getString(R.string.lighton));
                break;
            case "light off":
                sendData(getString(R.string.lightoff));
                break;
            default:
                log("Could Not Recognize");
        }

    }


    // click event for button speak
    public void btnSpeak(View view) {

        startVoiceInput();
    }


    public void btConnect() {
        //connecting to the bluetooth device
        connectBT cbt = new connectBT();
        cbt.execute();
        log("Connected");
    }

    public void btnConnect(View view) {
        btConnect();

    }


    public void btnCustomCmd(View view) {
        customCmd = cText.getText().toString();
        sendData(customCmd);
        cText.setText("");
        cText.clearFocus();
        log(customCmd);
    }


    public void btDisconnect(View view) {
        // disconnect from the bluetooth device
        try {
            if (btSocket != null) {
                btSocket.close();
                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                log("Disconnected");
                ;
                btSocket = null;
                btnDisconnect.setVisibility(View.INVISIBLE);
                btnConnect.setVisibility(View.VISIBLE);
                //hiding this btn
            } else {
                Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Problem disconnecting", Toast.LENGTH_SHORT).show();
        }
    }


    public void lightOn(View view) {
        // switch on the light
        if (btSocket != null) {

            sendData(getString(R.string.lighton));
            log(getString(R.string.lighton));
            say("Light is on");
        } else {
            say("You are not connected.");

        }
    }


    public void lightOff(View view) {
        //switch off the light
        sendData(getString(R.string.lightoff));
        log(getString(R.string.lightoff));
        say("light is off");

    }


    public void btnabout(View view) {
        Intent i = new Intent(HomeControl.this, About.class);
        startActivity(i);
        // go to the about activity

    }

    public void log(String s) {
        String tmp;
        tmp = logTxt.getText().toString();
        logTxt.setText(tmp + "\n" + s);
        logScroll.fullScroll(View.FOCUS_DOWN);


    }


    public void say(String x) {
        //ttobj.speak(x, TextToSpeech.QUEUE_ADD, null);
    }

    public void speakText() {
        ttobj = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            ttobj.setLanguage(Locale.US);
                            ttobj.setSpeechRate(1); //optional
                            ttobj.setPitch(7);
                        }
                    }
                });

    }


    // method to send data to the bluetooth
    public void sendData(String x) {
        if (btSocket != null) {
            try {
                if (btSocket != null) {
                    btSocket.getOutputStream().write(x.getBytes());
                }
            } catch (IOException e) {
                Toast.makeText(HomeControl.this, "Could not Send data", Toast.LENGTH_SHORT).show();
            }
        } else {
            say("Are you connected?");
        }

    }


    // class that executes the connection of the bluetooth device connection
    private class connectBT extends AsyncTask<Void, Void, Void> {


        private boolean connectsuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(HomeControl.this, "Connecting", "Please wait while it \nconnects!!");
            // showing a progress dialog

        }


        @Override
        protected Void doInBackground(Void... devices) {
// process done in the background
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();// mobile bt device
                    BluetoothDevice btDevice = myBluetooth.getRemoteDevice(address);
                    // the address is found from the previous activity as  string

                    btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect(); // get connected to the device
                }

            } catch (IOException e) {
                connectsuccess = false; // handling errors
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!connectsuccess) {
                Toast.makeText(getApplicationContext(), "Could not Connect", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Connect Success", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
                btnConnect.setVisibility(View.INVISIBLE);
                btnDisconnect.setVisibility(View.VISIBLE);

                // hiding the disconnect btn

            }
            progress.dismiss(); // close the progress dialog


        }


        // class for listening to the bletooth sockek in the back ground
        private class listenBT extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {

            }


            @Override
            protected Void doInBackground(Void... devices) {
                try {
                    while (true) {

                    }

                } catch (Exception e) {

                }
                return null;

            }


            @Override
            protected void onPostExecute(Void result) {

                super.onPostExecute(result);
            }
        }

    }
}
