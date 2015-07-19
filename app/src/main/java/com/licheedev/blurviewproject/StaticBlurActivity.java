package com.licheedev.blurviewproject;

import android.os.Bundle;
import android.widget.ImageView;

import com.licheedev.blurview.BlurView;

public class StaticBlurActivity extends BaseTestActivity {

    private ImageView mIvSrc;
    private BlurView mBlurView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_blur);
        initControl();
        initViews();
    }

    private void initViews() {
        mIvSrc = (ImageView) findViewById(R.id.ivSrc);
        mBlurView = (BlurView) findViewById(R.id.blurView);
        mBlurView.setToBlurView(mIvSrc); // 并联需要模糊的视图
        mIvSrc.post(new Runnable() {
            @Override
            public void run() {
                // 初始化完毕，更新模糊视图状态
                updateBlurView();
            }
        });
    }

    @Override
    protected void updateBlurView(int blurRadius, int downSample, int color) {
        mBlurView.blur(blurRadius, downSample, color);
    }

}
