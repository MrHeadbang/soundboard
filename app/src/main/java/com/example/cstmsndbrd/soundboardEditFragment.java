package com.example.cstmsndbrd;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class soundboardEditFragment extends Fragment {
    private LinearLayout soundboard_edit_addsound;
    private int REQ_CODE_PICK_SOUNDFILE = 1;
    private globals globals = new globals();
    private String boardPath = "", soundName = "", cutSoundPath = "";
    private ImageView soundboard_edit_addimage;
    private Bitmap cropped;
    private TextView soundboard_edit_soundname;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.soundboard_edit_fragment, container, false);
        soundboard_edit_addsound = view.findViewById(R.id.soundboard_edit_addsound);
        soundboard_edit_addimage = view.findViewById(R.id.soundboard_edit_addimage);
        soundboard_edit_soundname = view.findViewById(R.id.soundboard_edit_soundname);
        Bundle args = getArguments();
        boardPath = args.getString("boardPath");

        soundboard_edit_addsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg");
                startActivityForResult(Intent.createChooser(intent, "Select Audio File"), REQ_CODE_PICK_SOUNDFILE);
            }
        });
        ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null && result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            if (null != selectedImageUri) {
                                Bundle bundle = new Bundle();
                                bundle.putString("image_uri", selectedImageUri.toString());
                                Fragment imageCrop = new ImageCrop();
                                imageCrop.setTargetFragment(soundboardEditFragment.this, 200);
                                imageCrop.setArguments(bundle);
                                globals.setFragment(requireContext(), imageCrop, "ImageCrop");
                            }
                        }
                    }
                });
        soundboard_edit_addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                mLauncher.launch(i);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_SOUNDFILE && resultCode == Activity.RESULT_OK){
            if ((data != null) && (data.getData() != null)){
                Uri audioFileUri = data.getData();

                Bundle bundle = new Bundle();
                bundle.putString("audioUri", audioFileUri.toString());
                bundle.putString("boardPath", boardPath);
                Fragment soundCut = new soundCut();
                soundCut.setArguments(bundle);
                soundCut.setTargetFragment(soundboardEditFragment.this, 200);
                globals.setFragment(getActivity(), soundCut, "soundCut");
            }
        }
        if (resultCode == 201) {
            if (requestCode== 200){
                assert data != null;
                byte[] byteArray = data.getByteArrayExtra("cropped");
                cropped = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                soundboard_edit_addimage.setImageTintMode(null);
                soundboard_edit_addimage.setScaleType(ImageView.ScaleType.FIT_XY);
                soundboard_edit_addimage.setImageBitmap(cropped);
            }
        }
        if (resultCode == 202) {
            if (requestCode== 200){
                soundName = data.getStringExtra("soundName");
                cutSoundPath = data.getStringExtra("cutSoundPath");
                soundboard_edit_soundname.setText(soundName);
            }
        }
    }
}
