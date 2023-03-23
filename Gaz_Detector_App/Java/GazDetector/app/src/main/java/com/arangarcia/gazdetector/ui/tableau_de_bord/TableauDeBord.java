package com.arangarcia.gazdetector.ui.tableau_de_bord;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arangarcia.gazdetector.databinding.FragmentTableauDeBordBinding;

public class TableauDeBord extends Fragment {

    private @NonNull FragmentTableauDeBordBinding binding;
    private TableauDeBordViewModel mViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(TableauDeBordViewModel.class);

        binding = FragmentTableauDeBordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



         return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}