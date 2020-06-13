package com.example.robocontrol.ui.voicecontrol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.robocontrol.R;
import com.example.robocontrol.base.BaseActivity;

import java.util.ArrayList;

import static com.example.robocontrol.utils.ViewUtils.toast;

public class VoiceControlActivity extends BaseActivity<VoiceControlPresenter> implements VoiceControlContract.View, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImageView ivBack, ivListen, ivRms;
    private ToggleButton toggleSpeak;
    private TextView tvInput;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_voice_control;
    }

    @Override
    protected VoiceControlPresenter initPresenter() {
        return new VoiceControlPresenter();
    }

    @Override
    public void initViewComponents() {
        ivBack = findViewById(R.id.iv_back);
        ivListen = findViewById(R.id.iv_listen);
        ivRms = findViewById(R.id.iv_rms);
        toggleSpeak = findViewById(R.id.toggle_speak);
        tvInput = findViewById(R.id.tv_input);
    }

    @Override
    public void implementClickListener() {
        ivBack.setOnClickListener(this);
        toggleSpeak.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter.createSpeechRecognizer(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case VoiceControlContract.REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.startListening();
                } else {
                    showMessage("Permission Denied!");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case VoiceControlContract.REQUEST_SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result != null)
                        displaySpeechText(result.get(0));
                    else
                        displaySpeechText("Null result");
                }
                break;
        }
    }

    @Override
    public void showMessage(String message) {
        toast(this, message);
    }

    @Override
    public void displaySpeechText(String text) {
        tvInput.setText(text);
    }

    @Override
    public void toggleListening(boolean isListening) {
        toggleSpeak.setOnCheckedChangeListener(null);
        if (isListening) {
            ivListen.setImageDrawable(getDrawable(R.drawable.ic_voice_big_listening));
            if (!toggleSpeak.isChecked()) toggleSpeak.setChecked(true);
        } else {
            ivListen.setImageDrawable(getDrawable(R.drawable.ic_voice_big));
            if (toggleSpeak.isChecked()) toggleSpeak.setChecked(false);
        }
        toggleSpeak.setOnCheckedChangeListener(this);
    }

    @Override
    public void animateRms(float rmsdB) {
        float dB = rmsdB > 0 ? rmsdB : 0;
        ivRms.setScaleX((100 + dB * 8) / 100);
        ivRms.setScaleY((100 + dB * 8) / 100);
    }

    @Override
    public void checkRecordPermission() {
        ActivityCompat.requestPermissions(
                VoiceControlActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                VoiceControlContract.REQUEST_RECORD_PERMISSION
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.releaseSpeechRecognizer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // Button back
            case R.id.iv_back:
                onBackPressed();
                break;

            // Button listen
            case R.id.iv_listen:
                checkRecordPermission();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            checkRecordPermission();
        } else {
            mPresenter.stopListening();
        }
    }
}
