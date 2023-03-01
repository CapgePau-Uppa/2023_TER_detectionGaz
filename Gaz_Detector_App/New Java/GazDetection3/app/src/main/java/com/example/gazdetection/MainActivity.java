package com.example.gazdetection;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.gazdetection.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private UsbManager mUsbManager;
    private UsbDevice mDevice = null;
    private UsbSerialDevice mSerial = null;
    private UsbDeviceConnection mConnection = null;

    private String ACTION_USB_PERMISSION = "permission";

    public EditText receive_Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(broadcastReceiver, filter);

        binding.btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("o");
            }
        });

        binding.btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("x");
            }
        });
        binding.btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUsbConnecting();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void startUsbConnecting(){
        HashMap<String, UsbDevice> usbDevices = mUsbManager.getDeviceList();
        if (usbDevices.isEmpty()){
            Toast.makeText(MainActivity.this, "No devices identified" , Toast.LENGTH_LONG).show();
            return;
        }
        AtomicBoolean keep = new AtomicBoolean(true);
        usbDevices.forEach((key, value) -> {
            mDevice = value;
            int deviceVendorId = mDevice.getVendorId();
            //Toast.makeText(MainActivity.this, "vendorId" + deviceVendorId , Toast.LENGTH_LONG).show();

            if (deviceVendorId == 6790 ){
                //Toast.makeText(MainActivity.this, "test1", Toast.LENGTH_LONG).show();
                PendingIntent intent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_MUTABLE);

                mUsbManager.requestPermission(mDevice,intent);
                keep.set(false);
                Toast.makeText(MainActivity.this, "Connection Successful", Toast.LENGTH_LONG).show();

            }else {
                mConnection = null;
                mDevice = null;
                Toast.makeText(MainActivity.this, "Unable to connect"  , Toast.LENGTH_LONG).show();

            }
            if (!keep.get()){
                return;
            }
        });
    }

    private void sendData(String input){
        if (mSerial == null){
            return;
        }
        mSerial.write(input.getBytes());

        Toast.makeText(MainActivity.this, "Sending : " + input.getBytes() , Toast.LENGTH_LONG).show();
    }

    private void disconnect(){
        if (mSerial == null){
            Toast.makeText(MainActivity.this, "Port is null" , Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(MainActivity.this, "Disconnection" , Toast.LENGTH_LONG).show();
        mSerial.close();
    }

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0)
        {
            //Toast.makeText(MainActivity.this, "Callback Received"+arg0, Toast.LENGTH_SHORT).show();
            try {
                String data = new String(arg0, "UTF-8");
                data = data.substring(0, 30);
                data.concat("/n");
                receive_Data = findViewById(R.id.tvReceive);
                //Toast.makeText(MainActivity.this, "read : " + mSerial.read(mCallback), Toast.LENGTH_LONG).show();
                String finalData = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        receive_Data.setText(finalData);
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Exception:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    };
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Toast.makeText(MainActivity.this, "action : " + action, Toast.LENGTH_LONG).show();

            if (action.equals(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)){
                startUsbConnecting();
                Toast.makeText(MainActivity.this, "attaching", Toast.LENGTH_LONG).show();
                return;
            }else if (action.equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED)){
                disconnect();
                Toast.makeText(MainActivity.this, "disconnecting", Toast.LENGTH_LONG).show();
                return;
            }else if (!action.equals(ACTION_USB_PERMISSION)){

               return;
            }

            Boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
            if (!granted){
                Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_LONG).show();
                return;
            }

            mConnection = mUsbManager.openDevice(mDevice);
            mSerial = UsbSerialDevice.createUsbSerialDevice(mDevice, mConnection);
            if (mSerial == null){
                Toast.makeText(MainActivity.this, "Port is Null", Toast.LENGTH_LONG).show();
                return;
            }
            if (!mSerial.open()){
                Toast.makeText(MainActivity.this, "Port not open", Toast.LENGTH_LONG).show();
                return;
            }
            mSerial.setBaudRate(9600);
            mSerial.setDataBits(UsbSerialInterface.DATA_BITS_8);
            mSerial.setStopBits(UsbSerialInterface.STOP_BITS_1);
            mSerial.setParity(UsbSerialInterface.PARITY_NONE);
            mSerial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);

            try {
                mSerial.read(mCallback);
                Toast.makeText(MainActivity.this, "Read : "+mSerial.read(mCallback), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Exception in read:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(MainActivity.this, "Serial port connected", Toast.LENGTH_SHORT).show();


        };


    };

}