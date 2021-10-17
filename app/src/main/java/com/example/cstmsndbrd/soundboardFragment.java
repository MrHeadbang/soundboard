package com.example.cstmsndbrd;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

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
        JSONObject configObject = null;
        JSONObject soundList = null;
        String boardName = "";
        String boardImagePath = "";
        String boardDesc = "";
        String boardPath = args.getString("path");

        try {
            configObject = new JSONObject(args.getString("config"));
            boardName = configObject.getString("boardName");
            boardImagePath = boardPath + configObject.getString("boardImagePath");
            boardDesc = configObject.getString("boardDesc");
            soundList = configObject.getJSONObject("soundList");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        soundboard_headline.setText(boardName);
        soundboard_desc.setText(boardDesc);
        soundboard_image.setImageBitmap(BitmapFactory.decodeFile(boardImagePath));



        List<List<String>> sounds = new ArrayList<List<String>>();

        Iterator<String> keys = soundList.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            try {
                if (soundList.get(key) instanceof JSONObject) {
                    JSONObject soundObject = soundList.getJSONObject(key);
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



        return view;
    }
}
