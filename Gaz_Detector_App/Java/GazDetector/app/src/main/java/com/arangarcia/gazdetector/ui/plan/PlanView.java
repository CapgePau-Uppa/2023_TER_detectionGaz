package com.arangarcia.gazdetector.ui.plan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
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

import java.sql.Array;
import java.util.ArrayList;

public class PlanView extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final int PERMISSION_FINE_LOCATION = 99;
    public static final int DEFAULT_INTERVAL_MILLIS = 30000;
    public static final int MIN_UPDATE_INTERVAL_MILLIS = 5000;
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


        spinner = root.findViewById(R.id.spPlan);
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
        }else{
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

        return false;
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
        if (plan.isOnPlan(location.getLatitude(), location.getLongitude())) {

            Toast.makeText(getActivity(), "La position actuelle est sur le plan", Toast.LENGTH_SHORT).show();
            // Obtenez les coordonnées sur le plan
            ImageView imageView = (ImageView) getView().findViewById(R.id.imageViewPlan);

            int x = (int) (((location.getLongitude() - longTopLeft) / (longBottomRight - longTopLeft)) * imageView.getWidth() + imageView.getX());
            int y = (int) (((latBottomRight - location.getLatitude()) / (latBottomRight - latTopLeft)) * imageView.getHeight() + imageView.getY());
            Point point =  new Point(x, y);

            //plan.getCoordinatesOnPlan(location.getLatitude(), location.getLongitude());
            // Afficher les coordonnées sur le plan
            //ImageView imageView = (ImageView) getView().findViewById(R.id.imageViewPlan);
            //imageView.setImageBitmap(plan.getImage());
            ImageView markerView = (ImageView) getView().findViewById(R.id.imageViewMarker);
            markerView.setX(point.x);
            markerView.setY(point.y);
            markerView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getActivity(), "La position actualise n'est pas sur le plan", Toast.LENGTH_SHORT).show();

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