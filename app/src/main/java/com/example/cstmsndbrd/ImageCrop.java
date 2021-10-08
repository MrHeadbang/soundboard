package com.example.cstmsndbrd;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ImageCrop extends Fragment {
    private CropImageView cropImageView;
    private Uri uri;
    private globals globals = new globals();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_crop, container, false);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        assert bundle != null;
        uri = Uri.parse(bundle.getString("image_uri"));

        cropImageView = view.findViewById(R.id.cropImageView);
        cropImageView.setImageUriAsync(uri);
        cropImageView.setAspectRatio(1, 1);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.image_crop, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint({"NonConstantResourceId", "WrongThread"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.image_crop_done) {
            Bitmap cropped = cropImageView.getCroppedImage();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            cropped.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent = new Intent(getActivity(), ImageCrop.class);
            intent.putExtra("cropped", byteArray);

            getTargetFragment().onActivityResult(getTargetRequestCode(), 201, intent);
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
}
