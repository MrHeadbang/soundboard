package com.example.cstmsndbrd;

import android.os.Environment;

import java.io.File;

public class FileManager {

    private static String SOUNDBOARDS_PATH = Environment.getExternalStorageDirectory().toString() + "/soundboards/";

    public void createSoundboardDirectory(String boardName) {
        File soundboardDirectory = new File(SOUNDBOARDS_PATH + boardName);
        if (!soundboardDirectory.exists())
            soundboardDirectory.mkdir();
    }

}
