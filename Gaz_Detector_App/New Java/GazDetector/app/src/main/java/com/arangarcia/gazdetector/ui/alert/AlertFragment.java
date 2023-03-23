package com.arangarcia.gazdetector.ui.alert;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.arangarcia.gazdetector.R;
import com.arangarcia.gazdetector.databinding.FragmentAlertBinding;
//import com.ortiz.touchview.TouchImageView.OnTouchCoordinatesListener;
import com.ortiz.touchview.TouchImageView.OnTouchImageViewListener;

public class AlertFragment extends Fragment {
    private com.ortiz.touchview.TouchImageView imageViewAlert;
    private TextView posTextViewAlert;

    public AlertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentAlertBinding binding = FragmentAlertBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        imageViewAlert = root.findViewById(R.id.imageViewAlert);
        posTextViewAlert = root.findViewById(R.id.posTextViewAlert);

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

        return inflater.inflate(R.layout.fragment_alert, container, false);
    }
}