package com.example.robocontrol.ui.voicecontrol;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.example.robocontrol.base.BasePresenter;
import com.example.robocontrol.utils.BluetoothUtils;
import com.example.robocontrol.utils.CommandUtils;

import java.util.ArrayList;

/**
 * Created by Duy M. Nguyen on 5/23/2020.
 */
public class VoiceControlPresenter extends BasePresenter<VoiceControlContract.View> implements VoiceControlContract.Presenter, RecognitionListener {

    private SpeechRecognizer speech = null;

    private String command = "";
    private String commandCode = "";

    @Override
    public void createSpeechRecognizer(Context context) {
        speech = SpeechRecognizer.createSpeechRecognizer(context);
        speech.setRecognitionListener(this);
    }

    @Override
    public void resetCommand() {
        command = "";
        commandCode = "";
        mView.displayCommand(command);
        mView.displaySpeechText("");
    }

    @Override
    public void startListening() {
        Log.d("__VOICE__", "startListening");
        if (speech != null) {
            mView.toggleListening(true);

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi");
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            speech.startListening(intent);
        }
    }

    @Override
    public void stopListening() {
        Log.d("__VOICE__", "stopListening");
        if (speech != null) {
            mView.toggleListening(false);
            speech.stopListening();
        }
        mView.animateRms(0f);
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
    }

    @Override
    public void onResults(Bundle results) {
        Log.d("__VOICE__", "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = matches != null ? matches.get(0) : "";
        mView.displaySpeechText(text.trim());

        command += text.trim() + " ";
        commandCode += CommandUtils.toSingleCommand(text.trim());

        mView.displayCommand(command);
        mView.showMessage(commandCode);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void executeCommand() {
        String cmd = CommandUtils.toSingleCommand(command);
        mView.showMessage(cmd);
        for (char character : cmd.toCharArray()) {
            BluetoothUtils.sendMessage(character);
        }
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
