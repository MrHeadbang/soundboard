package com.example.cstmsndbrd;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class mainFragment extends Fragment {

    private RecyclerView mainRecyclerView;
    private File appDirectory;
    private File[] boardFiles;
    private ArrayList<String> boardPaths;
    private String appDirectoryPaths;
    private LinearLayout new_soundboard;
    private globals globals = new globals();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mainRecyclerView = view.findViewById(R.id.main_recyclerview);
        new_soundboard = view.findViewById(R.id.new_soundboard);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        mainRecyclerView.addItemDecoration(itemDecoration);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mainRecyclerView.setLayoutManager(linearLayoutManager);
        try {
            appDirectoryPaths = Environment.getExternalStorageDirectory().toString() + "/" + "soundboards";
            appDirectory = new File(appDirectoryPaths);
            if (!appDirectory.exists()) appDirectory.mkdir();
            boardFiles = appDirectory.listFiles();
            boardPaths = new ArrayList<String>();
            if (boardFiles.length > 0)
                for (int i = 0; i < boardFiles.length; i++)
                    boardPaths.add(boardFiles[i].getName());

            mainRecyclerViewAdapter mainRecyclerViewAdapter = new mainRecyclerViewAdapter(getActivity(), boardPaths, appDirectoryPaths);
            int curSize = mainRecyclerViewAdapter.getItemCount();
            mainRecyclerViewAdapter.notifyItemRangeInserted(curSize, boardPaths.size());
            mainRecyclerView.setAdapter(mainRecyclerViewAdapter);

            new_soundboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    globals.setFragment(getActivity(), new soundboardNewFragment(), "soundboardNewFragment");
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Could not create directory. Check storage permissions!", Toast.LENGTH_LONG).show();
        }

        return view;
    }
}
