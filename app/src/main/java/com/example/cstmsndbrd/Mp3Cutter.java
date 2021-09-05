package com.example.cstmsndbrd;

import android.content.Context;
import java.io.File;


public class Mp3Cutter {
    private Context context;
    private File mp3File;
    private float start;
    private float end;
    public Mp3Cutter(Context context, File mp3File, float start, float end) {
        this.context = context;
        this.mp3File = mp3File;
        this.start = start;
        this.end = end;
    }
    private void cutSound() {

    }
}
