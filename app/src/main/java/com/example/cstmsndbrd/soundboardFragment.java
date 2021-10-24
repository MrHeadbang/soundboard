package com.example.cstmsndbrd;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class soundboardFragment extends Fragment {

    private TextView soundboard_headline, soundboard_desc;
    private ImageView soundboard_image;
    private RecyclerView soundboard_recyclerview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.soundboard_fragment, container, false);
        soundboard_headline = view.findViewById(R.id.soundboard_headline);
        soundboard_desc = view.findViewById(R.id.soundboard_desc);
        soundboard_image = view.findViewById(R.id.soundboard_image);
        soundboard_recyclerview = view.findViewById(R.id.soundboard_recyclerview);
        soundboard_recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        Bundle args = getArguments();
        final JSONObject[] configObject = {null};
        final JSONObject[] soundList = {null};
        final String[] boardName = {""};
        final String[] boardImagePath = {""};
        final String[] boardDesc = {""};
        String boardPath = args.getString("path");

        requireActivity().getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                File file = new File(boardPath + "config.json");
                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                }
                catch (IOException e) {
                }


                try {
                    configObject[0] = new JSONObject(text.toString()).getJSONObject("Board");
                    //configObject = new JSONObject(args.getString("config"));
                    boardName[0] = configObject[0].getString("boardName");
                    boardImagePath[0] = boardPath + configObject[0].getString("boardImagePath");
                    boardDesc[0] = configObject[0].getString("boardDesc");
                    soundList[0] = configObject[0].getJSONObject("soundList");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                soundboard_headline.setText(boardName[0]);
                soundboard_desc.setText(boardDesc[0]);
                soundboard_image.setImageBitmap(BitmapFactory.decodeFile(boardImagePath[0]));



                List<List<String>> sounds = new ArrayList<List<String>>();

                Iterator<String> keys = soundList[0].keys();

                while(keys.hasNext()) {
                    String key = keys.next();
                    try {
                        if (soundList[0].get(key) instanceof JSONObject) {
                            JSONObject soundObject = soundList[0].getJSONObject(key);
                            List<String> soundParams = new ArrayList<String>();
                            soundParams.add(soundObject.getString("soundName"));
                            soundParams.add(soundObject.getString("soundImagePath"));
                            soundParams.add(soundObject.getString("soundFilePath"));
                            sounds.add(soundParams);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                soundboardRecyclerViewAdapter soundboardRecyclerViewAdapter = new soundboardRecyclerViewAdapter(getActivity(), boardPath,sounds);
                int curSize = soundboardRecyclerViewAdapter.getItemCount();
                soundboardRecyclerViewAdapter.notifyItemRangeInserted(curSize, sounds.size());
                soundboard_recyclerview.setAdapter(soundboardRecyclerViewAdapter);
            }
        });

        return view;
    }

}
