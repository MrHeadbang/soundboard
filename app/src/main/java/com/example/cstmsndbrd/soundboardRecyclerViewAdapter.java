package com.example.cstmsndbrd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class soundboardRecyclerViewAdapter extends RecyclerView.Adapter<soundboardRecyclerViewAdapter.ViewHolder> {
    Context context;
    String boardPath;
    globals globals = new globals();
    List<List<String>> sounds;
    MediaPlayer mediaPlayer;
    public soundboardRecyclerViewAdapter(Context context, String boardPath, List<List<String>> sounds) {
        this.context = context;
        this.boardPath = boardPath;
        this.sounds = sounds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.soundboard_recyclerview_item, viewGroup, false);
        return new soundboardRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull soundboardRecyclerViewAdapter.ViewHolder holder, int position) {
        int randomColor = globals.randomColor();
        List<String> params = sounds.get(position);
        holder.soundboard_item_caption.setText(params.get(0));
        Bitmap soundBitmap = BitmapFactory.decodeFile(boardPath + params.get(1));
        if(soundBitmap != null)
            holder.soundboard_item_image.setImageBitmap(soundBitmap);
        else
            holder.soundboard_item_image.setBackgroundColor(randomColor);
        holder.soundboard_layout.setBackgroundColor(randomColor);
        holder.soundboard_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer != null)
                    mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(context, Uri.parse(boardPath + params.get(2)));
                mediaPlayer.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return sounds.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout soundboard_layout;
        ImageView soundboard_item_image;
        TextView soundboard_item_caption;
        public ViewHolder(View view) {
            super(view);
            soundboard_layout = view.findViewById(R.id.soundboard_layout);
            soundboard_item_image = view.findViewById(R.id.soundboard_item_image);
            soundboard_item_caption = view.findViewById(R.id.soundboard_item_caption);
        }
    }
}
