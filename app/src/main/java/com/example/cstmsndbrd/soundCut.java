package com.example.cstmsndbrd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class soundCut extends Fragment {
    private String audioPath = "", boardPath = "";
    private RangeSlider slider, slider_milli_left, slider_milli_right;
    private MediaPlayer mediaPlayer;
    private LinearLayout cut_play, cut_sound;
    private TextView cut_left_time, cut_right_time, cut_path, play_text;
    private float milliSeconds, seconds, left_time, right_time;
    private ImageView play_image;
    private boolean play_switcher = true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sound_cut, container, false);
        Bundle args = getArguments();
        audioPath = Objects.requireNonNull(args).getString("audioUri");
        boardPath = args.getString("boardPath");
        slider = view.findViewById(R.id.slider_multiple_thumbs);
        cut_play = view.findViewById(R.id.cut_play);
        cut_left_time = view.findViewById(R.id.cut_left_time);
        cut_right_time = view.findViewById(R.id.cut_right_time);
        slider_milli_left = view.findViewById(R.id.slider_milli_left);
        slider_milli_right = view.findViewById(R.id.slider_milli_right);
        cut_sound = view.findViewById(R.id.cut_sound);
        play_text = view.findViewById(R.id.play_text);
        cut_path = view.findViewById(R.id.cut_path);
        play_image = view.findViewById(R.id.play_image);
        Uri uri = Uri.parse(audioPath);
        cut_path.setText(getFileName(uri));
        mediaPlayer = MediaPlayer.create(getActivity(), uri);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getActivity(),uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        milliSeconds = Integer.parseInt(durationStr);
        seconds = milliSeconds / 1000;
        setSliderSpecs();
        setSeconds();

        cut_play.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if(play_switcher) {
                    playCuts(left_time, right_time, uri);
                    play_image.setImageResource(R.drawable.ic_baseline_stop_24);
                    play_text.setText("Stop");
                    play_switcher = false;
                } else {
                    mediaPlayer.stop();
                    play_image.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    play_text.setText("Play");
                    play_switcher = true;
                }
            }
        });
        setEditTextValues();
        slider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                setSeconds();
            }
        });
        slider_milli_right.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                setSeconds();
            }
        });
        slider_milli_left.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                setSeconds();
            }
        });
        cut_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mediaPlayer.stop();
                    File mp3File = FileUtils.getFileFromUri(getActivity(), uri);
                    String mp3FilePath = mp3File.getAbsolutePath();
                    Mp3Cutter mp3Cutter = new Mp3Cutter(getActivity());
                    List<Float> slider_values = slider.getValues();
                    mp3Cutter.mp3FilePath = mp3FilePath;
                    mp3Cutter.start = slider_values.get(0) + slider_milli_left.getValues().get(0) / 1000;
                    mp3Cutter.end = slider_values.get(1) + slider_milli_right.getValues().get(0) / 1000;
                    mp3Cutter.outputPath = boardPath + "tmp.mp3";
                    mp3Cutter.cut();
                    Intent intent = new Intent(getActivity(), soundCut.class);
                    intent.putExtra("cutSoundPath", mp3Cutter.outputPath);
                    intent.putExtra("soundName", getFileName(uri));
                    Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), 202, intent);
                    requireActivity().getSupportFragmentManager().popBackStack();
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(view, "Can't read files from external drives.", Snackbar.LENGTH_LONG).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        return view;
    }

    @SuppressLint("DefaultLocale")
    private void setSeconds() {
        List<Float> slider_values = slider.getValues();
        left_time = slider_values.get(0) + slider_milli_left.getValues().get(0) / 1000;
        right_time = slider_values.get(1) + slider_milli_right.getValues().get(0) / 1000;
        if (seconds <= 60) {
            cut_left_time.setText(String.format("%.3f", left_time));
            cut_right_time.setText(String.format("%.3f", right_time));
        } else {
            cut_left_time.setText(secondsToMinutes(left_time));
            cut_right_time.setText(secondsToMinutes(right_time));
        }
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @SuppressLint("DefaultLocale")
    private void setEditTextValues() {
        List<Float> slider_values = slider.getValues();
        if (seconds <= 60) {
            cut_left_time.setText(String.format("%.3f", slider_values.get(0)));
            cut_right_time.setText(String.format("%.3f", slider_values.get(1)));
        } else {
            cut_left_time.setText(secondsToMinutes(slider_values.get(0)));
            cut_right_time.setText(secondsToMinutes(slider_values.get(1)));
        }

    }
    @SuppressLint("DefaultLocale")
    private String secondsToMinutes(float secs) {
        int minutes = 0;
        while(secs >= 60) {
            secs -= 60;
            minutes++;
        }
        String secSet = String.valueOf(secs).split("\\.")[0];
        String millsecSet = String.valueOf(secs).split("\\.")[1];
        return String.valueOf(minutes) + ":" + fill(secSet) + "." + String.format("%.3s", millsecSet);
    }
    private String fill(String s) {
        String first = s.split(":")[0];
        return (first.length() == 2) ? first : "0" + first;
    }
    private void setSliderSpecs() {
        if(seconds <= 60) {
            slider.setValueTo((int)seconds);
            slider.setValues(0f, (float)(int)seconds);
            slider.setStepSize(1);

        } else {
            slider.setValueTo((int)seconds);
            slider.setValues(0f, (float)(int)seconds);
            slider.setStepSize(1);
            slider.setLabelFormatter(new LabelFormatter() {
                @SuppressLint("DefaultLocale")
                @NonNull
                @Override
                public String getFormattedValue(float value) {
                    int minutes = 0;
                    value = (int)value;
                    while(value >= 60) {
                        value -= 60;
                        minutes++;
                    }
                    return String.valueOf(minutes) + ":" + fill(String.format("%.0f", value));
                }
            });
        }
    }


    private void playCuts(float start, float end, Uri uri) {
        mediaPlayer = MediaPlayer.create(getActivity(), uri);
        mediaPlayer.seekTo((int) (start * 1000));
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                Handler playerHandler = new Handler();
                playerHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.stop();
                    }
                }, (long)(end - start) * 1000);
            }
        });
    }
}
