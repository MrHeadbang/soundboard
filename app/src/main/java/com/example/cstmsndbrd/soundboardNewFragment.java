package com.example.cstmsndbrd;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class soundboardNewFragment extends Fragment {

    private ImageView new_soundboard_image;
    private globals globals = new globals();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.soundboard_new_fragment, container, false);
        new_soundboard_image = view.findViewById(R.id.new_soundboard_image);
        ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null) {
                            assert result.getData() != null;
                            Uri selectedImageUri = result.getData().getData();
                            if (null != selectedImageUri) {
                                //new_soundboard_image.setImageTintMode(null);
                                //new_soundboard_image.setScaleType(ImageView.ScaleType.FIT_XY);
                                //new_soundboard_image.setImageURI(selectedImageUri);
                                Bundle bundle = new Bundle();
                                bundle.putString("image_uri", selectedImageUri.toString());
                                Fragment imageCrop = new ImageCrop();
                                imageCrop.setArguments(bundle);
                                globals.setFragment(getContext(), imageCrop, "ImageCrop");
                            }
                        }
                    }
                });
        new_soundboard_image.setOnClickListener(new View.OnClickListener() {
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

}
