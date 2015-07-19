package com.licheedev.blurviewproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public abstract class BaseTestActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvBlurRadius;
    private SeekBar mSbBlurRadius;
    private TextView mTvDownSample;
    private SeekBar mSbDownSample;
    private View mColorView;
    private Button mBtnColor;

    protected int mBlurRadius = 0;
    protected int mDownSample = 1;
    protected int mColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initControl() {
        // 设置模糊半径
        mTvBlurRadius = (TextView) findViewById(R.id.tvBlurRadius);
        mSbBlurRadius = (SeekBar) findViewById(R.id.sbBlurRadius);
        mSbBlurRadius.setMax(24);
        mSbBlurRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBlurRadiusText(progress);
                updateBlurView(mBlurRadius, mDownSample, mColor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSbBlurRadius.setProgress(0);
        // 设置缩小样本因数 
        mTvDownSample = (TextView) findViewById(R.id.tvDownSample);
        mSbDownSample = (SeekBar) findViewById(R.id.sbDownSample);
        mSbDownSample.setMax(64 - 1);
        mSbDownSample.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setDownSampleText(progress);
                updateBlurView(mBlurRadius, mDownSample, mColor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSbDownSample.setProgress(0);
        // 设置颜色
        mColorView = findViewById(R.id.colorView);
        mBtnColor = (Button) findViewById(R.id.btnColor);
        mBtnColor.setOnClickListener(this);
        setColorText(getResources().getColor(R.color.green_a20));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnColor:
                selectColor();
                break;

        }
    }

    private void selectColor() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("选择颜色")
                .initialColor(mColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        Toast.makeText(getApplicationContext(),
                                "已选择颜色: #" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .setPositiveButton("确定", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor,
                                        Integer[] allColors) {
                        setColorText(selectedColor);
                        updateBlurView(mBlurRadius, mDownSample, mColor);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }


    protected void setBlurRadiusText(int blurRadius) {
        mBlurRadius = blurRadius;
        mTvBlurRadius.setText("模糊半径:" + mBlurRadius);
    }

    protected void setDownSampleText(int downSample) {
        mDownSample = downSample + 1;
        mTvDownSample.setText("样本因数:" + mDownSample);
    }

    protected void setColorText(int color) {
        mColor = color;
        mBtnColor.setText("颜色:#" + Integer.toHexString(color));
        mColorView.setBackgroundColor(mColor);
    }

    /**
     * 更新模糊
     *
     * @param blurRadius
     * @param downSample
     * @param color
     */
    protected abstract void updateBlurView(int blurRadius, int downSample, int color);

    protected void updateBlurView() {
        updateBlurView(mBlurRadius, mDownSample, mColor);
    }

}
