package com.example.cstmsndbrd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Random;

public class globals {
    private String[] colors = {"#000396", "#007820", "#960000", "#7d0079", "#bf6c00", "#009ea3"};
    private Context context;

    public int randomColor() {
        return Color.parseColor(colors[new Random().nextInt(colors.length)]);
    }
    public void setFragment(Context context, Fragment fragment, String backstack) {
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mainFrame, fragment);
        fragmentTransaction.addToBackStack(backstack);
        fragmentTransaction.commit();
    }
}
