package com.example.robocontrol.ui.voicecontrol;

import android.content.Context;

import com.example.robocontrol.base.BaseView;

/**
 * Created by Duy M. Nguyen on 5/23/2020.
 */
public class VoiceControlContract {

    public final static int REQUEST_RECORD_PERMISSION = 123;

    interface View extends BaseView {
        void startListening();

        void showMessage(String message);

        void displaySpeechText(String text);
    }

    interface Presenter {
        void setupBluetoothConnectListener();

        void createSpeechRecognizer(Context context);

        void setupRecognizer(Context context);

        void startListening();

        void stopListening();

        void releaseSpeechRecognizer();
    }

    interface Indicator {

    }
}
