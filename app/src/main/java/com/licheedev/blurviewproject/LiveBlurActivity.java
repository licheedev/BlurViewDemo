package com.licheedev.blurviewproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;

import com.licheedev.blurview.PartBlurView;
import com.licheedev.blurviewproject.fragment.ImageFragment;
import com.licheedev.blurviewproject.fragment.ListFragment;
import com.licheedev.blurviewproject.fragment.ScrollFragment;
import com.licheedev.blurviewproject.listener.ToBlurListener;

public class LiveBlurActivity extends BaseTestActivity implements ToBlurListener {

    private ViewPager mViewPager;
    private PartBlurView mBlurView1;
    private PartBlurView mBlurView2;
    private FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_blur);
        initControl();
        initViews();
        
    }

    private void initViews() {
        mContainer = (FrameLayout) findViewById(R.id.container);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                toBlur();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mBlurView1 = (PartBlurView) findViewById(R.id.blurView1);
        mBlurView1.setToBlurView(mContainer);
        mBlurView2 = (PartBlurView) findViewById(R.id.blurView2);
        mBlurView2.setToBlurView(mContainer);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mBlurView1.blur();
                updateBlurView();
            }
        });
    }

    @Override
    protected void updateBlurView(int blurRadius, int downSample, int color) {
        mBlurView2.blur(blurRadius, downSample, color);
    }
    
    private class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ImageFragment.getInstance(R.drawable.img_dog1);
                case 1:
                    return ImageFragment.getInstance(R.drawable.img_dog2);
                case 2:
                    return ImageFragment.getInstance(R.drawable.img_dog3);
                case 3:
                    return new ListFragment();
                case 4:
                    return new ScrollFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }


    @Override
    public void toBlur() {
        mBlurView1.blur();
        mBlurView2.blur();
    }
}
