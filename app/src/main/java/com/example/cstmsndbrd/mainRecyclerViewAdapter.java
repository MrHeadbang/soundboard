package com.example.cstmsndbrd;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class mainRecyclerViewAdapter extends RecyclerView.Adapter<mainRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<String> boardPaths;
    String appDirectoryPath;
    globals globals = new globals();
    public mainRecyclerViewAdapter(Context context, ArrayList<String> boardPaths, String appDirectoryPath) {
        this.context = context;
        this.boardPaths = boardPaths;
        this.appDirectoryPath = appDirectoryPath;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_recyclerview_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mainRecyclerViewAdapter.ViewHolder holder, int position) {
        String boardPath = appDirectoryPath + "/" + boardPaths.get(position);
        File boardFiles = new File(boardPath);

        File file = new File(boardPath + "/config.json");
        StringBuilder text = new StringBuilder();

        FileManager fileManager = new FileManager(context, boardPath);

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
        JSONObject configObject = null;
        String boardName = "";
        String boardImagePath = "";
        String boardDesc = "";

        try {
            configObject = new JSONObject(text.toString()).getJSONObject("Board");

            boardName = configObject.getString("boardName");
            boardImagePath = boardPath + "/" + configObject.getString("boardImagePath");
            boardDesc = configObject.getString("boardDesc");

        } catch (JSONException e) {
            e.printStackTrace();
        }



        holder.main_recyclerview_item_name.setText(boardName);
        holder.main_recyclerview_item_image.setImageBitmap(BitmapFactory.decodeFile(boardImagePath));
        holder.main_recyclerview_item_layout.setBackgroundColor(globals.randomColor());
        JSONObject finalConfigObject = configObject;
        holder.main_recyclerview_item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("config", finalConfigObject.toString());
                args.putString("path", boardPath + "/");
                Fragment soundboardFragment = new soundboardFragment();
                soundboardFragment.setArguments(args);
                globals.setFragment(context, soundboardFragment, "soundboardFragment");
            }
        });


    }

    @Override
    public int getItemCount() {
        return boardPaths.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView main_recyclerview_item_name;
        ImageView main_recyclerview_item_image;
        LinearLayout main_recyclerview_item_layout;
        CardView main_recyclerview_item_card;
        public ViewHolder(View view) {
            super(view);
            main_recyclerview_item_name = view.findViewById(R.id.main_recyclerview_item_name);
            main_recyclerview_item_image = view.findViewById(R.id.main_recyclerview_item_image);
            main_recyclerview_item_layout = view.findViewById(R.id.main_recyclerview_item_layout);
            main_recyclerview_item_card = view.findViewById(R.id.main_recyclerview_item_card);
        }
    }
}
