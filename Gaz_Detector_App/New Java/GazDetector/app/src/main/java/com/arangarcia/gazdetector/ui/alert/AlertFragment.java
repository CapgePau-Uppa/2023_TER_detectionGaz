package com.arangarcia.gazdetector.ui.alert;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gazdetector.R;
import com.example.gazdetector.databinding.FragmentPlanBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertFragment extends Fragment {
    private com.ortiz.touchview.TouchImageView imageViewPlan;
    private TextView posTextView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AlertFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertFragment newInstance(String param1, String param2) {
        AlertFragment fragment = new AlertFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentPlanBinding binding = FragmentPlanBinding.inflate(inflater, container, false);
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

        return inflater.inflate(R.layout.fragment_alert, container, false);
    }
}