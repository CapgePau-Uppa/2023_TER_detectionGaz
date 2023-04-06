package com.arangarcia.gazdetector.ui.alert;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arangarcia.gazdetector.R;
import com.arangarcia.gazdetector.databinding.FragmentAlertBinding;
import com.arangarcia.gazdetector.sendResult;
import com.arangarcia.gazdetector.RetrofitInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertFragment extends Fragment {

    private AlertViewModel mViewModel;
    private FragmentAlertBinding binding;
    private Button btnConfirmAlert;
    private RetrofitInterface retrofitInterface;

    public static AlertFragment newInstance() {
        return new AlertFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAlertBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //imageViewAlert = root.findViewById(R.id.imageViewAlert);

        //posTextViewAlert = root.findViewById(R.id.posTextViewAlert);


        btnConfirmAlert = (Button) root.findViewById(R.id.btnConfirmAlert);


        btnConfirmAlert.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Log.d("backEnd", "on click confirm");
                   Toast.makeText(root.getContext(), "confirmed", Toast.LENGTH_SHORT).show();

                   handleConfirmAlert();
               }
           }

        );



        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AlertViewModel.class);
        // TODO: Use the ViewModel
    }

    private void handleConfirmAlert() {
        HashMap<String, String> map = new HashMap<>();

        Log.d("backEnd", "start handleConfirm");

        map.put("latitude","latitude.toString()");
        map.put("longitude","longitude.toString()");
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


}