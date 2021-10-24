package com.example.cstmsndbrd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.Objects;
import java.util.Random;

public class FileManager {

    private String SOUNDBOARD_PATH = "";
    private JSONObject configObject = null;
    private File configFile = null;
    private Context context = null;
    public FileManager(Context context, String SOUNDBOARD_PATH) {
        this.context = context;
        if(SOUNDBOARD_PATH == null) {
            createBody();
            return;
        }


        this.SOUNDBOARD_PATH = SOUNDBOARD_PATH;
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
        }
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

    private String newPATH(String path, int boardCounter) {
        return path + "/board" + String.valueOf(boardCounter + 1);
    }

    private void createBody() {
        String path = Environment.getExternalStorageDirectory().toString() + "/" + "soundboards";
        File appDirectory = new File(path);
        File[] boardFiles = appDirectory.listFiles();
        int boardCounter = 0;
        if(boardFiles.length > 0) {
            String lastBoard = boardFiles[boardFiles.length - 1].toString();
            boardCounter = Integer.parseInt(lastBoard.substring(lastBoard.length() - 1));
        }
        SOUNDBOARD_PATH = newPATH(path, boardCounter + 1);
        configFile = new File(SOUNDBOARD_PATH + "/config.json");

        File newBoard = new File(SOUNDBOARD_PATH);
        if (!newBoard.exists()) newBoard.mkdir();
        try {
            configObject = createConfigBody();
            save();
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
        if(bitmap == null)
            return;
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
    public void deleteBoard() {
        deleteSubFolders(SOUNDBOARD_PATH);
        File currentFolder = new File(SOUNDBOARD_PATH);
        currentFolder.delete();
    }
    private void deleteSubFolders(String uri) {
        File currentFolder = new File(uri);
        File files[] = currentFolder.listFiles();

        if (files == null) {
            return;
        }
        for (File f : files)
        {
            if (f.isDirectory())
            {
                deleteSubFolders(f.toString());
            }
            f.delete();
        }
    }
    public void addSound(Bitmap soundImage, String soundName) {

        if(soundImage == null) {
            soundImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            soundImage.eraseColor(Color.WHITE);
        }
        try {
            JSONObject soundObject = configObject.getJSONObject("Board").getJSONObject("soundList");
            JSONArray soundNames = soundObject.names();
            String lastName = soundNames.get(soundNames.length() - 1).toString();
            int lastInt = Integer.parseInt(lastName.replace("sound", ""));
            String lastSoundName = "sound" + String.valueOf(lastInt + 1);


            if(soundImage == null)
                return;
            String bitmapName = lastSoundName + ".png";
            File outputFile = new File(SOUNDBOARD_PATH + bitmapName);
            outputFile.createNewFile();
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(outputFile);
            soundImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            String soundFilePath = lastSoundName + ".mp3";

            File from = new File(SOUNDBOARD_PATH,"tmp.mp3");
            File to = new File(SOUNDBOARD_PATH, soundFilePath);
            if(from.exists())
                from.renameTo(to);

            JSONObject newSound = new JSONObject();
            newSound.put("soundName", soundName);
            newSound.put("soundImagePath", bitmapName);
            newSound.put("soundFilePath", soundFilePath);
            soundObject.put(lastSoundName, newSound);
            configObject.getJSONObject("Board").put("soundList", soundObject);
            save();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }
}
