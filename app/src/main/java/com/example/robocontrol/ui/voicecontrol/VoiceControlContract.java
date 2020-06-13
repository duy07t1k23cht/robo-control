package com.example.robocontrol.ui.voicecontrol;

import android.app.Activity;
import android.content.Context;

import com.example.robocontrol.base.BaseView;

/**
 * Created by Duy M. Nguyen on 5/23/2020.
 */
public class VoiceControlContract {

    public final static int REQUEST_RECORD_PERMISSION = 123;

    interface View extends BaseView {
        void checkRecordPermission();

        void showMessage(String message);

        void displaySpeechText(String text);

        void displayCommand(String command);

        void toggleListening(boolean isListening);

        void animateRms(float rmsdB);
    }

    interface Presenter {

        void createSpeechRecognizer(Context context);

        void startListening();

        void stopListening();

        void releaseSpeechRecognizer();

        void resetCommand();

        void executeCommand();

    }
}
