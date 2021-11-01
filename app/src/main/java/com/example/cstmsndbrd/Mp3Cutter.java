package com.example.cstmsndbrd;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;

public class Mp3Cutter {
    public Context context;
    public float start;
    public float end;
    public String mp3FilePath;
    public String outputPath;
    public Mp3Cutter(Context context) {
        this.context = context;
    }
    public void cut() {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.AlertDialog);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        String command = "-y -i '" + mp3FilePath + "' -ss " + start + " -to " + end + " -c copy " + outputPath;

        long executionId = FFmpeg.executeAsync(command, new ExecuteCallback() {
            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    progressDialog.dismiss();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });
    }

}
