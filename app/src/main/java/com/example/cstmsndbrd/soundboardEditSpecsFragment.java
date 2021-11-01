package com.example.cstmsndbrd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class soundboardEditSpecsFragment extends Fragment {

    private String SOUNDBOARD_PATH = "";
    private ImageView new_soundboard_image;
    private EditText new_soundboard_name, new_soundboard_desc;
    private LinearLayout new_soundboard_create;
    private TextView save_text;
    private Bitmap cropped = null;
    private globals globals = new globals();
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.soundboard_new_fragment, container, false);
        Bundle args = getArguments();
        SOUNDBOARD_PATH = Objects.requireNonNull(args).getString("path");
        FileManager fileManager = new FileManager(requireContext(), SOUNDBOARD_PATH + "/");

        new_soundboard_image = view.findViewById(R.id.new_soundboard_image);
        new_soundboard_name = view.findViewById(R.id.new_soundboard_name);
        new_soundboard_desc = view.findViewById(R.id.new_soundboard_desc);
        new_soundboard_create = view.findViewById(R.id.new_soundboard_create);
        save_text = view.findViewById(R.id.save_text);
        new_soundboard_image.setImageTintMode(null);
        new_soundboard_image.setScaleType(ImageView.ScaleType.FIT_XY);

        save_text.setText("Save");

        String boardName = "";
        String boardImagePath = "";
        String boardDesc = "";

        File file = new File(SOUNDBOARD_PATH + "/config.json");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            JSONObject configObject = new JSONObject(text.toString()).getJSONObject("Board");
            boardName = configObject.getString("boardName");
            boardDesc = configObject.getString("boardDesc");
            boardImagePath = SOUNDBOARD_PATH + "/" + configObject.getString("boardImagePath");

            cropped = BitmapFactory.decodeFile(boardImagePath);

            new_soundboard_name.setText(boardName);
            new_soundboard_desc.setText(boardDesc);
            new_soundboard_image.setImageBitmap(cropped);
        }
        catch (IOException | JSONException ignored) {
        }
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
                                imageCrop.setTargetFragment(soundboardEditSpecsFragment.this, 200);
                                imageCrop.setArguments(bundle);
                                globals.setFragment(requireContext(), imageCrop, "ImageCrop");
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

        new_soundboard_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new_soundboard_name.getText().toString().equals("")) {
                    Snackbar.make(view, "Name can't be empty!", Snackbar.LENGTH_LONG).show();
                    return;
                }
                try {
                    fileManager.setName(new_soundboard_name.getText().toString());
                    fileManager.setDescription(new_soundboard_desc.getText().toString());
                    fileManager.setImage(cropped);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 201) {
            if (requestCode== 200){
                assert data != null;
                byte[] byteArray = data.getByteArrayExtra("cropped");
                cropped = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                new_soundboard_image.setImageTintMode(null);
                new_soundboard_image.setScaleType(ImageView.ScaleType.FIT_XY);
                new_soundboard_image.setImageBitmap(cropped);
            }
        }
    }
}
