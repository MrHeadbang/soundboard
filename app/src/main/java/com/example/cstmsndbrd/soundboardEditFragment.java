package com.example.cstmsndbrd;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class soundboardEditFragment extends Fragment {
    private LinearLayout soundboard_edit_addsound;
    private int REQ_CODE_PICK_SOUNDFILE = 1;
    private globals globals = new globals();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.soundboard_edit_fragment, container, false);
        soundboard_edit_addsound = view.findViewById(R.id.soundboard_edit_addsound);


        soundboard_edit_addsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg");
                startActivityForResult(Intent.createChooser(intent, "Select Audio File"), REQ_CODE_PICK_SOUNDFILE);
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_SOUNDFILE && resultCode == Activity.RESULT_OK){
            if ((data != null) && (data.getData() != null)){
                Uri audioFileUri = data.getData();

                Bundle bundle = new Bundle();
                bundle.putString("audioUri", audioFileUri.toString());
                Fragment soundCut = new soundCut();
                soundCut.setArguments(bundle);
                globals.setFragment(getActivity(), soundCut, "soundCut");
            }
        }
    }
}
