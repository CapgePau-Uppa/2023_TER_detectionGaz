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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SensorFragment extends Fragment {

    private FragmentSensorBinding binding;
    private UsbManager mUsbManager;
    private UsbDevice mDevice = null;
    private UsbSerialDevice mSerial = null;
    private UsbDeviceConnection mConnection = null;
    private String ACTION_USB_PERMISSION = "permission";

    public TextView co2Concentration;
    private GraphView graph  = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SensorViewModel SensorViewModel =
                new ViewModelProvider(this).get(SensorViewModel.class);

        binding = FragmentSensorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mUsbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        getActivity().registerReceiver(broadcastReceiver, filter);


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
                Toast.makeText(getActivity(), "Connection Successful", Toast.LENGTH_LONG).show();

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
                //receive_Data = getActivity().findViewById(R.id.tvReceive);

                Toast.makeText(getActivity(), "read : " + mSerial.read(mCallback), Toast.LENGTH_LONG).show();


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                //Toast.makeText(MainActivity.this, "Exception:"+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Read : "+mSerial.read(mCallback), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Exception in read:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(getActivity(), "Serial port connected", Toast.LENGTH_SHORT).show();
        }
    };

    public void handleGraph(){
        graph = (GraphView) getActivity().findViewById(R.id.graphView);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        //graph.setTitle("My Graph View");
        graph.addSeries(series);
    }
}
