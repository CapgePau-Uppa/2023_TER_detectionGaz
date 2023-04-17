package com.arangarcia.gazdetector.ui.alert;

import android.content.res.AssetManager;
import android.graphics.RectF;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arangarcia.gazdetector.R;
import com.arangarcia.gazdetector.RetrofitInterface;
import com.arangarcia.gazdetector.databinding.FragmentAlertBinding;
import com.arangarcia.gazdetector.sendResult;

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

public class AlertFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentAlertBinding binding;
    private String BASE_URL;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;

    public Spinner spinner;
    private String dangerSelected = "Nominative";
    private Button btnReset;
    private Button btnAlert;
    private com.ortiz.touchview.TouchImageView imageViewPlan;
    ImageView markerView;
    private ArrayList<Double> loc;
    // Coordinates of the map
    private static Double[] topLeft;
    private static Double[] topRight;
    private static Double[] botLeft;
    private static Double[] botRight;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.arangarcia.gazdetector.ui.Sensor.SensorViewModel SensorViewModel =  new ViewModelProvider(this).get(com.arangarcia.gazdetector.ui.Sensor.SensorViewModel.class);

        binding = FragmentAlertBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Handle the config file
        try {
            // Open the JSON file in the "assets" folder
            AssetManager manager = getContext().getAssets();
            InputStream stream = null;
            stream = manager.open("config.json");

            // Read the content of the file
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            String json = new String(buffer, "UTF-8");

            // Convert the content in a Java object
            JSONObject config = new JSONObject(json);
            JSONArray maps = config.getJSONArray("maps");

            // map 0 capge, map 1 uppa
            JSONObject curMap = maps.getJSONObject(0);
            JSONObject corners = curMap.getJSONObject("corners");

            JSONObject jsTopLeft = corners.getJSONObject("topLeft");
            topLeft = new Double[]{jsTopLeft.getDouble("latitude"), jsTopLeft.getDouble("longitude")};

            JSONObject jsTopRight = corners.getJSONObject("topRight");
            topRight = new Double[]{jsTopRight.getDouble("latitude"), jsTopRight.getDouble("longitude")};

            JSONObject jsBotLeft = corners.getJSONObject("botLeft");
            botLeft = new Double[]{jsBotLeft.getDouble("latitude"), jsBotLeft.getDouble("longitude")};

            JSONObject jsBotRight = corners.getJSONObject("botRight");
            botRight = new Double[]{jsBotRight.getDouble("latitude"), jsBotRight.getDouble("longitude")};

            BASE_URL = config.getString("baseUrl");

            // close the file
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //Initialisation of buttons

        btnAlert = (Button) root.findViewById(R.id.btnAlert);
        btnReset = (Button) root.findViewById(R.id.btnReset);

        imageViewPlan = root.findViewById(R.id.imageViewAlert);
        markerView = (ImageView) root.findViewById(R.id.AlertMarker);

        btnAlert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                handleConfirmAlert();
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
        /*
            On click, a cursor is placed on the map and the position is stored
         */
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
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {  }
    /*
    * sends an alert to the database
    */
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
    /*
    * converts a position on the map into a Geo localisation
    **/
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
