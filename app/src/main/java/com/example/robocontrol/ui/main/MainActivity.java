package com.example.robocontrol.ui.main;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.robocontrol.R;
import com.example.robocontrol.base.BaseActivity;
import com.example.robocontrol.navigation.Navigation;
import com.example.robocontrol.utils.AnimationHelpers;
import com.example.robocontrol.utils.Callbacks;

import static com.example.robocontrol.utils.ViewUtils.hide;
import static com.example.robocontrol.utils.ViewUtils.show;
import static com.example.robocontrol.utils.ViewUtils.toast;

public class MainActivity
        extends BaseActivity<MainPresenter>
        implements View.OnClickListener, MainContract.View {

    // View variables
    private ImageButton btnMore, btnHelp, btnInfo, btnSwitch, btnBluetooth, btnHorn, btnLight, btnDrive, btnSpeed, btnSwapVert, btnSwapHoriz, btnVoice;
    private TextView tvDrive, tvSpeed;
    private LinearLayout layoutControl, layoutSwap;
    private RelativeLayout layoutJoystick;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter.setupJoystick(layoutJoystick, R.drawable.bg_stick);
        mPresenter.checkBluetoothStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.setupBluetoothConnectListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainContract.ENABLE_BLUETOOTH_REQ) {
            if (resultCode == RESULT_OK) {
                toast(this, "Bluetooth is on");
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Error!")
                        .setMessage("We need to turn on bluetooth")
                        .setPositiveButton("TURN ON BLUETOOTH", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                enableBluetooth();
                            }
                        })
                        .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finishAffinity();
                            }
                        })
                        .show();
            }
        }

        if (requestCode == MainContract.CONNECT_DEVICE_REQ) {
            if (data != null) {
                String deviceAddress = data.getStringExtra(MainContract.DEVICE_ADDRESS);
                if (deviceAddress == null) {
                    toast(this, "Something went wrong");
                    return;
                }
                mPresenter.connectToDevice(deviceAddress);
            }
        }
    }

    @Override
    public void initViewComponents() {
        btnMore = findViewById(R.id.btn_more);
        btnHelp = findViewById(R.id.btn_help);
        btnInfo = findViewById(R.id.btn_info);
        btnSwitch = findViewById(R.id.btn_switch);
        btnVoice = findViewById(R.id.btn_voice_control);
        btnBluetooth = findViewById(R.id.btn_bluetooth);

        btnSwapHoriz = findViewById(R.id.btn_swap_horiz);
        btnSwapVert = findViewById(R.id.btn_swap_ver);

        btnDrive = findViewById(R.id.btn_drive);
        btnSpeed = findViewById(R.id.btn_speed);

        tvDrive = findViewById(R.id.tv_countdown_drive);
        tvSpeed = findViewById(R.id.tv_countdown_speed);

        btnHorn = findViewById(R.id.btn_horn);
        btnLight = findViewById(R.id.btn_light);

        layoutControl = findViewById(R.id.layout_control);
        layoutSwap = findViewById(R.id.layout_swap);

        layoutJoystick = findViewById(R.id.layout_joystick);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void implementClickListener() {
        btnMore.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);
        btnVoice.setOnClickListener(this);
        btnBluetooth.setOnClickListener(this);

        btnDrive.setOnClickListener(this);
        btnSpeed.setOnClickListener(this);

        btnSwapVert.setOnClickListener(this);
        btnSwapHoriz.setOnClickListener(this);

        btnHorn.setOnClickListener(this);
        btnLight.setOnClickListener(this);

        layoutJoystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mPresenter.processJoystickMovement(arg1);
                return true;
            }
        });
    }

    @Override
    public void showMessage(String message) {
        toast(this, message);
    }

    @Override
    public void enableBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, MainContract.ENABLE_BLUETOOTH_REQ);
    }

    @Override
    public void connectBluetooth() {
        // Go to list device to connect
        Navigation.toBluetoothConnecting(this, MainContract.CONNECT_DEVICE_REQ);
    }

    @Override
    public void updateBluetoothButton(boolean isConnect) {
        if (isConnect) {
            btnBluetooth.setImageResource(R.drawable.ic_bluetooth_disabled);
        } else {
            btnBluetooth.setImageResource(R.drawable.ic_bluetooth);
        }
    }

    @Override
    public void showMoreOption() {
        show(btnHelp);
        show(btnInfo);
        AnimationHelpers.rotate(btnMore, 0, 45);
        AnimationHelpers.leftIn(btnHelp, -100);
        AnimationHelpers.leftIn(btnInfo, -200);
    }

    @Override
    public void hideMoreOption() {
        AnimationHelpers.rotate(btnMore, 45, 0);
        hide(btnHelp);
        hide(btnInfo);
    }

    @Override
    public void showLayoutControl() {
        btnSwitch.setClickable(false);
        AnimationHelpers.rotate(btnSwitch, 90, 0);
        AnimationHelpers.fadeOut(layoutSwap, new Callbacks.VoidCallback() {
            @Override
            public void execute() {
                AnimationHelpers.fadeIn(layoutControl, new Callbacks.VoidCallback() {
                    @Override
                    public void execute() {
                        btnSwitch.setClickable(true);
                    }
                });
            }
        });
    }

    @Override
    public void showLayoutSwap() {
        btnSwitch.setClickable(false);
        AnimationHelpers.rotate(btnSwitch, 0, 90);
        AnimationHelpers.fadeOut(layoutControl, new Callbacks.VoidCallback() {
            @Override
            public void execute() {
                AnimationHelpers.fadeIn(layoutSwap, new Callbacks.VoidCallback() {
                    @Override
                    public void execute() {
                        btnSwitch.setClickable(true);
                    }
                });
            }
        });
    }

    @Override
    public void disableButtons(View view) {
        view.setClickable(false);
    }

    @Override
    public void enableButtons(View view) {
        view.setClickable(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // Button more
            case R.id.btn_more:
                mPresenter.showHideMoreOption();
                break;

            // Button switch mode
            case R.id.btn_switch:
                mPresenter.switchControlMode();
                break;

            // Button Voice control
            case R.id.btn_voice_control:
                Navigation.toVoiceControl(this);
                break;

            // Button bluetooth
            case R.id.btn_bluetooth:
                if (mPresenter.isConnected) {
                    mPresenter.disconnect();
                } else {
                    connectBluetooth();
                }
                break;

            // Button horn
            case R.id.btn_horn:
                mPresenter.sendMessage("T");
                break;

            // Button light
            case R.id.btn_light:
                mPresenter.sendMessage("Y");
                break;

            // Button drive
            case R.id.btn_drive:
                mPresenter.sendMessage("n");
                disableButtons(btnDrive);
                new CountDownTimer(2999, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tvDrive.setVisibility(View.VISIBLE);
                        tvDrive.setText(String.valueOf((int) (millisUntilFinished / 1000) + 1));
                    }

                    @Override
                    public void onFinish() {
                        enableButtons(btnDrive);
                        tvDrive.setVisibility(View.GONE);
                    }
                }.start();
                break;

            // Button speed
            case R.id.btn_speed:
                mPresenter.sendMessage("N");
                disableButtons(btnSpeed);
                new CountDownTimer(2999, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tvSpeed.setVisibility(View.VISIBLE);
                        tvSpeed.setText(String.valueOf((int) (millisUntilFinished / 1000) + 1));
                    }

                    @Override
                    public void onFinish() {
                        enableButtons(btnSpeed);
                        tvSpeed.setVisibility(View.GONE);
                    }
                }.start();
                break;

            // Button Swap vert
            case R.id.btn_swap_ver:
                mPresenter.sendMessage("U");
                break;

            // Button Swap horizontal
            case R.id.btn_swap_horiz:
                mPresenter.sendMessage("O");
                break;
        }
    }
}
