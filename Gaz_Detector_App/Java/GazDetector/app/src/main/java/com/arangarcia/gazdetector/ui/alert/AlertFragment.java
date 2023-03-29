package com.arangarcia.gazdetector.ui.alert;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.arangarcia.gazdetector.R;
import com.arangarcia.gazdetector.RetrofitInterface;
import com.arangarcia.gazdetector.databinding.FragmentAlertBinding;
import com.arangarcia.gazdetector.sendResult;
import com.ortiz.touchview.TouchImageView.OnTouchImageViewListener;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AlertFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    private com.ortiz.touchview.TouchImageView imageViewAlert;
    private TextView posTextViewAlert;
    private FragmentAlertBinding binding;
    private Button btnConfirmAlert;
    public Spinner spinner;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://192.168.1.51:3000";
    private Double latitude;
    private Double longitude;

    public AlertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAlertBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        imageViewAlert = root.findViewById(R.id.imageViewAlert);
        posTextViewAlert = root.findViewById(R.id.posTextViewAlert);

        // Initialisation of the server connection
        /*
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                        .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        */
        imageViewAlert.setOnTouchImageViewListener(new OnTouchImageViewListener () {
            @Override
            public void onMove() {

                PointF pt = imageViewAlert.getScrollPosition();
                String str = imageViewAlert.getZoomedRect().toString() + pt.toString();

                posTextViewAlert.setText(str);
                Log.d("Samuel_Plan","J'ai touché");
            }
        });

        imageViewAlert.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Float x = motionEvent.getX();
                Float y = motionEvent.getY();

                posTextViewAlert.setText("X: " + x.toString() + "; Y: " + y.toString());
                return true;
            }
        });

        /*imageViewPlan.setOnTouchCoordinatesListener(new OnTouchCoordinatesListener (){
            @Override
            public void onTouchCoordinate(View v, MotionEvent motionEvent, PointF point){
                String str = (String) posTextView.getText();
                posTextView.setText(str + " et la coordonnée est " + point.toString());
            }
        });*/
        spinner = root.findViewById(R.id.spDanger);
        initSpinner();
        
        btnConfirmAlert = (Button) root.findViewById(R.id.btnConfirmAlert);
        btnConfirmAlert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                handleConfirmAlert();                
            }
        });

        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    private void initSpinner() {

        String[] dangerLevels = {"Nominative", "Important", "Urgent"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dangerLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }



    private void handleConfirmAlert() {
        HashMap<String, String> map = new HashMap<>();

        map.put("latitude",latitude.toString());
        map.put("longitude",longitude.toString());
        map.put("danger", "todo");

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}