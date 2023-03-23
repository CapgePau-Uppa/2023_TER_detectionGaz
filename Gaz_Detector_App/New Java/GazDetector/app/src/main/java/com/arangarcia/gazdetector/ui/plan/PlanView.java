package com.example.gazdetector.ui.plan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Point;
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

import com.example.gazdetector.R;
import com.example.gazdetector.databinding.FragmentPlanBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class PlanView extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final int PERMISSION_FINE_LOCATION = 99;
    private FragmentPlanBinding binding;
    private Location location;
    private com.ortiz.touchview.TouchImageView imageViewPlan;
    private TextView posTextView;

    public Spinner spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentPlanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageViewPlan = root.findViewById(R.id.imageViewPlan);
        posTextView = root.findViewById(R.id.posTextView);

        imageViewPlan.setOnTouchImageViewListener(new com.ortiz.touchview.TouchImageView.OnTouchImageViewListener () {
            @Override
            public void onMove() {

                posTextView.setText(imageViewPlan.getZoomedRect().toString());
                //Log.d("Samuel_Plan","J'ai touché");
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

    private void initSpinner(){
        String[] planNames = {"UPPA", "CapGemini"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, planNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    private void updateGPS(){
        //get permission from user
        //get the current location from the fused client
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    setLocation(location);
                    updatePosition(location);
                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }



    public void updatePosition(Location location){
        double longTopLeft = -0.363534,latTopLeft = 43.319279 ;
        double longBottomRight = -0.362978,latBottomRight = 43.319093;

        com.example.gazdetector.ui.plan.Plan plan = new com.example.gazdetector.ui.plan.Plan(
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