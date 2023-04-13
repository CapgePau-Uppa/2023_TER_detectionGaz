package com.arangarcia.gazdetector.ui.alert;

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

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlertFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentAlertBinding binding;
    private String BASE_URL = "http://192.168.185.30:3000";
    private UsbManager mUsbManager;
    private UsbDevice mDevice = null;
    private UsbSerialDevice mSerial = null;
    private UsbDeviceConnection mConnection = null;
    private String ACTION_USB_PERMISSION = "permission";

    public TextView co2Concentration;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;

    public Spinner spinner;
    private ArrayList<Double> smokeDP;
    private ArrayList<Double> coDP;
    private ArrayList<Double> lpgDP;
    private String dangerSelected = "Nominative";
    private Button btnReset;
    private Button btnAlert;
    private com.ortiz.touchview.TouchImageView imageViewPlan;
    ImageView markerView;
    private Double latitude;
    private ArrayList<Double> loc;

    // Coordinates of cap, UPPA in commentary
    private static Double[] topLeft = /*{43.3162199, -0.364762}*/ {43.3193276422, -0.3636125675};
    private static Double[] topRight = /*{43.316268, -0.3620184};*/ {43.3193276422, -0.3629366508};
    private static Double[] botLeft = /*{43.3137179, -0.3650232};*/ {43.3190690788, -0.3636125675};
    private static Double[] botRight = /*{43.3130416, -0.3619866};*/ {43.3190690788, -0.3629366508};

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
                double y = markerView.getY() - imageViewPlan.getY();
                double x = markerView.getX() - imageViewPlan.getX();

                Log.d("Samuel_Alert","X: " + x + "; Y: " + y);
                Log.d("Samuel_Alert","Et on exécute la fonction qui donne la position gps");

                loc = posToLoc(x, y);
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

                Log.d("Samuel_Alert","Et on exécute la fonction qui donne la position gps");

                loc = posToLoc(xView, yView);

                Log.d("Samuel_Alert","Voilà la localisation : (lat: " + loc.get(0) + "; long: " + loc.get(1) + ")");

                return true;
            }
        });

        spinner = root.findViewById(R.id.spAlert);
        initSpinner();

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

    String data = new String();
    /*
     * Receives the Database in html and fills alert tables
     */
    private void parseData(String data) {

    }




    private void initSpinner(){
        String[] dangerLevels = {"Nominative", "Important", "Urgent"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dangerLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener( this );
        dangerSelected = dangerLevels[0];
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        dangerSelected = text;

        //Toast.makeText(getActivity(), text+" level selected.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //test
    private void handleConfirmAlert() {
        HashMap<String, String> map = new HashMap<>();

        if (loc == null || loc.size() == 0){
            Toast.makeText(getActivity(), "Please give a location.", Toast.LENGTH_SHORT).show();
            return;
        }

        map.put("latitude",loc.get(0).toString());
        map.put("longitude",loc.get(1).toString());
        map.put("danger", dangerSelected);

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
                if (getActivity().equals(null)){
                    return;
                }
                if (response.code() == 400) {
                    Toast.makeText(getActivity(), "Alert already added", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 200) {
                    Toast.makeText(getActivity(), "Alert successfully added", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<sendResult> call, Throwable t) {
                if (getActivity() == null){
                    return;
                }
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public ArrayList<Double> posToLoc(double x, double y){
        ArrayList<Double> loc = new ArrayList<>(2);

        ArrayList<Double> topLeftP = new ArrayList<>(2);
        ArrayList<Double> topRightP = new ArrayList<>(2);
        ArrayList<Double> botLeftP = new ArrayList<>(2);
        ArrayList<Double> botRightP = new ArrayList<>(2);

        RectF rect = imageViewPlan.getZoomedRect();

        // needs commentary
        topLeftP.add(topLeft[0]-(rect.top * (topLeft[0]-botLeft[0])));
        topLeftP.add(topLeft[1]+(rect.left * (topRight[1]-topLeft[1])));
        topRightP.add(topRight[0]-(rect.top * (topRight[0]-botRight[0])));
        topRightP.add(topRight[1]-((1-rect.right) * (topRight[1]-topLeft[1])));
        botLeftP.add(botLeft[0]+((1-rect.bottom) * (topLeft[0]-botLeft[0])));
        botLeftP.add(botLeft[1]+(rect.left * (botRight[1]-botLeft[1])));
        botRightP.add(botRight[0]+((1-rect.bottom) * (topRight[0]-botRight[0])));
        botRightP.add(botRight[1]-((1-rect.right) * (botRight[1]-botLeft[1])));


        Log.d("Samuel_Plan","It's on it!");
        loc.add(botLeftP.get(0) + ((imageViewPlan.getHeight() - y) / imageViewPlan.getHeight()) * (topRightP.get(0) - botLeftP.get(0)));
        loc.add(botLeftP.get(1) + (x / imageViewPlan.getWidth()) * (topRightP.get(1) - botLeftP.get(1)));
        return loc;
    }
}
