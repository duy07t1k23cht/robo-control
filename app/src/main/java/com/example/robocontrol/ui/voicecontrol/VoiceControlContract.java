package com.example.robocontrol.ui.voicecontrol;

import android.app.Activity;
import android.content.Context;

import com.example.robocontrol.base.BaseView;

/**
 * Created by Duy M. Nguyen on 5/23/2020.
 */
public class VoiceControlContract {

    public final static int REQUEST_RECORD_PERMISSION = 123;
    public final static int REQUEST_SPEECH_INPUT = 124;

    interface View extends BaseView {
        void checkRecordPermission();

        void showMessage(String message);

        void displaySpeechText(String text);

        void toggleListening(boolean isListening);
    }

    interface Presenter {

        void createSpeechRecognizer(Context context);

        void startListening();

        void stopListening();

        void releaseSpeechRecognizer();

    }
}
