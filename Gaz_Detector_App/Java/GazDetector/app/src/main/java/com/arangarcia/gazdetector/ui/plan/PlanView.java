package com.arangarcia.gazdetector.ui.plan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
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
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.arangarcia.gazdetector.R;
import com.arangarcia.gazdetector.RetrofitInterface;
import com.arangarcia.gazdetector.alertPojo;
import com.arangarcia.gazdetector.databinding.FragmentPlanBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlanView extends Fragment implements AdapterView.OnItemSelectedListener {
    private String BASE_URL = "http://192.168.185.30:3000";
    private static final int PERMISSION_FINE_LOCATION = 99;
    public static final int DEFAULT_INTERVAL_MILLIS = 1000;
    public static final int MIN_UPDATE_INTERVAL_MILLIS = 200;
    private FragmentPlanBinding binding;
    private Location location;
    private com.ortiz.touchview.TouchImageView imageViewPlan;
    private TextView posTextView;
    public Spinner spinner;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    //Config file for settings related to FusedLocationProviderClient
    private LocationRequest.Builder locationRequestBuilder;
    private LocationCallback locationCallBack;
    //Google's API for location services
    private FusedLocationProviderClient fusedLocationProviderClient;
    //delta modified with the test buttons
    private double deltaLat;
    private double deltaLong;
    private Button btnLat;
    private Button btnLong;

    // Coordinates of cap, UPPA in commentary
    private static Double[] topLeft = /*{43.3162199, -0.364762}*/ {43.3193276422, -0.3636125675};
    private static Double[] topRight = /*{43.316268, -0.3620184};*/ {43.3193276422, -0.3629366508};
    private static Double[] botLeft = /*{43.3137179, -0.3650232};*/ {43.3190690788, -0.3636125675};
    private static Double[] botRight = /*{43.3130416, -0.3619866};*/ {43.3190690788, -0.3629366508};

    private ImageView markerView;
    private ArrayList<ImageView> clonedMarkers;
    private boolean alertShown;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentPlanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ///////////////// config.json file /////////////////////////
        try {
            // Open the JSON file located in the folder "assets"
            AssetManager manager = getContext().getAssets();
            InputStream stream = null;
            stream = manager.open("config.json");

            // Read the content
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            String json = new String(buffer, "UTF-8");

            // Convert the content into Java Objects
            JSONObject config = new JSONObject(json);
            JSONArray maps = config.getJSONArray("maps");

            // map 0 capgemini, map 1 uppa
            JSONObject curMap = maps.getJSONObject(0);
            JSONObject corners = curMap.getJSONObject("corners");

            JSONObject jsTopLeft = corners.getJSONObject("topLeft");
            topLeft = new Double[]{jsTopLeft.getDouble("latitude"), jsTopLeft.getDouble("longitude")};

            JSONObject jsTopRight = corners.getJSONObject("topRight");
            topRight = new Double[]{jsTopRight.getDouble("latitude"), jsTopRight.getDouble("longitude")};

            Log.d("Samuel_Plan",botRight[0].toString() + "; " + botRight[1].toString());
            JSONObject jsBotLeft = corners.getJSONObject("botLeft");
            botLeft = new Double[]{jsBotLeft.getDouble("latitude"), jsBotLeft.getDouble("longitude")};

            JSONObject jsBotRight = corners.getJSONObject("botRight");
            botRight = new Double[]{jsBotRight.getDouble("latitude"), jsBotRight.getDouble("longitude")};
            Log.d("Samuel_Plan",botRight[0].toString() + "; " + botRight[1].toString());

            // Close the file
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        imageViewPlan = root.findViewById(R.id.imageViewPlan);
        posTextView = root.findViewById(R.id.posTextView);

        alertShown = false;
        clonedMarkers = new ArrayList<>();

        ///////////// INIT LOCATION REQUESTS ///////////
        locationRequestBuilder = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, DEFAULT_INTERVAL_MILLIS);
        locationRequestBuilder.setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL_MILLIS);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                updateGPS();
                location = locationResult.getLastLocation();
                posTextView.setText("Lat: " + location.getLatitude() + "; Long: " + location.getLongitude());
                Log.d("Samuel_Plan", location.toString());


                getAlarms();
            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        startLocationUpdates();


        ////////////// INIT ZOOM ///////////////
        Drawable img = imageViewPlan.getDrawable();


        // init the zoom and limit it
        imageViewPlan.setZoom(2);
        imageViewPlan.setMinZoom(2);

        /////////// INIT TOUCHLISTENER ///////////////////
        imageViewPlan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Float x = motionEvent.getX();
                Float y = motionEvent.getY();
                Float xView = imageViewPlan.getX();
                Float yView = imageViewPlan.getY();
                Integer wView = imageViewPlan.getWidth();
                Integer hView = imageViewPlan.getHeight();

                // If the touch get out of the image, no need to move the marker
                if (x < 0 || y < 0 || x > wView || y > hView) {
                    return false;
                }

                markerView = (ImageView) getView().findViewById(R.id.imageViewMarker);
                markerView.setX(x + xView);
                markerView.setY(y + yView);
                markerView.setVisibility(View.VISIBLE);


                return true;
            }
        });

        ////////////////// INIT DELTAS BTN /////////////////
        deltaLong = 0;
        deltaLat = 0;

        btnLat = (Button) root.findViewById(R.id.btnLat);
        btnLong = (Button) root.findViewById(R.id.btnLong);

        btnLat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deltaLat+=0.0001;
            }
        });

        btnLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deltaLong+=0.0001;
            }
        });

        //////////// INIT SPINNER ///////////////
        spinner = root.findViewById(R.id.spAlert);
        initSpinner();
        updateGPS();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initSpinner() {
        String[] planNames = {"UPPA", "CapGemini"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, planNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener( this );
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Samuel_Plan", "We're missing the persmissions");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequestBuilder.build(), locationCallBack, null);
        updateGPS();
    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private boolean isOnPlan(){

        if(location == null){
            posTextView.setText("no position");
            Log.d("Samuel_Plan","Location not found"); // TODO -------------------------------------------------------------------
            return false;
        }

        if(location.getLatitude() > botLeft[0] && location.getLatitude() < topRight[0] &&
            location.getLongitude() > botLeft[1] && location.getLongitude() < topRight[1]){
            return true;
        }

        return false;
    }

    //need commentary
    public ArrayList<Double> posOnPlan(double lat, double longi){
        Log.d("Samuel_Plan", String.valueOf(location.getAccuracy()));
        ArrayList<Double> pos = new ArrayList<>(2);

        ArrayList<Double> topLeftZoom = new ArrayList<>(2);
        ArrayList<Double> topRightZoom = new ArrayList<>(2);
        ArrayList<Double> botLeftZoom = new ArrayList<>(2);
        ArrayList<Double> botRightZoom = new ArrayList<>(2);

        RectF rect = imageViewPlan.getZoomedRect();

        // get the corners of the zoomed map
        topLeftZoom.add(topLeft[0]-(rect.top * (topLeft[0]-botLeft[0])));
        topLeftZoom.add(topLeft[1]+(rect.left * (topRight[1]-topLeft[1])));
        topRightZoom.add(topRight[0]-(rect.top * (topRight[0]-botRight[0])));
        topRightZoom.add(topRight[1]-((1-rect.right) * (topRight[1]-topLeft[1])));
        botLeftZoom.add(botLeft[0]+((1-rect.bottom) * (topLeft[0]-botLeft[0])));
        botLeftZoom.add(botLeft[1]+(rect.left * (botRight[1]-botLeft[1])));
        botRightZoom.add(botRight[0]+((1-rect.bottom) * (topRight[0]-botRight[0])));
        botRightZoom.add(botRight[1]-((1-rect.right) * (botRight[1]-botLeft[1])));

        if(lat >= botLeftZoom.get(0) && lat <= topRightZoom.get(0) &&
                longi >= botLeftZoom.get(1) && longi <= topRightZoom.get(1)){
            pos.add(imageViewPlan.getHeight() - ((lat - botLeftZoom.get(0)) / (topRightZoom.get(0) - botLeftZoom.get(0))) * imageViewPlan.getHeight());
            pos.add(((longi - botLeftZoom.get(1)) / (topRightZoom.get(1) - botLeftZoom.get(1))) * imageViewPlan.getWidth());

            return pos;
        }

        pos.add(0.0);
        pos.add(0.0);
        return pos;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void displayLocation() {
        if(location == null){
            posTextView.setText("no position");
        }
        else {
            posTextView.setText(this.location.toString());
        }
    }

    private void updateGPS() {
        if (getActivity().equals(null)){
            return;
        }

        //get permission from user
        //get the current location from the fused client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    setLocation(location);
                    updatePosition(location);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

        displayLocation();
    }

    public void updatePosition(Location location){
        //unused class
        com.arangarcia.gazdetector.ui.plan.Plan plan = new com.arangarcia.gazdetector.ui.plan.Plan(
                getActivity(),
                R.id.imageViewPlan,
                topLeft[0],
                topLeft[1],
                botRight[0],
                botRight[1]
        );/*43., -0.3748915662826034,44.313623593101546, -0.3524817214585103*/ /*UPPA*/

        // Check if the position is on the map
        if (isOnPlan()) {
            // Get coordinates on the map
            double lat = location.getLatitude() + deltaLat;
            double longi = location.getLongitude() + deltaLong;
            ArrayList<Double> pos = posOnPlan(lat, longi);


            int x = (int) (pos.get(1).intValue()+imageViewPlan.getX());
            int y = (int) (pos.get(0).intValue()+imageViewPlan.getY());

            // Display the coordonates on the map
            ImageView markerView = (ImageView) getView().findViewById(R.id.imageViewMarker);
            markerView.setX(x);
            markerView.setY(y);
            markerView.setVisibility(View.VISIBLE);
        } else {
            // Current location isn't on the map
            ImageView markerView = (ImageView) getView().findViewById(R.id.imageViewMarker);
            markerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        if (text.equals("UPPA")){

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    void getAlarms(){

        ConstraintLayout parentLayout = getView().findViewById(R.id.plan_view_id);

        for (int i = 0; i < clonedMarkers.size(); i++) {
            parentLayout.removeView(clonedMarkers.get(i));

        }
        HashMap<String, String> map = new HashMap<>();



        // Initialisation of the server connection

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<ArrayList<alertPojo>> call = retrofitInterface.executeGetAlerts(map);

        call.enqueue(new Callback<ArrayList<alertPojo>>() {
            @Override
            public void onResponse(Call<ArrayList<alertPojo>> call, Response<ArrayList<alertPojo>> response) {

                if (response.code() == 400) {
                    //Toast.makeText(getActivity(), "Alert already added", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 200) {
                    //Log.d("getAlarms","body : " + response.body().toString());
                    ArrayList<alertPojo> alarms = response.body();
                    clonedMarkers = new ArrayList<>();

                    ImageView markerView = (ImageView) getView().findViewById(R.id.imageViewMarker);

                    Boolean isDangerous = false;
                    Log.d("getAlarms","size : " + alarms.size());
                    for (int i = 0; i < alarms.size(); i++) {
                        Log.d("getAlarms","danger : " + alarms.get(i).getDanger());
                        if(alarms.get(i).getDanger().equals("Urgent")) {
                            isDangerous = true;

                            Log.d("getAlarms","Confirmed");
                        }

                        Log.d("getAlarms","body : " + alarms.get(i).getLatitude().toString());
                        clonedMarkers.add(new ImageView(markerView.getContext()));
                        clonedMarkers.get(i).setImageDrawable(markerView.getDrawable());
                        clonedMarkers.get(i).setLayoutParams(markerView.getLayoutParams());

                        //Float xView = 0.0;//imageViewPlan.getX();
                        //Float yView = 0.0;//imageViewPlan.getY();

                        Double longitude = Double.parseDouble(alarms.get(i).getLongitude());
                        Double latitude = Double.parseDouble(alarms.get(i).getLatitude());

                        ArrayList<Double> localisation =  posOnPlan(latitude, longitude);
                        clonedMarkers.get(i).setX(localisation.get(1).floatValue()  /*+xView*/);
                        clonedMarkers.get(i).setY(localisation.get(0).floatValue()  /*+ yView*/);
                        clonedMarkers.get(i).setVisibility(View.VISIBLE);

                        View rootView = binding.getRoot();
                        ViewGroup parentView = (ViewGroup) rootView.findViewById(R.id.plan_view_id);
                        parentView.addView(clonedMarkers.get(i));

                        Log.d("getAlarms","cloned" );
                    }

                    if (isDangerous && !alertShown){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Dangerous gaz leak detected!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // do something when the OK button is clicked
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        alertShown = true;
                    }

                    //Toast.makeText(getActivity(), "body : " + response.body().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<alertPojo>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}