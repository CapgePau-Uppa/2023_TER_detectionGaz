package com.arangarcia.gazdetector.ui.plan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.arangarcia.gazdetector.R;
import com.arangarcia.gazdetector.databinding.FragmentPlanBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class PlanView extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final int PERMISSION_FINE_LOCATION = 99;
    public static final int DEFAULT_INTERVAL_MILLIS = 1000;
    public static final int MIN_UPDATE_INTERVAL_MILLIS = 200;
    private FragmentPlanBinding binding;
    private Location location;
    private com.ortiz.touchview.TouchImageView imageViewPlan;
    private TextView posTextView;

    public Spinner spinner;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentPlanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageViewPlan = root.findViewById(R.id.imageViewPlan);
        posTextView = root.findViewById(R.id.posTextView);

        //init the locationRequest
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
            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        startLocationUpdates();
        /* code for zooming*/
        Drawable img = imageViewPlan.getDrawable();
        Log.d("Samuel_Plan", "width: " + ((Integer) img.getMinimumWidth()).toString());
        Log.d("Samuel_Plan", "height: " + ((Integer) img.getMinimumHeight()).toString());


        //value for imviewplan.png
        imageViewPlan.setMinZoom(2);
        imageViewPlan.setZoom(2);

        imageViewPlan.setOnTouchImageViewListener(new com.ortiz.touchview.TouchImageView.OnTouchImageViewListener() {
            @Override
            public void onMove() {

                //posTextView.setText(imageViewPlan.getZoomedRect().toString());
                //Log.d("Samuel_Plan", "J'ai touché" + imageViewPlan.getCurrentZoom());

            }
        });

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

                ImageView markerView = (ImageView) getView().findViewById(R.id.imageViewMarker);
                markerView.setX(x + xView);
                markerView.setY(y + yView);
                markerView.setVisibility(View.VISIBLE);

                return true;
            }
        });

        //init the test deltas
        deltaLong = 0;
        deltaLat = 0;

        btnLat = (Button) root.findViewById(R.id.btnLat);
        btnLong = (Button) root.findViewById(R.id.btnLong);

        btnLat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deltaLat+=0.00001;
            }
        });

        btnLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deltaLong+=0.00001;
            }
        });


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
        ArrayList<Double> topLeft = new ArrayList<>(2);
        ArrayList<Double> topRight = new ArrayList<>(2);
        ArrayList<Double> botLeft = new ArrayList<>(2);
        ArrayList<Double> botRight = new ArrayList<>(2);

        //1 for Newton, 2 for UPPA
        int chooseCoord = 1;

        if(chooseCoord == 1){

            //for Newton
            topLeft.add(43.3193045);
            topLeft.add(-0.3637196);
            topRight.add(43.3192893);
            topRight.add(-0.3633025);
            botLeft.add(43.3190271);
            botLeft.add(-0.3636341);
            botRight.add(43.3190975);
            botRight.add(-0.3629909);
        }
        if(chooseCoord == 2){

            //for uppa
            topLeft.add(43.3162199);
            topLeft.add(-0.364762);
            topRight.add(43.316268);
            topRight.add(-0.3620184);
            botLeft.add(43.3137179);
            botLeft.add(-0.3650232);
            botRight.add(43.3130416);
            botRight.add(-0.3619866);
        }

        if(location.getLatitude() > botLeft.get(0) && location.getLatitude() < topRight.get(0) &&
            location.getLongitude() > botLeft.get(1) && location.getLongitude() < topRight.get(1)){
            return true;
        }

        return false;
    }

    public ArrayList<Double> posOnPlan(){
        Log.d("Samuel_Plan", String.valueOf(location.getAccuracy()));
        ArrayList<Double> pos = new ArrayList<>(2);

        ArrayList<Double> topLeft = new ArrayList<>(2);
        ArrayList<Double> topRight = new ArrayList<>(2);
        ArrayList<Double> botLeft = new ArrayList<>(2);
        ArrayList<Double> botRight = new ArrayList<>(2);

        ArrayList<Double> topLeftP = new ArrayList<>(2);
        ArrayList<Double> topRightP = new ArrayList<>(2);
        ArrayList<Double> botLeftP = new ArrayList<>(2);
        ArrayList<Double> botRightP = new ArrayList<>(2);

        RectF rect = imageViewPlan.getZoomedRect();

        topLeft.add(43.3193045);
        topLeft.add(-0.3637196);
        topRight.add(43.3192893);
        topRight.add(-0.3633025);
        botLeft.add(43.3190271);
        botLeft.add(-0.3636341);
        botRight.add(43.3190975);
        botRight.add(-0.3629909);

        topLeftP.add(topLeft.get(0)-(rect.top * (topLeft.get(0)-botLeft.get(0))));
        topLeftP.add(topLeft.get(1)+(rect.left * (topRight.get(1)-topLeft.get(1))));
        topRightP.add(topRight.get(0)-(rect.top * (topRight.get(0)-botRight.get(0))));
        topRightP.add(topRight.get(1)-((1-rect.right) * (topRight.get(1)-topLeft.get(1))));
        botLeftP.add(botLeft.get(0)+((1-rect.bottom) * (topLeft.get(0)-botLeft.get(0))));
        botLeftP.add(botLeft.get(1)+(rect.left * (botRight.get(1)-botLeft.get(1))));
        botRightP.add(botRight.get(0)+((1-rect.bottom) * (topRight.get(0)-botRight.get(0))));
        botRightP.add(botRight.get(1)-((1-rect.right) * (botRight.get(1)-botLeft.get(1))));

        double lat = location.getLatitude() + deltaLat;
        double longi = location.getLongitude() + deltaLong;

        if(lat < botLeftP.get(0)){
            Log.d("Samuel_Plan","cond1 isnot ok: " + lat + "<" + botLeftP.get(0));
        }
        if(lat > topRightP.get(0)){
            Log.d("Samuel_Plan","cond2 isnot ok: " + lat + ">" + topRightP.get(0));
        }
        if(longi < botLeftP.get(1)){
            Log.d("Samuel_Plan","cond3 isnot ok: " + longi + "<" + botLeftP.get(1));
        }
        if(longi > topRightP.get(1)){
            Log.d("Samuel_Plan","cond4 isnot ok: " + longi + ">" + topRightP.get(1));
        }

        if(lat >= botLeftP.get(0) && lat <= topRight.get(0) &&
                longi >= botLeftP.get(1) && longi <= topRightP.get(1)){
            Log.d("Samuel_Plan","It's on it!");
            pos.add(imageViewPlan.getHeight() - ((lat - botLeftP.get(0)) / (topRightP.get(0) - botLeftP.get(0))) * imageViewPlan.getHeight());
            pos.add(((longi - botLeftP.get(1)) / (topRightP.get(1) - botLeftP.get(1))) * imageViewPlan.getWidth());
            //Toast.makeText(getActivity(), "La position est : X: " + pos.get(1) + "; Y: "+pos.get(0),Toast.LENGTH_SHORT).show();
            return pos;
        }
        Log.d("Samuel_Plan","It isn't on it!");

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
            Log.d("Samuel_Plan","Location not found");
        }
        else {
            posTextView.setText(this.location.toString());
            Log.d("Samuel_Plan","Location found");
        }
    }

    private void updateGPS() {
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
        double longTopLeft = -0.363534,latTopLeft = 43.319279 ;
        double longBottomRight = -0.362978,latBottomRight = 43.319093;

        com.arangarcia.gazdetector.ui.plan.Plan plan = new com.arangarcia.gazdetector.ui.plan.Plan(
                getActivity(),
                R.id.imageViewPlan,
                43.319279,
                -0.363534,
                43.319093,
                -0.362978
        );/*43., -0.3748915662826034,44.313623593101546, -0.3524817214585103*/ /*UPPA*/
        // Vérifiez si la position actuelle est sur le plan
        if (isOnPlan()) {
            Log.d("Samuel_Plan","Is on Plan!");

            //Toast.makeText(getActivity(), "La position actuelle est sur le plan", Toast.LENGTH_SHORT).show();
            // Obtenez les coordonnées sur le plan
            /* code for puting the markerView in the right place*/
            ArrayList<Double> pos = posOnPlan();


            int x = (int) (pos.get(1).intValue()+imageViewPlan.getX());
            int y = (int) (pos.get(0).intValue()+imageViewPlan.getY());

            //plan.getCoordinatesOnPlan(location.getLatitude(), location.getLongitude());
            // Afficher les coordonnées sur le plan
            //ImageView imageView = (ImageView) getView().findViewById(R.id.imageViewPlan);
            //imageView.setImageBitmap(plan.getImage());
            ImageView markerView = (ImageView) getView().findViewById(R.id.imageViewMarker);
            markerView.setX(x);
            markerView.setY(y);
            markerView.setVisibility(View.VISIBLE);
        } else {
            //Toast.makeText(getActivity(), "La position actualise n'est pas sur le plan", Toast.LENGTH_SHORT).show();

            // La position actuelle n'est pas sur le plan
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
}