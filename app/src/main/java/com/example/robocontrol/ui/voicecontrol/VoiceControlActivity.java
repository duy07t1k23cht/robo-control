package com.example.robocontrol.ui.voicecontrol;

import android.view.View;
import android.widget.ImageView;

import com.example.robocontrol.R;
import com.example.robocontrol.base.BaseActivity;

public class VoiceControlActivity extends BaseActivity<VoiceControlPresenter> implements VoiceControlContract.View, View.OnClickListener {

    private ImageView ivBack;

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
    }

    @Override
    public void implementClickListener() {
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // Button back
            case R.id.iv_back:
                onBackPressed();
                break;

        }
    }
}
