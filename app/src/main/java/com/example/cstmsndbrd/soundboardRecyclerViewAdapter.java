package com.example.cstmsndbrd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    Point point = new Point();
                    point.x = location[0];
                    point.y = location[1];
                    showStatusPopup(context, point, boardPath, holder.getAdapterPosition());
                    return false;
                }
            });
            ((MainActivity) context).getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if(mediaPlayer != null)
                        mediaPlayer.reset();
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
    private void showStatusPopup(final Context context, Point p, String boardPath, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.options_popup, null);

        PopupWindow changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);
        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setAnimationStyle(android.R.style.Animation_Dialog);

        int OFFSET_X = -300 + 300;
        int OFFSET_Y = 150;

        changeStatusPopUp.setOutsideTouchable(true);
        changeStatusPopUp.setFocusable(true);
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        LinearLayout shareOption = layout.findViewById(R.id.shareOption);
        LinearLayout deleteOption = layout.findViewById(R.id.deleteOption);
        FileManager fileManager = new FileManager(context, boardPath);
        shareOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatusPopUp.dismiss();
                fileManager.shareAudio(position);
            }
        });
        deleteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatusPopUp.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.ic_baseline_warning_24);
                builder.setMessage("This sound will be lost.");
                builder.setCancelable(true);
                builder.setNegativeButton("CANCEL", null);
                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mediaPlayer != null)
                            mediaPlayer.reset();

                        fileManager.deleteSound(position);
                        sounds.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, sounds.size());
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.primaryAccent));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.primaryAccent));
            }
        });

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
