package com.example.cstmsndbrd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class soundboardRecyclerViewAdapter extends RecyclerView.Adapter<soundboardRecyclerViewAdapter.ViewHolder> {
    Context context;
    String boardPath;
    globals globals = new globals();
    List<List<String>> sounds;
    MediaPlayer mediaPlayer;
    int FOOTER_VIEW = 1;
    public soundboardRecyclerViewAdapter(Context context, String boardPath, List<List<String>> sounds) {
        this.context = context;
        this.boardPath = boardPath;
        this.sounds = sounds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType == FOOTER_VIEW) {
            View footerView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sound_add_footer, viewGroup, false);
            return new Footer(footerView);
        }
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.soundboard_recyclerview_item, viewGroup, false);
        return new SoundItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull soundboardRecyclerViewAdapter.ViewHolder holder, int position) {

        if(holder instanceof SoundItem) {
            SoundItem itemHolder = (SoundItem) holder;
            int randomColor = globals.randomColor();
            List<String> params = sounds.get(position);
            itemHolder.soundboard_item_caption.setText(params.get(0));
            Bitmap soundBitmap = BitmapFactory.decodeFile(boardPath + params.get(1));
            if (soundBitmap != null)
                itemHolder.soundboard_item_image.setImageBitmap(soundBitmap);
            else
                itemHolder.soundboard_item_image.setBackgroundColor(randomColor);
            itemHolder.soundboard_layout.setBackgroundColor(randomColor);
            itemHolder.soundboard_layout_card.setCardBackgroundColor(randomColor);
            itemHolder.soundboard_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mediaPlayer != null)
                        mediaPlayer.release();
                    mediaPlayer = MediaPlayer.create(context, Uri.parse(boardPath + params.get(2)));
                    mediaPlayer.start();
                }
            });
            itemHolder.soundboard_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(context, params.get(0), Toast.LENGTH_LONG).show();
                    return false;
                }
            });


        } else if(holder instanceof Footer) {
            Footer itemHolder = (Footer) holder;
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mediaPlayer != null)
                        mediaPlayer.reset();
                    Bundle bundle = new Bundle();
                    bundle.putString("boardPath", boardPath);
                    soundboardEditFragment soundboardEditFragment = new soundboardEditFragment();
                    soundboardEditFragment.setArguments(bundle);
                    globals.setFragment(context, soundboardEditFragment, "soundboardEditFragment");
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return sounds.size() + 1;
    }
    @Override
    public int getItemViewType(int position) {
        if (position == sounds.size()) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

        }
    }
    public static class Footer extends ViewHolder {

        public Footer(View view) {
            super(view);
        }
    }
    public static class SoundItem extends ViewHolder {
        LinearLayout soundboard_layout;
        ImageView soundboard_item_image;
        TextView soundboard_item_caption;
        CardView soundboard_layout_card;
        public SoundItem(View view) {
            super(view);
            soundboard_layout = view.findViewById(R.id.soundboard_layout);
            soundboard_item_image = view.findViewById(R.id.soundboard_item_image);
            soundboard_item_caption = view.findViewById(R.id.soundboard_item_caption);
            soundboard_layout_card = view.findViewById(R.id.soundboard_layout_card);
        }
    }
}
