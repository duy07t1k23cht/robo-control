package com.example.robocontrol.ui.voicecontrol;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.example.robocontrol.R;
import com.example.robocontrol.base.BasePresenter;
import com.example.robocontrol.utils.BluetoothUtils;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.robocontrol.ui.voicecontrol.VoiceControlContract.REQUEST_SPEECH_INPUT;

/**
 * Created by Duy M. Nguyen on 5/23/2020.
 */
public class VoiceControlPresenter extends BasePresenter<VoiceControlContract.View> implements VoiceControlContract.Presenter, RecognitionListener {

    private SpeechRecognizer speech = null;

    @Override
    public void createSpeechRecognizer(Context context) {
        speech = SpeechRecognizer.createSpeechRecognizer(context);
        speech.setRecognitionListener(this);
    }

    @Override
    public void startListening() {
        Log.d("__VOICE__", "startListening");
        if (speech != null) {
            mView.toggleListening(true);

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi");
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            mView.displaySpeechText("Listening...");

            speech.startListening(intent);
        }
    }

    @Override
    public void stopListening() {
        mView.animateRms(0f);
        Log.d("__VOICE__", "stopListening");
        if (speech != null) {
            mView.toggleListening(false);
            speech.stopListening();
        }
    }

    @Override
    public void releaseSpeechRecognizer() {
        if (speech != null) {
            speech.destroy();
        }
    }

    // RecognitionListener methods
    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        mView.animateRms(rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d("__VOICE__", "onEndOfSpeech");
        stopListening();
    }

    @Override
    public void onError(int error) {
        Log.d("__VOICE__", "onError");
        mView.showMessage(getErrorText(error));
        mView.displaySpeechText("");
    }

    @Override
    public void onResults(Bundle results) {
        Log.d("__VOICE__", "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = matches != null ? matches.get(0) : "";
        mView.displaySpeechText(text.trim());
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
