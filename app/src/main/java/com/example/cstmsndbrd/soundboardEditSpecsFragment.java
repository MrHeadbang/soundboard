package com.example.cstmsndbrd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class soundboardEditSpecsFragment extends Fragment {

    String SOUNDBOARD_PATH = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.soudboard_edit_specs_fragment, container, false);
        Bundle args = getArguments();
        SOUNDBOARD_PATH = args.getString("path");
        FileManager fileManager = new FileManager(requireContext(), SOUNDBOARD_PATH);
        return view;
    }
}
