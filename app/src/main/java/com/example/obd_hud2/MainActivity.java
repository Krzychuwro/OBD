package com.example.obd_hud2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Switch enableBtSwitch, visibleBtSwitch;
    ImageButton search_ImageButton;
    TextView myNameTextView;
    ListView devicesListView;

    private BluetoothAdapter BtAdapter;
    private Set<BluetoothDevice> pairedDevices;


    String deviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableBtSwitch = (Switch) findViewById(R.id.enableBtSwitch);
        visibleBtSwitch = (Switch) findViewById(R.id.visibleBtSwitch);
        search_ImageButton = (ImageButton) findViewById(R.id.search_ImageButton);
        myNameTextView = (TextView) findViewById(R.id.myNameTextView);
        devicesListView = (ListView) findViewById(R.id.devicesListView);

        myNameTextView.setText(getLocalBluetoothName());

        BtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        if (BtAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (BtAdapter.isEnabled()){
            enableBtSwitch.setChecked(true);
        }
        else{
            enableBtSwitch.setChecked(false);
        }

        enableBtSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    BtAdapter.disable();
                    Toast.makeText(MainActivity.this, "Turned off", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intentOn = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intentOn, 0);
                    Toast.makeText(MainActivity.this, "Turned on", Toast.LENGTH_SHORT).show();
                }
            }
        });


        visibleBtSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(getVisible, 1);
                Toast.makeText(MainActivity.this, "Bluetooth is visible", Toast.LENGTH_SHORT).show();
            }
        });

        search_ImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                list();
            }

        });

    }

    private void list() {
        pairedDevices = BtAdapter.getBondedDevices();
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final ArrayList list = new ArrayList();

        for (BluetoothDevice bt: pairedDevices){
            list.add(bt.getName());
        }

        Toast.makeText(this, "Showing devices", Toast.LENGTH_SHORT).show();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, list);
        devicesListView.setAdapter(adapter);

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                for (BluetoothDevice bt: pairedDevices){
                    if( bt.getName().equals(list.get(position).toString())){
                        deviceAddress = bt.getAddress();
                    }
                }

                // TODO save deviceAddress

            }
        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
        Toast.makeText(this, deviceAddress, Toast.LENGTH_SHORT).show();

    }

    public String getLocalBluetoothName(){
        if (BtAdapter == null){
            BtAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        String name = BtAdapter.getName();
        if (name == null){
            name = BtAdapter.getAddress();
        }
        return name;
    }
}
