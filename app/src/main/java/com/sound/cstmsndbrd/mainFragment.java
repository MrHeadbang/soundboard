package com.sound.cstmsndbrd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class mainFragment extends Fragment {

    private RecyclerView mainRecyclerView;
    private File appDirectory;
    private File[] boardFiles;
    private ArrayList<String> boardPaths;
    private String appDirectoryPaths;
    private LinearLayout new_soundboard;
    private globals globals = new globals();

    @RequiresApi(api = Build.VERSION_CODES.R)
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

        new_soundboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globals.setFragment(getActivity(), new soundboardNewFragment(), "soundboardNewFragment");
            }
        });

        requireActivity().getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        directoryLister();
                    }
                });
        checkPermission();
        return view;
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void directoryLister() {
        try {
            appDirectoryPaths = Environment.getExternalStorageDirectory().toString() + "/" + "soundboards";
            appDirectory = new File(appDirectoryPaths);
            if (!appDirectory.exists()) appDirectory.mkdir();
            boardFiles = appDirectory.listFiles();
            boardPaths = new ArrayList<String>();
            if (boardFiles.length > 0)
                for (int i = boardFiles.length - 1; i >= 0; i--)
                    boardPaths.add(boardFiles[i].getName());

            mainRecyclerViewAdapter mainRecyclerViewAdapter = new mainRecyclerViewAdapter(getActivity(), boardPaths, appDirectoryPaths);
            int curSize = mainRecyclerViewAdapter.getItemCount();
            mainRecyclerViewAdapter.notifyItemRangeInserted(curSize, boardPaths.size());
            mainRecyclerView.setAdapter(mainRecyclerViewAdapter);
            hideKeyboard(requireActivity());

        } catch (Exception e) {
            Snackbar.make(requireView(), "Could not create directory. Check storage permissions!", Snackbar.LENGTH_LONG).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onResume() {
        super.onResume();
        directoryLister();
        if(Environment.isExternalStorageManager() && alertDialog != null)
            alertDialog.dismiss();
    }
    private AlertDialog alertDialog = null;
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void checkPermission() {
        if (Environment.isExternalStorageManager())
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialog);
        builder.setCancelable(false);
        builder.setMessage("Please allow full storage permission. It's necessary to create and store the sound files.");
        builder.setTitle("Warning");
        builder.setIcon(R.drawable.ic_baseline_warning_24);
        builder.setPositiveButton("Settings", null);
        if(alertDialog != null)
            alertDialog.dismiss();
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primaryAccent));
        Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }
}
