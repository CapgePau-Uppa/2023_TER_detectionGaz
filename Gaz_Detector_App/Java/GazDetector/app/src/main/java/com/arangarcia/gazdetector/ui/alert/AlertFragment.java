package com.arangarcia.gazdetector.ui.alert;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.RectF;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.arangarcia.gazdetector.R;
import com.arangarcia.gazdetector.RetrofitInterface;
import com.arangarcia.gazdetector.databinding.FragmentAlertBinding;
import com.arangarcia.gazdetector.sendResult;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlertFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentAlertBinding binding;
    private UsbManager mUsbManager;
    private UsbDevice mDevice = null;
    private UsbSerialDevice mSerial = null;
    private UsbDeviceConnection mConnection = null;
    private String ACTION_USB_PERMISSION = "permission";

    public TextView co2Concentration;
    private GraphView graph  = null;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://192.168.103.30:3000";

    public Spinner spinner;
    private ArrayList<Double> smokeDP;
    private ArrayList<Double> coDP;
    private ArrayList<Double> lpgDP;
    private String gazSelected = "Smoke";
    private Button btnReset;
    private Button btnAlert;
    private com.ortiz.touchview.TouchImageView imageViewPlan;
    ImageView markerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.arangarcia.gazdetector.ui.Sensor.SensorViewModel SensorViewModel =  new ViewModelProvider(this).get(com.arangarcia.gazdetector.ui.Sensor.SensorViewModel.class);

        binding = FragmentAlertBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //////////////// INIT BUTTONS ////////////////////////////

        btnAlert = (Button) root.findViewById(R.id.btnAlert);
        btnReset = (Button) root.findViewById(R.id.btnReset);

        imageViewPlan = root.findViewById(R.id.imageViewAlert);
        markerView = (ImageView) root.findViewById(R.id.AlertMarker);

        btnAlert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // Launching new Activity on selecting single List Item

                handleConfirmAlert();
                //Fragment fragment = null;
                //fragment = new AlertFragment();
                //replaceFragment(fragment);
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Log.d("Samuel_Alert","On va prendre les coordonnées du marker");
                float x = markerView.getX();
                float y = markerView.getY();

                Log.d("Samuel_Alert","X: " + x + "; Y: " + y);

                Log.d("Samuel_Alert","Et on exécute la fonction qui donne la position gps");
                ArrayList<Double> loc = posToLoc();
                Log.d("Samuel_Alert","Voilà la localisation : (lat: " + loc.get(0) + "; long: " + loc.get(1) + ")");
            }
        });

        //value for imviewplan.png
        imageViewPlan.setMinZoom(2);
        imageViewPlan.setZoom(2);

        imageViewPlan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Float x = motionEvent.getX();
                Float y = motionEvent.getY();
                Float xView = imageViewPlan.getX();
                Float yView = imageViewPlan.getY();
                Integer wView = imageViewPlan.getWidth();
                Integer hView = imageViewPlan.getHeight();

                //Log.d("Samuel_Plan", "wView: " + wView + "; hView: " + hView);

                if (x < 0 || y < 0 || x > wView || y > hView) {
                    return false;
                }

                //posTextView.setText("X: " + x.toString() + "; Y: " + y.toString());
                //Log.d("Samuel_Plan", "X: " + x.toString() + "; Y: " + y.toString());

                //ImageView markerView = (ImageView) getView().findViewById(R.id.imageViewMarker);
                markerView.setX(x + xView);
                markerView.setY(y + yView);
                markerView.setVisibility(View.VISIBLE);

                return true;
            }
        });


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


        spinner = root.findViewById(R.id.spAlert);
        initSpinner();

        //startUsbConnecting();
        //final TextView textView = binding.textSlideshow;
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.nav_host_fragment_content_main2, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
        public void onReceivedData(byte[] arg0) {
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

                //TextView text = getActivity().findViewById(R.id.textView5);
                //if (text != null) text.setText(data); ///////////////////////////////////////// DEBUG//////////////////

                //Toast.makeText(getActivity(), "|" + data + "|", Toast.LENGTH_LONG).show();

                if (data.contains("{") && data.contains("}")){

                    Log.d("sensor", "receiveddata:  " + data);
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
            JSONObject json = new JSONObject(data);
            if (!json.getString("smoke").equals("nan")  && !json.getString("smoke").equals("inf")){
                smokeDP.add(json.getDouble("smoke"));
            }

            if (!json.getString("CO").equals("nan")  && !json.getString("CO").equals("inf")){
                coDP.add(json.getDouble("CO"));
            }

            if (!json.getString("LPG").equals("nan") && !json.getString("LPG").equals("inf")){
                lpgDP.add(json.getDouble("LPG"));
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
        if (null == getActivity().findViewById(R.id.graphView)) {
            return;
        }
        graph = (GraphView) getActivity().findViewById(R.id.graphView);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> threshold = new LineGraphSeries<DataPoint>();
        threshold.setColor(Color.RED);

        float thresholdNumber = 0;
        if (gaz.equals("Smoke")){
            for (int i = 0; i<smokeDP.size(); i++){
                series.appendData(new DataPoint(i, smokeDP.get(i) ), false, 150, false);
            }
            thresholdNumber = 5000;
        }
        if (gaz.equals("CO")){
            for (int i = 0; i<coDP.size(); i++){
                series.appendData(new DataPoint(i, coDP.get(i) ), true, 150, false);
            }
            thresholdNumber = 50;
        }
        if (gaz.equals("LPG")){
            for (int i = 0; i<lpgDP.size(); i++){
                series.appendData(new DataPoint(i, lpgDP.get(i) ), true, 150, false);
            }
            thresholdNumber = 1000;
        }


        threshold.appendData(new DataPoint(0, thresholdNumber),false, 2,false);
        threshold.appendData(new DataPoint(smokeDP.size()-1, thresholdNumber),false, 2,false);
        //graph.setTitle("My Graph View");

        graph.removeAllSeries();
        graph.clearSecondScale();
        graph.addSeries(series);
        graph.addSeries(threshold);
    }

    private void initSpinner(){
        String[] planNames = {"UPPA", "CapGemini"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, planNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener( this );
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        gazSelected = text;

        if (!(graph == null)){
            graph.removeAllSeries();
            graph.clearSecondScale();

        }
        //handlePoints(text);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //test
    private void handleConfirmAlert() {
        HashMap<String, String> map = new HashMap<>();

        Log.d("backEnd", "start handleConfirm");

        map.put("latitude","latitude.toString()");
        map.put("longitude","longitude.toString()");
        map.put("danger", "todo");

        // Initialisation of the server connection

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<sendResult> call = retrofitInterface.executeAddAlert(map);

        call.enqueue(new Callback<sendResult>() {
            @Override
            public void onResponse(Call<sendResult> call, Response<sendResult> response) {

                if (response.code() == 400) {
                    Toast.makeText(getActivity(), "Alert already added", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 200) {
                    Toast.makeText(getActivity(), "Alert successfully added", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<sendResult> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public ArrayList<Double> posToLoc(){
        ArrayList<Double> loc = new ArrayList<>(2);

        ArrayList<Double> topLeft = new ArrayList<>(2);
        ArrayList<Double> topRight = new ArrayList<>(2);
        ArrayList<Double> botLeft = new ArrayList<>(2);
        ArrayList<Double> botRight = new ArrayList<>(2);

        ArrayList<Double> topLeftP = new ArrayList<>(2);
        ArrayList<Double> topRightP = new ArrayList<>(2);
        ArrayList<Double> botLeftP = new ArrayList<>(2);
        ArrayList<Double> botRightP = new ArrayList<>(2);

        RectF rect = imageViewPlan.getZoomedRect();

        topLeft.add(43.3162199);
        topLeft.add(-0.364762);
        topRight.add(43.316268);
        topRight.add(-0.3620184);
        botLeft.add(43.3137179);
        botLeft.add(-0.3650232);
        botRight.add(43.3130416);
        botRight.add(-0.3619866);

        topLeftP.add(topLeft.get(0)-(rect.top * (topLeft.get(0)-botLeft.get(0))));
        topLeftP.add(topLeft.get(1)+(rect.left * (topRight.get(1)-topLeft.get(1))));
        topRightP.add(topRight.get(0)-(rect.top * (topRight.get(0)-botRight.get(0))));
        topRightP.add(topRight.get(1)-((1-rect.right) * (topRight.get(1)-topLeft.get(1))));
        botLeftP.add(botLeft.get(0)+((1-rect.bottom) * (topLeft.get(0)-botLeft.get(0))));
        botLeftP.add(botLeft.get(1)+(rect.left * (botRight.get(1)-botLeft.get(1))));
        botRightP.add(botRight.get(0)+((1-rect.bottom) * (topRight.get(0)-botRight.get(0))));
        botRightP.add(botRight.get(1)-((1-rect.right) * (botRight.get(1)-botLeft.get(1))));

        double y = markerView.getY() - imageViewPlan.getY();
        double x = markerView.getX() - imageViewPlan.getX();

        Log.d("Samuel_Plan","It's on it!");
        loc.add(botLeftP.get(0) + ((imageViewPlan.getHeight() - y) / imageViewPlan.getHeight()) * (topRightP.get(0) - botLeftP.get(0)));
        loc.add(botLeftP.get(1) + (x / imageViewPlan.getWidth()) * (topRightP.get(1) - botLeftP.get(1)));
        return loc;
    }
}
