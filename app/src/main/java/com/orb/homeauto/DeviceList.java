package com.orb.homeauto;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class DeviceList extends AppCompatActivity {
    // list of variables needed
    public static String EXTRA_ADDRESS = "device_address";
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    private ProgressDialog progress;
    //declaring the widget object
    Button btnshowPairedDevice;
    ListView pairedDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

/* uncomment in final release
        //media player
        MediaPlayer mp = MediaPlayer.create(this, R.raw.welcome);
        mp.start();
        // play greetings
*/

        // defining the wiegets
        btnshowPairedDevice = (Button) findViewById(R.id.btnShowPaired);
        pairedDeviceList = (ListView) findViewById(R.id.pairedlist);

        //initiating myBluetooth

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) {
            // the device does not support bluetooth

            Toast.makeText(this, R.string.bt_not_supported, Toast.LENGTH_SHORT).show();
            finish();//closing the app

        } else if (!myBluetooth.isEnabled()) {

            Toast.makeText(this, R.string.switchbton, Toast.LENGTH_SHORT).show();
            myAlert(null);

        } else {
            //Bluetooth is working
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        }

        btnshowPairedDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPairedDevice();
            }
        });


    }

    public void btenable(View view) {
        if (!myBluetooth.isEnabled()) {
            enableBT ebt = new enableBT();
            ebt.execute();
        } else {
            Toast.makeText(this, R.string.btalreadyon, Toast.LENGTH_SHORT).show();
        }


    }


    public void btdisable(View view) {
        if (myBluetooth.isEnabled()) {
            // if bluetooth is enabled
            myBluetooth.disable();
            Toast.makeText(this, R.string.btdisabled, Toast.LENGTH_SHORT).show();
            //disable it
        }
    }


    private void myAlert(final View view) {

        AlertDialog.Builder alertdialogBuilder = new AlertDialog.Builder(this);
        alertdialogBuilder.setMessage(R.string.confirmbton);
        alertdialogBuilder.setTitle(R.string.confirmbtontitle);
        alertdialogBuilder.setIcon(R.drawable.ic_bt);
        alertdialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btenable(view);
            }
        });
        alertdialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Bluetooth Not Enabled", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = alertdialogBuilder.create();
        alert.show();
    }

    private void showPairedDevice() {
        // show paired devices in the list view
        if (myBluetooth.isEnabled()) {
            pairedDevices = myBluetooth.getBondedDevices();
            ArrayList btlist = new ArrayList();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice bt : pairedDevices) {
                    btlist.add(bt.getName().trim() + "\n" + bt.getAddress());
                    //adding the paired device name and addtress to the array
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_paired_device, Toast.LENGTH_SHORT).show();

            }

            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, btlist);
            pairedDeviceList.setAdapter(adapter);
            pairedDeviceList.setOnItemClickListener(selectedDevice);

        } else {
            Toast.makeText(getApplicationContext(), R.string.btnotenabled, Toast.LENGTH_SHORT).show();
        }

    }


    private AdapterView.OnItemClickListener selectedDevice = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent myIntent = new Intent(DeviceList.this, HomeControl.class);
            myIntent.putExtra(DeviceList.EXTRA_ADDRESS, address);
            startActivity(myIntent);
            // start new activity for connecting the selected device
        }
    };


    // class that enable Bluetooth
    private class enableBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(DeviceList.this, "Switching On", "Please wait while Bluetooth is \nswitched On!!");
            // showing a progress dialog

        }


        @Override
        protected Void doInBackground(Void... devices) {
// process done in the background
            try {
                if (!myBluetooth.isEnabled()) {
                    myBluetooth.enable();
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            progress.dismiss(); // close the progress dialog


        }


    }

}
