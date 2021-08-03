package com.example.bluetoothapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button searchButton;
    TextView statusTextView;
    ArrayList<String> blueToothDevices=new ArrayList<>();
    ArrayList<String> addresses=new ArrayList<>();

    ArrayAdapter  arrayAdapter;

    BluetoothAdapter bluetoothAdapter;

    //making of Receiver
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            Log.i("Action",action);
            if(bluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                statusTextView.setText("Finished");
                searchButton.setEnabled(true);
            }else if(BluetoothDevice.ACTION_FOUND.equals(action)){ //founding the details of the bluetooth
                BluetoothDevice device= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name=device.getName();
                String address= device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
                // Log.i("Device Found","Name: "+ name+"Address: "+ address+ "RSSI: "+rssi);
                if(!addresses.contains(address)){
                    addresses.add(address);

                    String deviceString="";
                    if(name==null || name.equals("")){
                        deviceString=address+ "- RSSI "+ rssi +"dBm";
                    }else {
                        deviceString=name+ "- RSSI "+ rssi +"dBm";
                    }

                    blueToothDevices.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    //handing onclick for button
    public void searchClicked(View view){
         statusTextView.setText("Searching...");
         searchButton.setEnabled(false);
         blueToothDevices.clear();
         addresses.clear();
         bluetoothAdapter.startDiscovery();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.listView);
        searchButton=findViewById(R.id.statusButton);
        statusTextView=findViewById(R.id.statusTextView);

        arrayAdapter =new ArrayAdapter(this, android.R.layout.simple_list_item_1,blueToothDevices);
        listView.setAdapter(arrayAdapter);


        //bluetoothAdapter and intentfilter to filter the action from the receiver
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        //registering receiver
        registerReceiver(broadcastReceiver,intentFilter);
    }
}