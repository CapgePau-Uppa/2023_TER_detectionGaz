package com.arangarcia.gazdetector;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.arangarcia.gazdetector.databinding.ActivityMain2Binding;
import com.felhr.usbserial.UsbSerialDevice;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class Main2Activity extends AppCompatActivity {

    private static final int PERMISSION_FINE_LOCATION = 99;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMain2Binding binding;

    private UsbManager mUsbManager;
    private UsbDevice mDevice = null;
    private UsbSerialDevice mSerial = null;
    private UsbDeviceConnection mConnection = null;
    private String ACTION_USB_PERMISSION = "permission";

    private float latitude;
    private float longitude;


    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    public EditText receive_Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(myToolbar);
        myToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_tab, R.id.nav_plan, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(broadcastReceiver, filter);
        */
        View v = null;

        /*v.findViewById(R.id.btnClean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { cleanTv();}
        });
        v.findViewById(R.id.btnOn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("o");
            }
        });

        v.findViewById(R.id.btnOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("x");
            }
        });
        v.findViewById(R.id.btnDisconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
        v.findViewById(R.id.btnConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUsbConnecting();
            }
        });*/

        updateGPS();
    }// end Create

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
/*
    private void cleanTv() {
        receive_Data = findViewById(R.id.tvReceive);
        receive_Data.setText("");
    }
*/
    /*
    private void startUsbConnecting(){
        HashMap<String, UsbDevice> usbDevices = mUsbManager.getDeviceList();
        if (usbDevices.isEmpty()){
            Toast.makeText(Main2Activity.this, "No devices identified" , Toast.LENGTH_LONG).show();
            return;
        }
        AtomicBoolean keep = new AtomicBoolean(true);
        usbDevices.forEach((key, value) -> {
            mDevice = value;
            int deviceVendorId = mDevice.getVendorId();
            //Toast.makeText(Main2Activity.this, "vendorId" + deviceVendorId , Toast.LENGTH_LONG).show();

            if (true deviceVendorId == 6790 ){
                Toast.makeText(Main2Activity.this, "test1", Toast.LENGTH_LONG).show();
                PendingIntent intent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_MUTABLE);

                mUsbManager.requestPermission(mDevice,intent);
                keep.set(false);
                Toast.makeText(Main2Activity.this, "Connection Successful", Toast.LENGTH_LONG).show();

            }else {
                mConnection = null;
                mDevice = null;
                Toast.makeText(Main2Activity.this, "Unable to connect"  , Toast.LENGTH_LONG).show();

            }
            if (!keep.get()){
                return;
            }
        });
    }*/
/*
    private void sendData(String input){
        if (mSerial == null){
            return;
        }
        mSerial.write(input.getBytes());

        Toast.makeText(Main2Activity.this, "Sending : " + input.getBytes() , Toast.LENGTH_LONG).show();
    }*/
/*
    private void disconnect(){
        if (mSerial == null){
            Toast.makeText(Main2Activity.this, "Port is null" , Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(Main2Activity.this, "Disconnection" , Toast.LENGTH_LONG).show();
        mSerial.close();
    }*/
/*
    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0)
        {
            //Toast.makeText(MainActivity.this, "Callback Received"+arg0, Toast.LENGTH_SHORT).show();
            try {
                String data = new String(arg0, "UTF-8");
                if (data.length()>30){
                    data = data.substring(0, 30);
                }
                data.concat("/n");

                receive_Data = findViewById(R.id.tvReceive);

                Toast.makeText(MainActivity.this, "read : " + mSerial.read(mCallback), Toast.LENGTH_LONG).show();
                String finalData = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        receive_Data.setText(receive_Data.getText() + finalData);
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                //Toast.makeText(MainActivity.this, "Exception:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    };
    */
    /*
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Toast.makeText(Main2Activity.this, "action : " + action, Toast.LENGTH_LONG).show();

            if (action.equals(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)){
                startUsbConnecting();
                Toast.makeText(Main2Activity.this, "attaching", Toast.LENGTH_LONG).show();
                return;
            }else if (action.equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED)){
                disconnect();
                Toast.makeText(Main2Activity.this, "disconnecting", Toast.LENGTH_LONG).show();
                return;
            }else if (!action.equals(ACTION_USB_PERMISSION)){

                return;
            }

            Boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
            if (!granted){
                Toast.makeText(Main2Activity.this, "Permission not granted", Toast.LENGTH_LONG).show();
                return;
            }

            mConnection = mUsbManager.openDevice(mDevice);
            mSerial = UsbSerialDevice.createUsbSerialDevice(mDevice, mConnection);
            if (mSerial == null){
                Toast.makeText(Main2Activity.this, "Port is Null", Toast.LENGTH_LONG).show();
                return;
            }
            if (!mSerial.open()){
                Toast.makeText(Main2Activity.this, "Port not open", Toast.LENGTH_LONG).show();
                return;
            }
            mSerial.setBaudRate(9600);
            mSerial.setDataBits(UsbSerialInterface.DATA_BITS_8);
            mSerial.setStopBits(UsbSerialInterface.STOP_BITS_1);
            mSerial.setParity(UsbSerialInterface.PARITY_NONE);
            mSerial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);

            try {
                mSerial.read(mCallback);
                Toast.makeText(Main2Activity.this, "Read : "+mSerial.read(mCallback), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(Main2Activity.this, "Exception in read:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(Main2Activity.this, "Serial port connected", Toast.LENGTH_SHORT).show();


        };


    };*/

    private void updateGPS(){
        //get permission from user
        //get the current location from the fused client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Main2Activity.this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //updateUIValues(location);
                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void updateUIValues(Location location) {
        // Update all text Objects
        Toast.makeText(Main2Activity.this, "Longitude : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        Toast.makeText(Main2Activity.this, "Latitude : " + location.getLatitude(), Toast.LENGTH_SHORT).show();
    }

}