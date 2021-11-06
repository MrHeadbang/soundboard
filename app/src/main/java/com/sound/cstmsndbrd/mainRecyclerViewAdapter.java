package com.sound.cstmsndbrd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull mainRecyclerViewAdapter.ViewHolder holder, int position) {
        String boardPath = appDirectoryPath + "/" + boardPaths.get(position);
        File file = new File(boardPath + "/config.json");
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
        JSONObject configObject = null;
        String boardName = "";
        String boardImagePath = "";
        try {
            configObject = new JSONObject(text.toString()).getJSONObject("Board");
            boardName = configObject.getString("boardName");
            boardImagePath = boardPath + "/" + configObject.getString("boardImagePath");
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
                args.putString("path", boardPath + "/");
                Fragment soundboardFragment = new soundboardFragment();
                soundboardFragment.setArguments(args);
                globals.setFragment(context, soundboardFragment, "soundboardFragment");
            }
        });
        holder.main_recyclerview_item_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];
                showStatusPopup(context, point, boardPath, holder.getAdapterPosition());
            }
        });
    }
    private void showStatusPopup(final Context context, Point p, String boardPath, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View layout = layoutInflater.inflate(R.layout.options_popup, null);

        PopupWindow changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);
        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setAnimationStyle(android.R.style.Animation_Dialog);

        int OFFSET_X = -300;
        int OFFSET_Y = 50;

        changeStatusPopUp.setOutsideTouchable(true);
        changeStatusPopUp.setFocusable(true);
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        LinearLayout shareOption = layout.findViewById(R.id.shareOption);
        LinearLayout deleteOption = layout.findViewById(R.id.deleteOption);
        LinearLayout editOption = layout.findViewById(R.id.editOption);
        editOption.setVisibility(View.VISIBLE);
        FileManager fileManager = new FileManager(context, boardPath);
        shareOption.setVisibility(View.GONE);
        deleteOption.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                changeStatusPopUp.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.ic_baseline_warning_24);
                builder.setMessage("All sounds and images will be lost.");
                builder.setCancelable(true);
                builder.setNegativeButton("CANCEL", null);
                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fileManager.deleteBoard();
                        boardPaths.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,boardPaths.size());
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.primaryAccent));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.primaryAccent));
            }
        });
        editOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatusPopUp.dismiss();
                soundboardEditSpecsFragment soundboardEditSpecsFragment = new soundboardEditSpecsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("path", boardPath);
                soundboardEditSpecsFragment.setArguments(bundle);
                globals.setFragment(context, soundboardEditSpecsFragment, "soundboardEditSpecsFragment");
            }
        });

    }
    @Override
    public int getItemCount() {
        return boardPaths.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView main_recyclerview_item_name;
        private ImageView main_recyclerview_item_image, main_recyclerview_item_options;
        private LinearLayout main_recyclerview_item_layout;
        private CardView main_recyclerview_item_card;
        public ViewHolder(View view) {
            super(view);
            main_recyclerview_item_name = view.findViewById(R.id.main_recyclerview_item_name);
            main_recyclerview_item_image = view.findViewById(R.id.main_recyclerview_item_image);
            main_recyclerview_item_layout = view.findViewById(R.id.main_recyclerview_item_layout);
            main_recyclerview_item_card = view.findViewById(R.id.main_recyclerview_item_card);
            main_recyclerview_item_options = view.findViewById(R.id.main_recyclerview_item_options);
        }
    }
}
