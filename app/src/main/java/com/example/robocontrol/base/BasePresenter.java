package com.example.robocontrol.base;

import android.view.View;

/**
 * Created by Duy M. Nguyen on 5/14/2020.
 */
public class BasePresenter<V extends BaseView> {
    protected V mView;

    void attachView(V view) {
        mView = view;
    }

    void detachView() {
        mView = null;
    }
}
