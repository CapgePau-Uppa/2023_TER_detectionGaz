package com.example.gazdetector.ui.Sensor;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gazdetector.R;
import com.example.gazdetector.databinding.FragmentSensorBinding;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SensorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentSensorBinding binding;
    private UsbManager mUsbManager;
    private UsbDevice mDevice = null;
    private UsbSerialDevice mSerial = null;
    private UsbDeviceConnection mConnection = null;
    private String ACTION_USB_PERMISSION = "permission";

    public TextView co2Concentration;
    private GraphView graph  = null;

    public Spinner spinner;
    private ArrayList<Double> smokeDP;
    private ArrayList<Double> coDP;
    private ArrayList<Double> lpgDP;
    private String gazSelected = "Smoke";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SensorViewModel SensorViewModel =
                new ViewModelProvider(this).get(SensorViewModel.class);

        binding = FragmentSensorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ////////////////  INIT USB CONNECTION  ////////////////////
        mUsbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        getActivity().registerReceiver(broadcastReceiver, filter);

        coDP = new ArrayList<Double>();
        lpgDP = new ArrayList<Double>();
        smokeDP = new ArrayList<Double>();
        Log.d("","test");


        spinner = root.findViewById(R.id.spGaz);
        initSpinner();

        startUsbConnecting();
        //final TextView textView = binding.textSlideshow;
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void startUsbConnecting(){
        HashMap<String, UsbDevice> usbDevices = mUsbManager.getDeviceList();
        if (usbDevices.isEmpty()){
            Toast.makeText(getActivity(), "No devices identified" , Toast.LENGTH_LONG).show();
            return;
        }
        AtomicBoolean keep = new AtomicBoolean(true);
        usbDevices.forEach((key, value) -> {
            mDevice = value;
            int deviceVendorId = mDevice.getVendorId();
            //Toast.makeText(MainActivity.this, "vendorId" + deviceVendorId , Toast.LENGTH_LONG).show();

            if (true /*deviceVendorId == 6790*/ ){ // checking the ID of the Arduino board
                //Toast.makeText(MainActivity.this, "test1", Toast.LENGTH_LONG).show();
                PendingIntent intent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_MUTABLE);

                mUsbManager.requestPermission(mDevice,intent);
                keep.set(false);
                //Toast.makeText(getActivity(), "Connection Successful", Toast.LENGTH_LONG).show();

            }else {
                mConnection = null;
                mDevice = null;
                Toast.makeText(getActivity(), "Unable to connect"  , Toast.LENGTH_LONG).show();

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

        Toast.makeText(getActivity(), "Sending : " + input.getBytes() , Toast.LENGTH_LONG).show();
    }

    private void disconnect(){
        if (mSerial == null){
            Toast.makeText(getActivity(), "Port is null" , Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(getActivity(), "Disconnection" , Toast.LENGTH_LONG).show();
        mSerial.close();
    }

    String data = new String();
    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0)
        {
            //Toast.makeText(MainActivity.this, "Callback Received"+arg0, Toast.LENGTH_SHORT).show();
            try {

                if (data.length()>60){
                    data = data.substring(0, 60);
                }

                String received = new String(arg0, "UTF-8");
                //Log.d("sensor", "receiveddata:  " + received + " contains ? " + (!received.contains("{") && !data.contains("{")));
                if (!received.contains("{")){
                    if (!data.contains("{")){
                        return;
                    }
                } else {
                    if (data.contains("{")){
                        return;
                    }
                }
                data = data.concat(received);

                Log.d("sensor", "receiveddata:  " + data);
                //TextView text = getActivity().findViewById(R.id.textView5);
                //if (text != null) text.setText(data); ///////////////////////////////////////// DEBUG//////////////////

                //Toast.makeText(getActivity(), "|" + data + "|", Toast.LENGTH_LONG).show();

                if (data.contains("{") && data.contains("}")){
                    parseData(data);
                    handlePoints(gazSelected);
                    data = "";
                }

                //data.concat("/n");
                //receive_Data = getActivity().findViewById(R.id.tvReceive);

                //Toast.makeText(getActivity(), "read parsed : " + data, Toast.LENGTH_LONG).show();

                //Toast.makeText(getActivity(), "read parsed : " + mSerial.read(mCallback), Toast.LENGTH_LONG).show();


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                //Toast.makeText(MainActivity.this, "Exception:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    };
    /*
     * Receives a string, parse jsons and fill data tables
     */
    private void parseData(String data) {
        //data = data.replace("}{", "}}{{");
        //String Pattern = getString(R.string.patern);

        //String[] jsons = data.split(Pattern) ;

        try {
            Log.d("sensor", "parseData:  " + data);
            JSONObject json = new JSONObject(data);
            Log.d("sensor", "parseData: smoke : " + json.getString("smoke") + " inf? " + (json.getString("smoke").equals("nan")  || json.getString("smoke").equals("inf")));
            if (!json.getString("smoke").equals("nan")  && !json.getString("smoke").equals("inf")){
                smokeDP.add(json.getDouble("smoke"));
                Log.d("sensor", "added to smokeDP  " + smokeDP.get(0) + " is " + json.getDouble("smoke") );
            }

            if (!json.getString("CO").equals("nan")  && !json.getString("CO").equals("inf")){
                coDP.add(json.getDouble("CO"));
                Log.d("sensor", "added to coDP  " + coDP.get(0) );
            }

            if (json.getString("LPG").equals("nan") && json.getString("LPG").equals("inf")){
                lpgDP.add(json.getDouble("LPG"));
                Log.d("sensor", "added to lpgDP  " + lpgDP.get(0) );
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }/*
        for (int i = 0; i<jsons.length; i++) {

        }*/

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Toast.makeText(MainActivity.this, "action : " + action, Toast.LENGTH_LONG).show();

            if (action.equals(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)){
                startUsbConnecting();
                Toast.makeText(getActivity(), "attaching", Toast.LENGTH_LONG).show();
                return;
            }else if (action.equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED)){
                disconnect();
                Toast.makeText(getActivity(), "disconnecting", Toast.LENGTH_LONG).show();
                return;
            }else if (!action.equals(ACTION_USB_PERMISSION)){

                return;
            }

            Boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
            if (!granted){
                Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_LONG).show();
                return;
            }

            mConnection = mUsbManager.openDevice(mDevice);
            mSerial = UsbSerialDevice.createUsbSerialDevice(mDevice, mConnection);
            if (mSerial == null){
                Toast.makeText(getActivity(), "Port is Null", Toast.LENGTH_LONG).show();
                return;
            }
            if (!mSerial.open()){
                Toast.makeText(getActivity(), "Port not open", Toast.LENGTH_LONG).show();
                return;
            }
            mSerial.setBaudRate(9600);
            mSerial.setDataBits(UsbSerialInterface.DATA_BITS_8);
            mSerial.setStopBits(UsbSerialInterface.STOP_BITS_1);
            mSerial.setParity(UsbSerialInterface.PARITY_NONE);
            mSerial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);

            try {
                mSerial.read(mCallback);
                //Toast.makeText(getActivity(), "Read broadcast : "+mSerial.read(mCallback), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Exception in read:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            //Toast.makeText(getActivity(), "Serial port connected", Toast.LENGTH_SHORT).show();
        }
    };
/*
 * create dataPoint list and updates the graph
 */
    public void handlePoints(String gaz){
        if (smokeDP.size() == 0 && lpgDP.size() == 0 && coDP.size() == 0){
            Log.d("sensor", "handlePoints: vide");
            return ;
        }
        graph = (GraphView) getActivity().findViewById(R.id.graphView);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        if (gaz.equals("Smoke")){
            for (int i = 0; i<smokeDP.size(); i++){
                series.appendData(new DataPoint(i, smokeDP.get(i) ), false, 15, false);
            }
        }
        if (gaz.equals("CO")){
            for (int i = 0; i<coDP.size(); i++){
                series.appendData(new DataPoint(i, coDP.get(i) ), false, 15, false);
            }
        }
        if (gaz.equals("LPG")){
            for (int i = 0; i<lpgDP.size(); i++){
                series.appendData(new DataPoint(i, lpgDP.get(i) ), false, 15, false);
            }
        }
        Log.d("sensor", "handlePoints: " + smokeDP.get(0));


        //graph.setTitle("My Graph View");
        graph.addSeries(series);
    }

    private void initSpinner(){
        String[] gazNames = {"Smoke", "LPG", "CO"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, gazNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        gazSelected = text;
        handlePoints(text);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}