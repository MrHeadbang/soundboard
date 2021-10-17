package com.example.cstmsndbrd;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class FileManager {

    private String SOUNDBOARD_PATH = "";
    private JSONObject configObject = null;
    private File configFile = null;
    private Context context = null;
    public FileManager(Context context, String SOUNDBOARD_PATH) {
        this.context = context;
        if(SOUNDBOARD_PATH == null)
            createBody();


        this.SOUNDBOARD_PATH = SOUNDBOARD_PATH;
        /*
        this.configFile = new File(this.SOUNDBOARD_PATH + "config.json");


        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(configFile));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
        }

        try {
            this.configObject = new JSONObject(text.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
    private void save() {
        try {
            FileOutputStream stream = new FileOutputStream(configFile);
            stream.write(configObject.toString().getBytes(StandardCharsets.UTF_8));
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JSONObject createConfigBody() throws JSONException {
        JSONObject jsonData = new JSONObject();
        jsonData.put("boardName", "");
        jsonData.put("boardDesc", "");
        jsonData.put("boardImagePath", "");
        JSONObject jsonSounds = new JSONObject();
        jsonData.put("soundList", jsonSounds);

        JSONObject output = new JSONObject();
        output.put("Board", jsonData);

        return output;
    }

    private void createBody() {
        String path = Environment.getExternalStorageDirectory().toString() + "/" + "soundboards";
        File appDirectory = new File(path);
        File[] boardFiles = appDirectory.listFiles();
        String lastBoard = boardFiles[boardFiles.length - 1].toString();
        int boardCounter = Integer.parseInt(lastBoard.substring(lastBoard.length() - 1));
        SOUNDBOARD_PATH = path + "/board" + String.valueOf(boardCounter + 1);
        File newBoard = new File(SOUNDBOARD_PATH);
        if (!newBoard.exists()) newBoard.mkdir();
        try {
            Toast.makeText(context, createConfigBody().toString(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setName(String name) throws JSONException {
        configObject.getJSONObject("Board").put("boardName", name);
        save();
    }
    public void setDescription(String description) throws JSONException {
        configObject.getJSONObject("Board").put("boardDesc", description);
        save();
    }
    public void setImage(Bitmap bitmap) throws JSONException, IOException {
        String bitmapName = "board.png";
        File outputFile = new File(SOUNDBOARD_PATH + "/" + bitmapName);
        outputFile.createNewFile();
        FileOutputStream fOut = null;
        fOut = new FileOutputStream(outputFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
        configObject.getJSONObject("Board").put("boardImagePath", bitmapName);
        save();
    }

    public void addSound(File soundFile, Bitmap soundImage, String soundName) {

    }
}
