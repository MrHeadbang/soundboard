package com.sound.cstmsndbrd;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Random;

public class globals {
    private final String[] colors = {"#000396", "#007820", "#960000", "#7d0079", "#bf6c00", "#009ea3"};
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
    private static final int RANDNAME_LENGHT = 16;
    private static final String SOUNDBOARDS_PATH = Environment.getExternalStorageDirectory().toString() + "/soundboards/";

    private static  String randName() {
        String output = "";
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();
        output = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(RANDNAME_LENGHT)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return output;
    }
}
