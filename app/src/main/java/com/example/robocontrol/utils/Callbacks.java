package com.example.robocontrol.utils;

/**
 * Created by Duy M. Nguyen on 5/14/2020.
 */
public interface Callbacks {
    interface VoidCallback {
        void execute();
    }

    interface BoolCallback {
        void execute(boolean b);
    }
}
