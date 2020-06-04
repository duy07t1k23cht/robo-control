package com.example.robocontrol.ui.voicecontrol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.robocontrol.R;
import com.example.robocontrol.base.BaseActivity;

import java.util.ArrayList;

import static com.example.robocontrol.utils.ViewUtils.toast;

public class VoiceControlActivity extends BaseActivity<VoiceControlPresenter> implements VoiceControlContract.View, View.OnClickListener {

    private ImageView ivBack, ivListen;
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
        toggleSpeak = findViewById(R.id.toggle_speak);
        tvInput = findViewById(R.id.tv_input);
    }

    @Override
    public void implementClickListener() {
        ivBack.setOnClickListener(this);

        toggleSpeak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ivListen.setImageDrawable(getDrawable(R.drawable.ic_voice_big_listening));
                    startListening();
                } else {
                    ivListen.setImageDrawable(getDrawable(R.drawable.ic_voice_big));
                    mPresenter.stopListening();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter.createSpeechRecognizer(this);
        mPresenter.setupRecognizer(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case VoiceControlContract.REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.startListening(VoiceControlActivity.this);
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
                mPresenter.stopListening();
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
    public void startListening() {
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

            case R.id.toggle_speak:

                break;

        }
    }
}
